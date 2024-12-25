plugins {
    id("net.risesoft.y9.conventions-java")
    id("net.risesoft.y9.lombok")
}

dependencies {
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-openfeign"))
    api(project(":y9-digitalbase-module:y9-module-platform:risenet-y9boot-api-interface-platform"))
    compileOnly("jakarta.servlet:jakarta.servlet-api")
}

description = "risenet-y9boot-api-feignclient-platform"
