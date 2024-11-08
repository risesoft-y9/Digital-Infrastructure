plugins {
    id("net.risesoft.y9.java-conventions")
}

dependencies {
    api(project(":y9-digitalbase-common:risenet-y9boot-properties"))
    api(project(":y9-digitalbase-common:risenet-y9boot-common-model"))
    api(project(":y9-digitalbase-common:risenet-y9boot-common-util"))
    api(project(":y9-digitalbase-common:risenet-y9boot-common-tenant-datasource"))
    api(libs.org.springframework.boot.spring.boot)
    api(libs.org.springframework.boot.spring.boot.autoconfigure)
    api(libs.org.springframework.boot.spring.boot.configuration.processor)
    api(libs.org.springframework.kafka.spring.kafka)
    api(libs.org.springframework.cloud.spring.cloud.context)
    compileOnly(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)
}

description = "risenet-y9boot-starter-listener-kafka"
