package net.sf.aidl2;

import android.app.Service;
import android.content.ServiceConnection;
import android.os.*;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
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

    private static final Linker linker = getLinker();

    private static volatile boolean warned;

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

        if (linker != null) {
            final IBinder found = linker.newServer(server, aidlInterface);

            if (found != null) {
                return found;
            }
        } else {
            if (!warned) {
                Log.e(TAG, "Failed to find the linker class, make sure that your build system set up is correct");

                warned = true;
            }

            IBinder tried = FallbackLocator.loadServerViaFallback(aidlInterface, server);

            if (tried != null) {
                if (useVerboseLogging) {
                    Log.v(TAG, "Successfully located " + tried.getClass() + " via resource metadata");
                }

                return tried;
            }
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

        if (linker != null) {
            Z found = linker.newClient(binder, aidlInterface);

            if (found != null) {
                return found;
            }
        } else {
            if (!warned) {
                Log.e(TAG, "Failed to find the linker class, make sure that your build system set up is correct");

                warned = true;
            }

            Z tried = (Z) FallbackLocator.loadClientViaFallback(aidlInterface, interfaceName, binder);

            if (tried != null) {
                if (useVerboseLogging) {
                    Log.v(TAG, "Successfully located " + tried.getClass() + " via resource metadata");
                }

                return tried;
            }
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

    @SuppressWarnings("unchecked")
    private static Linker getLinker() {
        try {
            Class<? extends Linker> linkerClass = (Class<? extends Linker>)
                    Class.forName("net.sf.aidl2.LinkerImpl");

            return linkerClass.newInstance();
        } catch (Exception e) {
            if (useVerboseLogging) {
                Log.i(TAG, "Failed to load linker", e);
            }

            return null;
        }
    }

    private static class FallbackLocator {
        private static final Set<String> pkgSet = new HashSet<>();

        private static final Map implMap = new HashMap<>();

        private static final Map<String, Constructor<?>> classes = new ConcurrentHashMap<>();

        @SuppressWarnings("unchecked")
        private static @NotNull <T> Constructor<T> getConstructor(String className, Class<?> argType) throws ClassNotFoundException, NoSuchMethodException {
            final Class<? extends IBinder> simplyFound = (Class<? extends IBinder>) Class.forName(className);

            return (Constructor<T>) simplyFound.getConstructor(argType);
        }

        @SuppressWarnings("unchecked")
        private static @Nullable String getClassImplName(Class<?> clazz, String implType) {
            final String packageName = clazz.getPackage().getName();

            final String className = clazz.getName();

            final ClassLoader loader = FallbackLocator.class.getClassLoader();

            synchronized (FallbackLocator.class) {
                if (!pkgSet.contains(packageName)) {
                    final String metadataPath = packageName.replace('.', '/') + "/aidl2.properties";

                    try {
                        final Enumeration<URL> res = loader.getResources(metadataPath);

                        if (!res.hasMoreElements()) {
                            Log.e(TAG, "Unable to find " + metadataPath + " in resources using " + loader);

                            return null;
                        }

                        final Properties properties = new Properties();

                        while (res.hasMoreElements()) {
                            try (InputStream stream = res.nextElement().openStream()) {
                                properties.load(stream);
                            }
                        }

                        implMap.putAll(properties);
                    } catch (IOException e) {
                        Log.e(TAG, "IO error during reading resource file " + metadataPath + " with " + loader, e);

                        return null;
                    }

                    pkgSet.add(packageName);
                }
            }

            final Object found = implMap.get(className + implType);

            if (found != null) {
                return found.toString();
            }

            throw new IllegalStateException("Metadata file found, but does not have entry for " + clazz);
        }

        public static IInterface loadClientViaFallback(Class<? extends IInterface> clazz, String interfaceName, IBinder client) {
            final String name = clazz.getName();

            final String implType = "$$AidlClientImpl";

            String serverImplClassName = name + implType;

            Constructor constructor = classes.get(serverImplClassName);

            try {
                if (constructor == null) {
                    try {
                        constructor = getConstructor(serverImplClassName, IBinder.class);
                    } catch (ClassNotFoundException ignored) {
                        final String renamedImpl = getClassImplName(clazz, implType);

                        if (renamedImpl == null) {
                            Log.e(TAG, "Failed to find IInterface implementation for " + clazz);

                            return null;
                        }

                        serverImplClassName = renamedImpl;

                        constructor = getConstructor(serverImplClassName, IBinder.class);
                    }

                    classes.put(serverImplClassName, constructor);
                }

                checkVersion(client, interfaceName, constructor.getDeclaringClass());

                return (IInterface) constructor.newInstance(client);
            } catch (NoSuchMethodException ignored) {
                throw new RuntimeException(
                        "Class \"" + serverImplClassName + "\" exists, but does not contain " +
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

        public static <Y extends IInterface> IBinder loadServerViaFallback(Class<Y> clazz, Y server) {
            final String name = clazz.getName();

            final String implType = "$$AidlServerImpl";

            String serverImplClassName = name + implType;

            Constructor constructor = classes.get(serverImplClassName);

            try {
                if (constructor == null) {
                    try {
                        constructor = getConstructor(serverImplClassName, clazz);
                    } catch (ClassNotFoundException ignored) {
                        final String renamedImpl = getClassImplName(clazz, implType);

                        if (renamedImpl == null) {
                            Log.e(TAG, "Failed to find Binder implementation for " + clazz);

                            return null;
                        }

                        serverImplClassName = renamedImpl;

                        constructor = getConstructor(serverImplClassName, clazz);
                    }

                    classes.put(serverImplClassName, constructor);
                }

                return (IBinder) constructor.newInstance(server);
            } catch (NoSuchMethodException ignored) {
                throw new RuntimeException(
                        "Class \"" + serverImplClassName + "\" exists, but does not contain " +
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

        private static void checkVersion(IBinder rpc, String interfaceName, Class<?> proxy) throws IllegalAccessException, RemoteException {
            Field[] fields = proxy.getDeclaredFields();

            for (Field field : fields) {
                if (field.getType() == long.class) {
                    field.setAccessible(true);

                    final long localRpcVersion;
                    final long interfaceRpcVer;

                    localRpcVersion = field.getLong(null);

                    Parcel req = Parcel.obtain();
                    Parcel resp = Parcel.obtain();
                    try {
                        req.writeString(interfaceName);

                        if (!rpc.transact(AidlUtil.VERSION_TRANSACTION, req, resp, 0)) {
                            throw new VersionMismatch("Failed to get interface version from remote process");
                        }

                        interfaceRpcVer = resp.readLong();
                    } finally {
                        req.recycle();
                        resp.recycle();
                    }

                    if (interfaceRpcVer != localRpcVersion) {
                        throw new VersionMismatch("RPC interface version mismatch: local is " + localRpcVersion + " remote is " + interfaceRpcVer);
                    }
                }
            }
        }
    }
}
