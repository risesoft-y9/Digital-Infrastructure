plugins {
    id("net.risesoft.y9.conventions-java")
    id("net.risesoft.y9.lombok")
}

dependencies {
    implementation(platform(project(":y9-digitalbase-dependencies")))

    api(project(":y9-digitalbase-common:risenet-y9boot-common-util"))
    api("org.springframework.data:spring-data-commons")
}

description = "risenet-y9boot-support-log-repository-api"
