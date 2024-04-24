plugins {
    id("java")
    id("com.github.weave-mc.weave-gradle") version "fac948d"
}

group = "me.pianopenguin471"
version = "1.1.5-Beta"

minecraft.version("1.8.9")

repositories {
    maven("https://jitpack.io")
    maven("https://repo.spongepowered.org/maven/")
}

dependencies {
    compileOnly("com.github.weave-mc:weave-loader:v0.2.4")
    compileOnly("org.spongepowered:mixin:0.8.5")
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
}

tasks.compileJava {
    options.release.set(17)
}
