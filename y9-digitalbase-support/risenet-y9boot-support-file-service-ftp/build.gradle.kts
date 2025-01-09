plugins {
    id("net.risesoft.y9.conventions-java")
    id("net.risesoft.y9.lombok")
}

dependencies {
    api(project(":y9-digitalbase-support:risenet-y9boot-support-file-jpa-repository"))

    api("org.springframework.boot:spring-boot-configuration-processor")
    api("org.slf4j:slf4j-api")
    api("org.apache.commons:commons-lang3")
    api(libs.commons.io)
    api(libs.commons.net)
    api("org.apache.commons:commons-pool2")
}

description = "risenet-y9boot-support-file-service-ftp"
