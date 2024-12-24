plugins {
    id("net.risesoft.y9.java-conventions")
    id("net.risesoft.y9.lombok")
    id("net.risesoft.y9.aspectj")
}

dependencies {
    api(project(":y9-digitalbase-common:risenet-y9boot-properties"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-jpa-public"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-idgenerator"))
    compileOnly("jakarta.servlet:jakarta.servlet-api")
}

description = "risenet-y9boot-support-file-jpa-repository"
