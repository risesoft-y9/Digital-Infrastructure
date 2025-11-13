plugins {
    id("net.risesoft.y9.conventions-java")
    id("net.risesoft.y9.lombok")
}

dependencies {
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-idgenerator"))
    api(project(":y9-digitalbase-common:risenet-y9boot-common-util"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-jpa-public"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-liquibase"))
    api(project(":y9-digitalbase-module:y9-module-log:risenet-y9boot-support-log-repository-api"))
    api(libs.hibernate.validator)
}

description = "risenet-y9boot-support-log-jpa-repository"
