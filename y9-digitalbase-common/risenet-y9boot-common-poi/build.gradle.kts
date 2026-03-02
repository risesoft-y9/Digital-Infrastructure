plugins {
    id("net.risesoft.y9.conventions-java")
    id("net.risesoft.y9.lombok")
}

dependencies {
    api(platform(libs.spring.boot.bom))
    
    api("org.slf4j:slf4j-api")
    api(libs.poi.scratchpad)
    api(libs.poi.ooxml)

    compileOnly("jakarta.servlet:jakarta.servlet-api")
}

description = "risenet-y9boot-common-poi"
