plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    api(project(":y9-digitalbase-common:risenet-y9boot-common-tenant-datasource"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-jpa-tenant"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-liquibase"))
    api(libs.org.springframework.boot.spring.boot)
    api(libs.org.springframework.boot.spring.boot.autoconfigure)
    api(libs.org.springframework.boot.spring.boot.configuration.processor)
    api(libs.org.springframework.kafka.spring.kafka)
    compileOnly(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)
}

description = "risenet-y9boot-starter-multi-tenant"
