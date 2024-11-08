plugins {
    id("net.risesoft.y9.java-conventions")
}

dependencies {
    api(libs.com.fasterxml.jackson.core.jackson.databind)
    api(libs.org.springframework.boot.spring.boot.starter.validation)
    compileOnly(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)
}

description = "risenet-y9boot-common-model"
