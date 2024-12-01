plugins {
    id("net.risesoft.y9.java-conventions")
    id("net.risesoft.y9.lombok")
}

dependencies {
    management(platform(project(":y9-digitalbase-dependencies")))

    api("org.springframework.boot:spring-boot")
    api("org.springframework.boot:spring-boot-configuration-processor")
}

description = "risenet-y9boot-properties"
