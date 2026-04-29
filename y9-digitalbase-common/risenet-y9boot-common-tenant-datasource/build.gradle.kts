plugins {
    id("net.risesoft.y9.conventions-java")
    id("net.risesoft.y9.lombok")
}

dependencies {
    api(platform(libs.spring.boot.bom))

    api(project(":y9-digitalbase-common:risenet-y9boot-common-util"))
    api("org.springframework.boot:spring-boot-starter-jdbc")
}

description = "risenet-y9boot-common-tenant-datasource"
