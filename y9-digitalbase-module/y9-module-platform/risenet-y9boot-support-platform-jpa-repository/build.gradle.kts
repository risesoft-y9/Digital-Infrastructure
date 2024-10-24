plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-idgenerator"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-jpa-public"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-jpa-tenant"))
    api(libs.org.hibernate.validator.hibernate.validator)
    compileOnly(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)
}

description = "risenet-y9boot-support-platform-jpa-repository"
