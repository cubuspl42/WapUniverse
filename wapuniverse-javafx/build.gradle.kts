plugins {
    application
    kotlin("jvm")
}

application {
    mainClassName = "wapuniverse.javafx.Main"
}

dependencies {
    compile(project(":wapuniverse-viewmodel"))
    compile(kotlin("stdlib"))
}
