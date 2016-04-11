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
limited DSL, aidl2 uses Java annotation processing framework to generate Java code from Java code.

### Current status

This project is in early alpha. It is not nearly as well-tested as original aidl tool, but
most features are there.

### Limitations and Known Issues

* JDK 6 *may* work, but isn't supported, since it is no longer supported by it's
creator and developers of Javapoet.

* Passing invalid expressions as annotation parameters may not be correctly detected
during validation step and results in runtime exception during compilation with Javac.

Example:

    @AIDL(SomeNonExistingType.DESCRIPTOR)

This is due to limitations of Javac, which converts erroneous "types" to strings
before passing them to AnnotationValueVisitor.

* Generated implementations of interfaces, containing intersection types, may fail to
compile with source versions before Java 8.

    // this will fail to compile with JDK 7
    <T extends Parcelable & Runnable> void methodWithIntersectionArgument(T param);

This is because implementing those requires support for either advanced type inference
or casts to intersection, both of which weren't available until Java 8.

* Some of generated files may contain redundant casts. This is due to limitation of
Java compilers, which sometimes fail to correctly tell annotation processors
whether a cast is needed (this tends to happen when generics are involved).

* Nonsential intersection types like `Boolean & Runnable` aren't detected.
I believe, that those should be handled by the compiler.
