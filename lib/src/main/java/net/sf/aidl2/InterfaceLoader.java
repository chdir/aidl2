package net.sf.aidl2;

import android.app.Service;
import android.content.ServiceConnection;
import android.os.*;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Static factory of proxy and stub classes. Offers a convenient way to instantiates implementations,
 * generated during annotation processing. Together proxy and stub wrap a lower-level IPC channel, represented by
 * {@link Binder#transact}.
 *
 * <br>
 *
 * Proxy is a client-side implementation of IInterface, used by Service clients to perform IPC. It serializes
 * method arguments, passes them to {@link Binder#transact} method of wrapped Binder object and deserializes
 * a returned value.
 *
 * <br>
 *
 * Stub is a subclass of Binder, that overrides {@link Binder#onTransact} and dispatches interprocess calls to
 * Service-side implementation of RPC interface. It is commonly returned from {@link Service#onBind} method.
 *
 * <br>
 *
 * This class supports short-circuiting between client and Service, located in the same process — in such
 * case a value, returned from {@link #asInterface} will be a Service-side implementation itself instead of proxy.
 *
 * <br>
 *
 * Note, that Android framework purposefully makes semantics of Binder garbage-collections same for IPC and
 * local use-cases. That is — as long as you hold a reference to client Binder object, a corresponding Binder
 * instance in remote process will NOT be garbage-collected.
 */
public final class InterfaceLoader {
    private static final String PROP_VERBOSE = "net.sf.aidl2.verbose";

    private static final boolean useVerboseLogging;

    static {
        useVerboseLogging = Boolean.valueOf(System.getProperty(PROP_VERBOSE));
    }

    private static final String TAG = "AIDL2";

    /**
     * Export provided interface implementation for inter-process access.
     *
     * Corresponding interface must have been subjected to compile-time annotation processing
     * by AIDL2 processor.
     *
     * @param server interface implementation
     * @param aidlInterface interface being published
     * @param <Z> the class of interface, being published
     *
     * @return IPC handle for implemented interface, suitable for returning from {@link Service#onBind}
     */
    public static @NotNull <Z extends IInterface> IBinder asBinder(@NotNull Z server, @NotNull Class<Z> aidlInterface) {
        if (server == null) {
            throw new IllegalArgumentException("server == null");
        }

        if (aidlInterface == null) {
            throw new IllegalArgumentException("aidlInterface == null");
        }

        IBinder tried = FallbackLocator.loadServerViaFallback(aidlInterface, server);

        if (tried != null) {
            if (useVerboseLogging) {
                Log.v(TAG, "Successfully located " + tried.getClass() + " via resource metadata");
            }

            return tried;
        }

        makeMotions(aidlInterface);

        throw new IllegalStateException("Failed to find generated implementation of " + aidlInterface);
    }

    /**
     * Wrap provided binder for type-safe access.
     *
     * @param binder IPC primitive, such as passed to {@link ServiceConnection#onServiceConnected}
     * @param aidlInterface interface being requested, must have undergone compile-time annotation processing with AIDL2
     * @param <Z> the class of interface, being requested
     *
     * @return implementation of requested IPC interface
     *
     * @throws RemoteException if IPC has failed for any reason (remote process dead, exceeded transaction size, version mismatch etc.)
     */
    @SuppressWarnings("unchecked")
    public static @NotNull <Z extends IInterface> Z asInterface(@NotNull IBinder binder, @NotNull Class<Z> aidlInterface) throws RemoteException {
        if (binder == null) {
            throw new IllegalArgumentException("binder == null");
        }

        if (aidlInterface == null) {
            throw new IllegalArgumentException("aidlInterface == null");
        }

        final String interfaceName = binder.getInterfaceDescriptor();

        final IInterface localInterface = binder.queryLocalInterface(interfaceName);

        if (localInterface != null) {
            return (Z) localInterface;
        }

        Z tried = (Z) FallbackLocator.loadClientViaFallback(aidlInterface, interfaceName, binder);

        if (tried != null) {
            if (useVerboseLogging) {
                Log.v(TAG, "Successfully located " + tried.getClass() + " via resource metadata");
            }

            return tried;
        }

        makeMotions(aidlInterface);

        throw new IllegalStateException("Failed to find generated implementation of " + aidlInterface);
    }

    private static void makeMotions(Class<?> datClass) {
        if (!datClass.isInterface()) {
            throw new IllegalArgumentException("Received " + datClass + ", but an interface was expected!");
        }

        if ((Modifier.PUBLIC & datClass.getModifiers()) == 0) {
            throw new IllegalArgumentException(datClass.getCanonicalName() + " does not look like @AIDL interface: not public");
        }

        if (datClass.getAnnotation(AIDL.class) == null) {
            throw new IllegalArgumentException("Failed to find generated implementation of " + datClass +
                    ", are you sure, that it is annotated with @AIDL?");
        }
    }

    public static final class FallbackLocator {
        private static final Map<String, Constructor<?>> clients = new ConcurrentHashMap<>();
        private static final Map<String, Constructor<?>> servers = new ConcurrentHashMap<>();

        @SuppressWarnings("unchecked")
        private static @NotNull <T> Constructor<T> getConstructor(String className, Class<?> argType) throws ClassNotFoundException, NoSuchMethodException {
            final Class<? extends IBinder> simplyFound = (Class<? extends IBinder>) Class.forName(className);

            Constructor<T> result = (Constructor<T>) simplyFound.getConstructor(argType);

            result.setAccessible(true);

            return result;
        }

        static IInterface loadClientViaFallback(Class<? extends IInterface> clazz, String interfaceName, IBinder client) {
            final String name = clazz.getName();

            final String implType = "$$AidlClientImpl";

            Constructor constructor = clients.get(name);

            try {
                if (constructor == null) {
                    String serverImplClassName = name + implType;

                    constructor = getConstructor(serverImplClassName, IBinder.class);

                    clients.put(name, constructor);
                }

                return (IInterface) constructor.newInstance(client);
            } catch (NoSuchMethodException ignored) {
                throw new RuntimeException(
                        "Class \"" + name + implType + "\" exists, but does not contain " +
                                "default constructor. Issues like this are sometimes caused by Proguard. " +
                                "Check your build system set up");
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "Failed to load IInterface implementation for " + clazz, e);

                return null;
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        static <Y extends IInterface> IBinder loadServerViaFallback(Class<Y> clazz, Y server) {
            final String name = clazz.getName();

            final String implType = "$$AidlServerImpl";

            Constructor constructor = servers.get(name);

            try {
                if (constructor == null) {
                    String serverImplClassName = name + implType;

                    constructor = getConstructor(serverImplClassName, clazz);

                    servers.put(name, constructor);
                }

                return (IBinder) constructor.newInstance(server);
            } catch (NoSuchMethodException ignored) {
                throw new RuntimeException(
                        "Class \"" + name + implType + "\" exists, but does not contain " +
                                "default constructor. Issues like this are sometimes caused by Proguard. " +
                                "Check your build system set up");
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "Failed to load Binder implementation for " + clazz, e);

                return null;
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
