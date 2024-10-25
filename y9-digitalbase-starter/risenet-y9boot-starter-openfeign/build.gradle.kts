plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    api(project(":y9-digitalbase-common:risenet-y9boot-properties"))
    api(project(":y9-digitalbase-common:risenet-y9boot-common-util"))
    api(libs.org.springframework.boot.spring.boot)
    api(libs.org.springframework.boot.spring.boot.autoconfigure)
    api(libs.org.springframework.boot.spring.boot.configuration.processor)
    api(libs.org.springframework.cloud.spring.cloud.starter.openfeign) {
        exclude(group = "org.springframework.cloud", module = "spring-cloud-context")
    }
    api(libs.org.springframework.cloud.spring.cloud.starter.loadbalancer)
    api(libs.org.springframework.boot.spring.boot.starter.data.redis)
    compileOnly(libs.jakarta.servlet.jakarta.servlet.api)
    compileOnly(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)
}

description = "risenet-y9boot-starter-openfeign"
