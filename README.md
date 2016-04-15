| Branch | Build Status                                                                                                     |
| ------ |:-----------------------------------------------------------------------------------------------------------------|
| Master | [![Master Build status][master build]][travis link] [![Master Coverage Status][master coverage]][coveralls link] |
| Dev    | [![Dev Build status][dev build]][travis link] [![Dev Coverage Status][dev coverage]][coveralls link]             |

[travis link]: https://travis-ci.org/chdir/aidl2
[coveralls link]: https://coveralls.io/github/chdir/aidl2
[master build]: https://travis-ci.org/chdir/aidl2.svg?branch=master
[dev build]: https://travis-ci.org/chdir/aidl2.svg?branch=dev
[master coverage]: https://coveralls.io/repos/github/chdir/aidl2/badge.svg?branch=master
[dev coverage]: https://coveralls.io/repos/github/chdir/aidl2/badge.svg?branch=dev

### AIDL2

This is a replacement for Google's AIDL tool. Unlike it's predecessor, which parses extremely
limited DSL, aidl2 uses Java annotation processing framework to generate Java implementation,
based on Java interface code. This allows you to extend from other interfaces,
annotate your interface classes, use generics and other features of Java language.

### Current status

This project is in early alpha. It is not nearly as well-tested as original aidl tool, but
most features are there. If you decide to use it, make sure to read the generated code, to
avoid any unpleasant surprises.

### Usage

1. Add the library to project:

    ```groovy
    repositories {
        maven { url 'http://dl.bintray.com/alexanderr/maven' }
    }

    dependencies {
        compile 'net.sf.aidl2:compiler:0.0.1'
    }
    ```

2. Write an interface with required methods. It must extend `android.os.IInterface`. Furthermore,
each method must declare `android.os.RemoteException` to be thrown.
Annotate the interface with `@AIDL`.

    ```java
    @AIDL
    public interface RemoteApi extends IInterface {
        String sayHello() throws RemoteException;
    }
    ```

3. Retrive generated proxy/stub using `InterfaceLoader`.

    Service code:

    ```java
    public IBinder onBind(Intent intent) {
      RemoteApi serviceApi = new RemoteApi() {
        public String sayHello() {
          return "Hello world";
        }

        public IBinder asBinder() {
          return InterfaceLoader.asBinder(this, RemoteApi.class);
        }
      };
      return serviceApi.asBinder();
    }
    ```

    Caller code:

    ```java
    public void onServiceConnected(ComponentName name, IBinder serviceBinder) {
      try {
        final RemoteApi serviceApi = InterfaceLoader.asInterface(serviceBinder, RemoteApi.class);
    
        final String callResult = serviceApi.sayHello();

        Toast.makeText(this, "Received message \"" + callResult + '"', Toast.LENGTH_SHORT).show();
      } catch (RemoteException e) {
        Toast.makeText(this, "Failed to receive a string " + e.getMessage(), Toast.LENGTH_SHORT).show();
      }
    }
    ```

### Defining an interface

You can specify any number of parameters, including varargs. Parameters and
return values must be of types, supported by `android.os.Parcel`. You can use tricky
generic if you want, but make sure, that those can be interpreted as one of supported
types:

```java
@AIDL
public interface RemoteApi extends IInterface {
    <T extends Runnable> void exampleMethod(T runnableParameter) throws RemoteException;
}
```

will fail to compile with error, because Parcel does not support Runnables.

```java
@AIDL
public interface RemoteApi extends IInterface {
    <T extends Runnable & Parcelable> void exampleMethod(T runnableParameter) throws RemoteException;
}
```

will compile and generate code, using `writeParcelable`  and `readParcelable` to pass "runnableParameter"
between processes.

```java
@AIDL
public interface RemoteApi extends IInterface {
    void exampleMethod(Date dateParameter) throws RemoteException;
}
```

will compile and use `readSerializable` and `writeSerializable` to passs "dateParameter" between processes.

### Differences with Android aidl tool

In additiona to types, supported by aidl tool AIDL2 allows use of

* Multi-dimensional arrays
* Void — ignored during (de)serialization always evaluate to null
* Externalizable/Serializable

### Limitations/Known Issues

* JDK 6 *may* work, but isn't supported, since it is no longer supported by it's
creator and developers of Javapoet.

* Passing invalid expressions as annotation parameters may not be correctly detected
during validation step and results in runtime exception during compilation with Javac.

    Example:

    ```java
    // will fail to compile with ugly message, asking you to send a bug report :(
    @AIDL(SomeNonExistingType.DESCRIPTOR)
    ```

    This is due to limitations of Javac, which converts erroneous "types" to strings
    before passing them to AnnotationValueVisitor. This also happens with other annotation
    processors, such as ButterKnife.

* Generated implementations of interfaces, containing intersection types, may fail to
compile with source versions before Java 8.

    ```java
    // this will fail to compile with JDK 7
    <T extends Parcelable & Runnable> void methodWithIntersectionArgument(T param);
    ```

    This is because implementing those requires support for either advanced type inference
    or casts to intersection, both of which didn't exist until Java 8.

* Some of generated files may contain redundant casts. This is due to limitation of
Java compilers, which sometimes fail to correctly tell annotation processors
whether a cast is needed (this tends to happen when generics are involved).

* Nonsential intersection types like `Boolean & Runnable` aren't detected.
I believe, that those should be handled by the compiler.
