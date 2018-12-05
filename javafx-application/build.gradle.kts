plugins {
    application
    kotlin("jvm")
}

application {
    mainClassName = "wapuniverse.javafx.Main"
}

dependencies {
    compile(project(":application"))
    compile(kotlin("stdlib"))
}
