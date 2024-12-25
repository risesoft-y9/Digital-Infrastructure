plugins {
    id("net.risesoft.y9.conventions-java")
    id("net.risesoft.y9.lombok")
}

dependencies {
    api(project(":y9-digitalbase-common:risenet-y9boot-properties"))
    api(project(":y9-digitalbase-common:risenet-y9boot-common-util"))
    
    api(platform(libs.spring.boot.bom))
    api(platform(libs.spring.cloud.bom))
    api("org.springframework.boot:spring-boot")
    api("org.springframework.boot:spring-boot-autoconfigure")
    api("org.springframework.boot:spring-boot-configuration-processor")
    api("org.springframework.cloud:spring-cloud-context")
    api(libs.antisamy)
    compileOnly("jakarta.servlet:jakarta.servlet-api")
}

description = "risenet-y9boot-starter-security"
