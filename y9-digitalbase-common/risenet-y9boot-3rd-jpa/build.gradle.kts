plugins {
    id("net.risesoft.y9.conventions-java")
    id("net.risesoft.y9.lombok")
}

dependencies {
    api(platform(libs.spring.boot.bom))

    api("org.springframework.boot:spring-boot-starter-data-jpa")
}

//group = "net.risesoft"
//version = providers.gradleProperty("Y9_VERSION").get()
description = "risenet-y9boot-3rd-jpa"
