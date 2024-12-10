plugins {
    id("net.risesoft.y9.java-conventions")
    id("net.risesoft.y9.lombok")
    //id("net.risesoft.y9.aspectj")
}

dependencies {
    api(libs.ipaddress)
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-jpa-public"))
    compileOnly("jakarta.servlet:jakarta.servlet-api")
}

description = "risenet-y9boot-support-api-access-control"
