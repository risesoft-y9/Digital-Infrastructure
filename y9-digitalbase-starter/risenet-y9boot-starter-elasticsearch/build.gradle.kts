plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    api(libs.org.springframework.boot.spring.boot)
    api(libs.org.springframework.boot.spring.boot.autoconfigure)
    api(libs.org.springframework.boot.spring.boot.configuration.processor)
    api(libs.org.springframework.boot.spring.boot.starter.data.elasticsearch)
    api(libs.org.glassfish.jakarta.json)
    compileOnly(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)
}

description = "risenet-y9boot-starter-elasticsearch"
