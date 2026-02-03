plugins {
    id("net.risesoft.y9.conventions-java")
    id("net.risesoft.y9.lombok")
}

dependencies {
    api(platform(libs.spring.boot.bom))
    //api(platform(libs.spring.cloud.alibaba.bom))

    api(project(":y9-digitalbase-common:risenet-y9boot-common-model"))

    api("com.fasterxml.jackson.core:jackson-databind")
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-properties")
    api("org.apache.commons:commons-lang3")
    api("org.slf4j:slf4j-api")
    api("org.springframework:spring-web")
    api("org.springframework:spring-context")
    api(libs.commons.io)
    api(libs.commons.httpclient)
    api(libs.alibaba.transmittable.thread.local)
    api(libs.jbcrypt)
    api(libs.poi.scratchpad)
    api(libs.poi.ooxml)
    api(libs.hutool.all)
    api(libs.bcprov.ext.jdk18on)

    compileOnly("jakarta.servlet:jakarta.servlet-api")
    
    testImplementation("jakarta.servlet:jakarta.servlet-api")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter")
}

description = "risenet-y9boot-common-util"
