plugins {
    id("net.risesoft.y9.conventions-java")
    id("net.risesoft.y9.lombok")
}

dependencies {
    api(platform(libs.spring.boot.bom))

    api("org.springframework.boot:spring-boot")
    api("org.springframework.boot:spring-boot-configuration-processor")
}

description = "risenet-y9boot-properties"
