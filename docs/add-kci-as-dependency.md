## Adding CustomItems as dependency to your own project
### Without build system
If you don't like build systems, you can simply download CustomItems.jar from Spigot or BukkitDev
and add it to your classpath.

### Gradle
Add the Jitpack maven repository:
```
repositories {
    // Your current repositories...
    maven { url 'https://jitpack.io' }
}
```
And add the modules you want to your dependencies:
```
dependencies {
    // Your current dependencies...
    
    // You probably want shared-code and plug-in
    compileOnly 'com.github.knokko.custom-items-gradle:shared-code:master-SNAPSHOT'
    compileOnly 'com.github.knokko.custom-items-gradle:plug-in:master-SNAPSHOT'
    
    // You may also want to have e.g. the Editor
    compileOnly 'com.github.knokko.custom-items-gradle:editor:master-SNAPSHOT'
}
```

### Maven
Add the Jitpack maven repository:
```
<repositories>
    # Your current repositories...
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
And add the modules you want to your dependencies:
```
# Your current dependencies...
<dependency>
    <groupId>com.github.knokko.custom-items-gradle</groupId>
    <artifactId>shared-code</artifactId>
    <version>master-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>com.github.knokko.custom-items-gradle</groupId>
    <artifactId>plug-in</artifactId>
    <version>master-SNAPSHOT</version>
</dependency>
```
