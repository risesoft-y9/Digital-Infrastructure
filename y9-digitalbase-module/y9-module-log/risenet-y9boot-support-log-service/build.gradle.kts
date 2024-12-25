plugins {
    id("net.risesoft.y9.conventions-java")
    id("net.risesoft.y9.lombok")
}

dependencies {
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-idgenerator"))
    api(project(":y9-digitalbase-module:y9-module-log:risenet-y9boot-support-log-jpa-repository"))
    api(project(":y9-digitalbase-module:y9-module-platform:risenet-y9boot-api-feignclient-platform"))
}

description = "risenet-y9boot-support-log-service"
