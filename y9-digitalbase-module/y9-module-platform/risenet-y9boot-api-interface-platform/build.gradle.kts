plugins {
    id("net.risesoft.y9.conventions-java")
    id("net.risesoft.y9.lombok")
}

dependencies {
    api(project(":y9-digitalbase-common:risenet-y9boot-common-model"))
    api(platform(libs.spring.boot.bom))
    
    api("org.springframework:spring-web")
    api("org.springframework.boot:spring-boot-starter-validation")
}

description = "risenet-y9boot-api-interface-platform"
