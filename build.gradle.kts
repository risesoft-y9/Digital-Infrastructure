plugins {
    java
    id("com.vanniktech.maven.publish") version "0.30.0" apply false
    id("tech.yanand.maven-central-publish") version "1.3.0" apply false

    id("net.risesoft.y9.aspectj") version "0.0.5" apply false
    id("net.risesoft.y9.docker") version "0.0.5" apply false
    id("net.risesoft.y9.java-conventions") version "0.0.5" apply false
    id("net.risesoft.y9.java-publish") version "0.0.5" apply false
    id("net.risesoft.y9.java-publish-central") version "0.0.5" apply false
    id("net.risesoft.y9.javaPlatform-publish") version "0.0.5" apply false
    id("net.risesoft.y9.javaPlatform-publish-central") version "0.0.5" apply false
    id("net.risesoft.y9.lombok") version "0.0.5" apply false
    id("net.risesoft.y9.management") version "0.0.5" apply true
    id("net.risesoft.y9.repository") version "0.0.5" apply false
    id("net.risesoft.y9.smart-doc") version "0.0.5" apply false
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    //management(platform(project(":y9-digitalbase-dependencies")))
    //management(platform(libs.spring.boot.bom))
}

group = "net.risesoft"
version = "v9.7.0-SNAPSHUT"