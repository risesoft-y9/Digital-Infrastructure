plugins {
    id("net.risesoft.y9.conventions-java")
    id("net.risesoft.y9.lombok")
}

dependencies {
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-idgenerator"))
    api(project(":y9-digitalbase-common:risenet-y9boot-common-util"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-jpa-public"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-multi-tenant"))
    api(libs.hibernate.validator)
}

description = "risenet-y9boot-support-log-jpa-repository"
