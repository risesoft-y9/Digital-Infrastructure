plugins {
    id("net.risesoft.y9.java-conventions")
    id("net.risesoft.y9.lombok")
}

dependencies {
    api(platform(libs.spring.boot.bom))
    api("org.springframework.boot:spring-boot")
    api("org.springframework.boot:spring-boot-autoconfigure")
    api("org.springframework.boot:spring-boot-configuration-processor")
    api("org.slf4j:slf4j-api")
    api("org.springframework.boot:spring-boot-starter-data-redis")
    api(libs.java.uuid.generator)
    api(libs.hutool.all)
}

description = "risenet-y9boot-starter-idgenerator"
