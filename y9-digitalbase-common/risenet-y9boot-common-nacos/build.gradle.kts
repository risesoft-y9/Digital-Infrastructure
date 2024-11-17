plugins {
    id("net.risesoft.y9.java-conventions")
    id("net.risesoft.y9.lombok")
}

dependencies {
    api(platform(libs.spring.boot.bom))
    api(platform(libs.spring.cloud.alibaba.bom))

    api("org.springframework.boot:spring-boot-autoconfigure")
    api("com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-config")
    api("com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-discovery")
    api("com.alibaba.nacos:nacos-client")
    api("org.springframework.boot:spring-boot-starter-web")
}

description = "risenet-y9boot-common-nacos"
