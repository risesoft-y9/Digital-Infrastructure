plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    api(libs.org.springframework.boot.spring.boot)
    api(libs.org.springframework.boot.spring.boot.autoconfigure)
    api(libs.org.springframework.boot.spring.boot.configuration.processor)
    api(libs.org.slf4j.slf4j.api)
    api(libs.org.springframework.boot.spring.boot.starter.data.redis)
    api(libs.com.fasterxml.uuid.java.uuid.generator)
    api(libs.cn.hutool.hutool.all)
    compileOnly(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)
}

description = "risenet-y9boot-starter-idgenerator"
