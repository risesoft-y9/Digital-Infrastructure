plugins {
    id("net.risesoft.y9.conventions-java")
    id("net.risesoft.y9.lombok")
}

dependencies {
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-idgenerator"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-jpa-public"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-jpa-tenant"))
    api(project(":y9-digitalbase-support:risenet-y9boot-support-history"))
    api(libs.hibernate.validator)
}

description = "risenet-y9boot-support-platform-jpa-repository"
