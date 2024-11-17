plugins {
    id("net.risesoft.y9.java-conventions")
    id("net.risesoft.y9.lombok")
}

dependencies {
    api(project(":y9-digitalbase-common:risenet-y9boot-common-util"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-jpa-public"))
    
    api(platform(libs.spring.boot.bom))
    api("org.springframework.boot:spring-boot-configuration-processor")
    api("org.springframework:spring-web")
    api("org.springframework:spring-webmvc")
    api(libs.javers.spring.boot.starter.sql)
    api(libs.google.guava)
    compileOnly("jakarta.servlet:jakarta.servlet-api")
}

description = "risenet-y9boot-support-history"
