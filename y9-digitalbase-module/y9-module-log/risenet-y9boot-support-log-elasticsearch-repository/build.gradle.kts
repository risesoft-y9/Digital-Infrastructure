plugins {
    id("net.risesoft.y9.java-conventions")
    id("net.risesoft.y9.lombok")
}

dependencies {
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-idgenerator"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-elasticsearch"))
    api(project(":y9-digitalbase-module:y9-module-platform:risenet-y9boot-api-feignclient-platform"))

}

description = "risenet-y9boot-support-log-elasticsearch-repository"
