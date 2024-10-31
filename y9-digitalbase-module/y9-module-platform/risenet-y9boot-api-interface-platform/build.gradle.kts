plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    api(project(":y9-digitalbase-common:risenet-y9boot-common-model"))
    api(libs.org.springframework.spring.web)
    api(libs.org.springframework.boot.spring.boot.starter.validation)
    compileOnly(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)
}

description = "risenet-y9boot-api-interface-platform"