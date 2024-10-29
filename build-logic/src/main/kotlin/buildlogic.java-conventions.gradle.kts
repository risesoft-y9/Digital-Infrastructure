plugins {
    id("java-library")
    id("maven-publish")
    id("signing")
    id("buildlogic.y9-repository")
}

java.sourceCompatibility = JavaVersion.VERSION_21

java {
    withJavadocJar()
    withSourcesJar()
}

