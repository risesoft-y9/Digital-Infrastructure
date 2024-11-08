plugins {
    id("net.risesoft.y9.java-conventions")
}

dependencies {
    api(project(":y9-digitalbase-common:risenet-y9boot-common-tenant-datasource"))
    api(project(":y9-digitalbase-common:risenet-y9boot-properties"))
    api(libs.org.springframework.boot.spring.boot)
    api(libs.org.springframework.boot.spring.boot.autoconfigure)
    api(libs.org.springframework.boot.spring.boot.configuration.processor)
    api(libs.org.liquibase.liquibase.core)
    api(libs.org.postgresql.postgresql)
    compileOnly(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)
}

description = "risenet-y9boot-starter-liquibase"
