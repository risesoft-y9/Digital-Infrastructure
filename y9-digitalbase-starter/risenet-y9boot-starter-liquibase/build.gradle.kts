plugins {
    id("net.risesoft.y9.conventions-java")
    id("net.risesoft.y9.lombok")
}

dependencies {
    api(project(":y9-digitalbase-common:risenet-y9boot-common-tenant-datasource"))
    api(project(":y9-digitalbase-common:risenet-y9boot-properties"))
    
    api(platform(libs.spring.boot.bom))
    api("org.springframework.boot:spring-boot")
    api("org.springframework.boot:spring-boot-autoconfigure")
    api("org.springframework.boot:spring-boot-configuration-processor")
    api(libs.liquibase.core)
    api(libs.postgresql)
}

description = "risenet-y9boot-starter-liquibase"
