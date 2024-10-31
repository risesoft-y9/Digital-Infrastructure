plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    api(project(":y9-digitalbase-support:risenet-y9boot-support-file-jpa-repository"))
    api(libs.org.springframework.boot.spring.boot.configuration.processor)
    api(libs.org.slf4j.slf4j.api)
    compileOnly(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)
}

description = "risenet-y9boot-support-file-service-local"