plugins {
    id("net.risesoft.y9.conventions-java")
    id("net.risesoft.y9.lombok")
}

dependencies {
    api(platform(libs.spring.boot.bom))

    api("org.springframework.boot:spring-boot")
    api("org.springframework.boot:spring-boot-autoconfigure")
    api("org.springframework.boot:spring-boot-configuration-processor")
    api("org.springframework.boot:spring-boot-starter-data-redis")
    api("org.apache.commons:commons-pool2")
    api("org.springframework.boot:spring-boot-starter-cache")
}

description = "risenet-y9boot-starter-cache-redis"
