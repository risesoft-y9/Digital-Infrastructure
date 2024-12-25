plugins {
    id("net.risesoft.y9.conventions-java")
    id("net.risesoft.y9.lombok")
}

dependencies {
    api(project(":y9-digitalbase-common:risenet-y9boot-properties"))
    api(project(":y9-digitalbase-common:risenet-y9boot-common-util"))
    
    api(platform(libs.spring.boot.bom))
    api("org.springframework.boot:spring-boot-autoconfigure")
    api("org.springframework.boot:spring-boot-configuration-processor")
    api("org.springframework.boot:spring-boot-starter-web")
    compileOnly("jakarta.servlet:jakarta.servlet-api")
}

description = "risenet-y9boot-starter-web"
