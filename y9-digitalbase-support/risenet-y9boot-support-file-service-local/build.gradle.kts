plugins {
    id("net.risesoft.y9.java-conventions")
    id("net.risesoft.y9.lombok")
    id("net.risesoft.y9.aspectj")
}

dependencies {
    api(project(":y9-digitalbase-support:risenet-y9boot-support-file-jpa-repository"))
    
    api(platform(libs.spring.boot.bom))
    api("org.springframework.boot:spring-boot-configuration-processor")
    api("org.slf4j:slf4j-api")
}

description = "risenet-y9boot-support-file-service-local"
