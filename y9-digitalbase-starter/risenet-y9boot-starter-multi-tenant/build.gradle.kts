plugins {
    id("net.risesoft.y9.conventions-java")
    id("net.risesoft.y9.lombok")
}

dependencies {
    api(project(":y9-digitalbase-common:risenet-y9boot-common-tenant-datasource"))
    api(project(":y9-digitalbase-module:y9-module-platform:risenet-y9boot-common-platform"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-jpa-tenant"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-liquibase"))

    api(platform(libs.spring.boot.bom))
    api("org.springframework.boot:spring-boot")
    api("org.springframework.boot:spring-boot-autoconfigure")
    api("org.springframework.boot:spring-boot-configuration-processor")
    api("org.springframework.kafka:spring-kafka")
}

description = "risenet-y9boot-starter-multi-tenant"
