plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    api(libs.org.springframework.boot.spring.boot)
    api(libs.org.springframework.boot.spring.boot.autoconfigure)
    api(libs.org.springframework.boot.spring.boot.configuration.processor)
    api(libs.org.springframework.boot.spring.boot.starter.data.redis)
    api(libs.org.apache.commons.commons.pool2)
    api(libs.org.springframework.boot.spring.boot.starter.cache)
    compileOnly(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)
}

description = "risenet-y9boot-starter-cache-redis"
