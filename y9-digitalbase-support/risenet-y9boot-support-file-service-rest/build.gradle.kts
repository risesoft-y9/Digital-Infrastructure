plugins {
    id("net.risesoft.y9.java-conventions")
}

dependencies {
    api(project(":y9-digitalbase-support:risenet-y9boot-support-file-jpa-repository"))
    api(libs.org.slf4j.slf4j.api)
    api(libs.org.apache.commons.commons.lang3)
    api(libs.commons.io.commons.io)
    api(libs.org.springframework.spring.web)
    api(libs.org.jodd.jodd.http)
    compileOnly(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)
}

description = "risenet-y9boot-support-file-service-rest"
