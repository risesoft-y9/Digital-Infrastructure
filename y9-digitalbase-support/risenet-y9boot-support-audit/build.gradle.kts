plugins {
    id("net.risesoft.y9.conventions-java")
    id("net.risesoft.y9.lombok")
    id("net.risesoft.y9.aspectj")
}

dependencies {
    api(project(":y9-digitalbase-common:risenet-y9boot-common-model"))
    api(project(":y9-digitalbase-common:risenet-y9boot-common-util"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-jpa-public"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-idgenerator"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-web"))
    
    compileOnly("jakarta.servlet:jakarta.servlet-api")
}

description = "risenet-y9boot-support-audit"