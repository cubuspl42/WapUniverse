import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
    id("edu.sc.seis.launch4j") version "2.4.4"
    id("com.github.johnrengelman.shadow") version "4.0.4"
}

application {
    mainClassName = "wapuniverse.app.Main"
}

dependencies {
    compile(project(":wapuniverse-editor"))
    compile(kotlin("stdlib"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.register<Jar>("uberJar") {
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)

    from(
        configurations.runtimeClasspath.get()
                .filter { it.name.endsWith("jar") }
                .map { zipTree(it) }
    )
}

launch4j {
    mainClassName = "wapuniverse.app.MyApplication"
    copyConfigurable = project.tasks.shadowJar.get().outputs.files
    jar = "lib/${project.tasks.shadowJar.get().archiveName}"
//    icon = "${projectDir}/icons/myApp.ico"
}
