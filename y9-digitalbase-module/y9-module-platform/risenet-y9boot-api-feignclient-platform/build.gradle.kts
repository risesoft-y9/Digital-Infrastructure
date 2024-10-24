plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-openfeign"))
    api(project(":y9-digitalbase-module:y9-module-platform:risenet-y9boot-api-interface-platform"))
    compileOnly(libs.jakarta.servlet.jakarta.servlet.api)
    compileOnly(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)
}

description = "risenet-y9boot-api(-feignclient-platform")
