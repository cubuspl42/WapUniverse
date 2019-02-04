import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

dependencies {
    compile(kotlin("stdlib"))
    compile("org.fxmisc.easybind:easybind:1.0.3")
    compile("org.reactfx:reactfx:2.0-M5")
    compile("org.yaml:snakeyaml:1.9")
    compile("net.corda:corda:3.3-corda")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
