plugins {
    id("net.risesoft.y9.java-conventions")
    id("net.risesoft.y9.lombok")
}

dependencies {
    api(project(":y9-digitalbase-support:risenet-y9boot-support-file-jpa-repository"))
    
    api(platform(libs.spring.boot.bom))
    api("org.slf4j:slf4j-api")
    api(libs.commons.lang3)
    api(libs.commons.io)
    api("org.springframework:spring-web")
    api(libs.jodd.http)
}

description = "risenet-y9boot-support-file-service-rest"
