plugins {
    id("net.risesoft.y9.aspectj")
    id("net.risesoft.y9.java-conventions")
}

dependencies {
    api(project(":y9-digitalbase-support:risenet-y9boot-support-file-jpa-repository"))
    api(libs.org.springframework.boot.spring.boot.configuration.processor)
    api(libs.org.slf4j.slf4j.api)
    api(libs.org.apache.commons.commons.lang3)
    api(libs.commons.io.commons.io)
    api(libs.commons.net.commons.net)
    api(libs.org.apache.commons.commons.pool2)
    compileOnly(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)
}

description = "risenet-y9boot-support-file-service-ftp"
