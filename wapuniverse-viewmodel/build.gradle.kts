plugins {
    kotlin("jvm")
}

dependencies {
    compile(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.0.1")
    compile("org.fxmisc.easybind:easybind:1.0.3")
    compile("org.reactfx:reactfx:2.0-M5")
}
