plugins {
    java
    id("com.vanniktech.maven.publish") version "0.30.0" apply false
    id("tech.yanand.maven-central-publish") version "1.3.0" apply false
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    //api(platform(project(":y9-digitalbase-dependencies")))
    //api(platform(libs.spring.boot.bom))
}

group = "net.risesoft"
allprojects {
    version = findProperty("Y9_VERSION") as String? ?: "9.7.0-SNAPSHOT"
}
extra.set("Y9_VERSION", version)
extra.set("Y9BOM_VERSION", findProperty("Y9BOM_VERSION") as String? ?: "9.7.0-01")
extra.set("Y9PLUGIN_VERSION", findProperty("Y9PLUGIN_VERSION") as String? ?: "9.7.0-01")
