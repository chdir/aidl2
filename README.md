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

This is a replacement for Google's aidl generator tool. Instead of parsing highly limited DSL this
one uses Java annotation processing framework to generate Java implementation, based on Java
interface code. This allows you to extend from other interfaces, annotate your interface classes,
use generics and other features of Java language.

### AIDL2 and Android aidl tool

Traditionally *"aidl"* refers to several related but distinct concepts:

* the [AIDL][1] interface definition language;
* .aidl files (which contain AIDL);
* the [aidl generator][2] which transforms AIDL into client/server IPC interfaces.

Unlike Google's code generator, which is implemented as standalone C++ executable, executed
by Android build system, AIDL2 is an annotation processor, that is — a plugin for
Java compiler. It has better error reporting and can be used with plain Java projects
as long as Android framework classes are on classpath. It works automatically by adding
jar file to project dependencies.

Better integration with compiler gives aidl2 complete understanding of involved types, which
makes it possible to support more types without resorting to runtime reflection. In addition
to types, supported by aidl tool, AIDL2 allows use of

* Generic Collections
* IInterface subtypes (both from AIDL2 and aidl tool)
* Externalizable/Serializable types
* Multi-dimensional arrays
* Void — ignored during (de)serialization, always evaluates to null

All generated code is human-readable and well-formatted. Annotating a method with `@SuppresWarnings`
will carry the annotation over to generated classes (please create an issue, if you'd like for
other annotations to be transplanted the same way).

Following features of aidl tool are not (yet?) supported:

* Pure `out` and `inout` parameters
* Generating C++ code

Aidl2 won't work with Google's Jack and other compilers incompatible with post-Java 5 language.
It is not tested for compatibility with Eclipse Java compiler, but any feedback is welcome.

### Current status

This project is in early alpha. It is not nearly as well-tested as original aidl tool, but
most features are there. If you decide to use it, make sure to read the generated code, to
avoid any unpleasant surprises.

### Usage

Add the library to project:

```groovy
repositories {
  maven { url 'http://dl.bintray.com/alexanderr/maven' }
}

dependencies {
  compile 'net.sf.aidl2:compiler:0.0.1'
}
```

Write an interface with required methods. It must extend `android.os.IInterface`. Furthermore,
each method must declare `android.os.RemoteException` to be thrown.
Annotate the interface with `@AIDL`.

```java
@AIDL
public interface RemoteApi extends IInterface {
  String sayHello() throws RemoteException;
}
```

Retrive generated proxy/stub using `InterfaceLoader`.

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
generic types if you want, but make sure, that those can be interpreted as one of supported
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
    <T extends Runnable & Parcelable> T exampleMethod() throws RemoteException;
}
```

will compile and generate code, using `writeParcelable`  and `readParcelable` to pass "runnableParameter"
between processes.

```java
@AIDL
public interface RemoteApi extends IInterface {
    void exampleMethod(Date[] dateArray) throws RemoteException;
}
```

will compile and use `readSerializable` and `writeSerializable` to passs "dateArray" between processes.

### Other documentation

Read [project wiki](http://sf.net/p/aidl2/docs/) for in-depth documentations and some useful links.

[1]: https://developer.android.com/guide/components/aidl.html
[2]: https://android.googlesource.com/platform/system/tools/aidl/
