plugins {
    application
    kotlin("jvm")
}

application {
    mainClassName = "wapuniverse.app.Main"
}

dependencies {
    compile(project(":wapuniverse-editor"))
    compile(kotlin("stdlib"))
}
