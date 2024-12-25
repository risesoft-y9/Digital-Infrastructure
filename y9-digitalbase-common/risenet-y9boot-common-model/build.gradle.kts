plugins {
    id("net.risesoft.y9.conventions-java")
    id("net.risesoft.y9.lombok")
}

dependencies {
    api(platform(libs.spring.boot.bom))

    api("com.fasterxml.jackson.core:jackson-databind")
    api("org.springframework.boot:spring-boot-starter-validation")
}

description = "risenet-y9boot-common-model"
