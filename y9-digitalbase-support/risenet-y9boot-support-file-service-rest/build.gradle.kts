plugins {
    id("net.risesoft.y9.conventions-java")
    id("net.risesoft.y9.lombok")
}

dependencies {
    api(project(":y9-digitalbase-support:risenet-y9boot-support-file-jpa-repository"))
    api("org.springframework:spring-web")
    api("org.slf4j:slf4j-api")
    api("org.apache.commons:commons-lang3")
    api(libs.commons.io)
    api(libs.jodd.http)
}

description = "risenet-y9boot-support-file-service-rest"
