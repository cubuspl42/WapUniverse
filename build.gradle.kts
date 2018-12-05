plugins {
    base
    kotlin("jvm") version "1.3.10" apply false
}

allprojects {
    group = "io.github.cubuspl42"
    version = "1.0"

    repositories {
        jcenter()
    }
}

dependencies {
    // Make the root project archives configuration depend on every subproject
    subprojects.forEach {
        archives(it)
    }
}
