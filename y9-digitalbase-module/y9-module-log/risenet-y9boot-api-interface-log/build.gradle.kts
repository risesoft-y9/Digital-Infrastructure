/*
 * This file was generated by the Gradle "init" task.
 *
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    id("net.risesoft.y9.java-conventions")
    id("net.risesoft.y9.lombok")
}

dependencies {
    api(project(":y9-digitalbase-common:risenet-y9boot-common-model"))
    api(platform(libs.spring.boot.bom))
    
    api("org.springframework:spring-web")
    api("org.springframework.boot:spring-boot-starter-validation")
}

description = "risenet-y9boot-api-interface-log"