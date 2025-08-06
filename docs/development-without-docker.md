## Development without Docker
Using Docker is recommended for development,
but not absolute required.
If you don't want to use Docker 
(e.g. because you value your disk space),
you will need to follow these steps:

### Install OpenJDK
You need to install OpenJDK (or some other JDK) 
on your development computer.
The Java version depends on the minecraft version(s) 
on which you want to test.
The next section provides an overview of which Java
version is required for which minecraft version.

### Run BuildTools
For each minecraft version that you want to test on,
you need to run
[BuildTools](https://www.spigotmc.org/wiki/buildtools/)
using the right Java version (some MC versions support
multiple Java versions, but I only mention 1):
- MC 1.12: use Java 8: `java -jar BuildTools.jar --rev 1.12.2 --compile CRAFTBUKKIT`
- MC 1.13: use Java 8: `java -jar BuildTools.jar --rev 1.13.2 --compile CRAFTBUKKIT`
- MC 1.14: use Java 8: `java -jar BuildTools.jar --rev 1.14.4 --compile CRAFTBUKKIT`
- MC 1.15: use Java 8: `java -jar BuildTools.jar --rev 1.15.2`
- MC 1.16: use Java 8: `java -jar BuildTools.jar --rev 1.16.5`
- MC 1.17: use Java 16: `java -jar BuildTools.jar --rev 1.17.1`
- MC 1.18: use Java 17: `java -jar BuildTools.jar --rev 1.18.2`
- MC 1.19: use Java 17: `java -jar BuildTools.jar --rev 1.19.4`
- MC 1.20: use Java 21: `java -jar BuildTools.jar --rev 1.20.6`
- MC 1.21: use Java 21: `java -jar BuildTools.jar --rev 1.21.8`

These steps will add some dependencies to the mavenLocal
on your computer, which are required for development.
They will also generate the server JAR files, which you
can run.

Note that I only mention 1 supported Java version per
minecraft version. Some minecraft versions support
multiple Java versions, but I won't mention them all.

### Getting rid of the other minecraft versions
If you don't need to test on all these minecraft versions
in development (understandable), you should get rid of
some dependencies in `build.gradle` and `settings.gradle`.
You should remove the `kci-nms1.XY` projects for each
MC 1.XY version that you do **not** wish to test on.
If you intend to develop with Java 8, you should also get
rid of `ce-event-handler` and `test-custom-recipes`.
Do **not** push these changes to Git! This is just for
local development.

### Building the plug-in
When you followed all the above steps, you should be able
to build the plug-in by running `./gradlew shadowJar`.
If it succeeds, it should create the plug-in at
`plug-in/build/libs/plug-in-all.jar`. You can copy this
file to the `plugins` folder of your server.

### Running the Editor
You should normally run the Editor via IntelliJ
(or Eclipse, or something else). 
Basically, you should go to 
`editor/src/main/java/nl/knokko/customitems/editor/Editor.java`
and click on the 'run' button of your IDE.

Alternatively, you can build the Editor using
`./gradlew shadowJar` and run it using
`java -jar editor/build/libs/editor-all.jar`.

### Running unit tests
You can run the unit tests using `./gradlew test`.

### Note about publishing releases
When you want to create releases to publish, you should
follow [different steps](./create-release.md).
The steps mentioned on this page are only relevant for
testing and developing on **your** computer, and
don't affect the actual releases.
The actual releases will support any minecraft version
that is supported by this plug-in, **regardless** of
the steps above.
