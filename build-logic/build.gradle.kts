plugins {
    // Support convention plugins written in Kotlin. Convention plugins are build scripts in 'src/main' that automatically become available as plugins in the main build.
    `kotlin-dsl`
}

repositories {
    // Use the plugin portal to apply community plugins in convention plugins.
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("com.google.cloud.tools:jib-gradle-plugin:3.4.4")
    implementation("com.ly.smart-doc:smart-doc-gradle-plugin:3.0.8")
    implementation("io.freefair.gradle:aspectj-plugin:8.10.2")
    implementation("io.freefair.gradle:lombok-plugin:8.10.2")
}
