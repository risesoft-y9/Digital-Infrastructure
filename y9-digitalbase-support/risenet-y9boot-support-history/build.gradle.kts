plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    api(project(":y9-digitalbase-common:risenet-y9boot-common-util"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-jpa-public"))
    api(libs.org.springframework.boot.spring.boot.configuration.processor)
    api(libs.org.springframework.spring.web)
    api(libs.org.springframework.spring.webmvc)
    api(libs.org.javers.javers.spring.boot.starter.sql)
    api(libs.com.google.guava.guava)
    compileOnly(libs.jakarta.servlet.jakarta.servlet.api)
    compileOnly(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)
}

description = "risenet-y9boot-support-history"
