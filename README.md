# Java Commons

Contains some common method that I've gathered into this container in order
not to rewrite those for every Java project I do.

## Usage tips

I recommend to use this utilities container through [JitPack](https://jitpack.io/).

In particular, configure the `build.gradle` file as follows.

```
repositories {
    mavenCentral()

    jcenter()

    /* JitPack */
    maven {
        url "https://jitpack.io"
    }
}

dependencies {
    compile 'com.github.Docheinstein:Commons:0.1'
}

```