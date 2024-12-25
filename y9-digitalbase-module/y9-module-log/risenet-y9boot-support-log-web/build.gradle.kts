plugins {
    id("net.risesoft.y9.conventions-java")
    id("net.risesoft.y9.lombok")
    id("net.risesoft.y9.smart-doc")
}

dependencies {
    api(project(":y9-digitalbase-module:y9-module-log:risenet-y9boot-support-log-service"))
    api(project(":y9-digitalbase-module:y9-module-log:risenet-y9boot-api-interface-log"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-log"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-kafka"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-web"))
    api(libs.jxls)
    api(libs.jxls.poi)
    api(libs.jxls.jexcel)
    api(libs.jxls.reader)
    compileOnly("jakarta.servlet:jakarta.servlet-api")
}

description = "risenet-y9boot-support-log-web"
