plugins {
    id("net.risesoft.y9.conventions-java")
    id("net.risesoft.y9.lombok")
}

dependencies {
    api(platform(project(":y9-digitalbase-dependencies")))

    api("org.springframework.boot:spring-boot")
    api("org.springframework.boot:spring-boot-configuration-processor")
}

description = "risenet-y9boot-properties"
