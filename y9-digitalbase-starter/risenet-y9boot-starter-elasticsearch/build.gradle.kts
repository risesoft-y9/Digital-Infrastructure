plugins {
    id("net.risesoft.y9.java-conventions")
    id("net.risesoft.y9.lombok")
}

dependencies {
    api(platform(libs.spring.boot.bom))

    api("org.springframework.boot:spring-boot")
    api("org.springframework.boot:spring-boot-autoconfigure")
    api("org.springframework.boot:spring-boot-configuration-processor")
    api("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    //pi(libs.org.glassfish.jakarta.json)
}

description = "risenet-y9boot-starter-elasticsearch"
