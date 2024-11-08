plugins {
    id("net.risesoft.y9.java-conventions")
}

dependencies {
    api(project(":y9-digitalbase-common:risenet-y9boot-properties"))
    api(project(":y9-digitalbase-common:risenet-y9boot-common-util"))
    api(libs.org.springframework.boot.spring.boot.autoconfigure)
    api(libs.org.springframework.boot.spring.boot.configuration.processor)
    api(libs.org.springframework.boot.spring.boot.starter.web)
    compileOnly(libs.jakarta.servlet.jakarta.servlet.api)
    compileOnly(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)
}

description = "risenet-y9boot-starter-web"
