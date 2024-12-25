plugins {
    id("net.risesoft.y9.conventions-java")
    id("net.risesoft.y9.lombok")
}

dependencies {
    api(project(":y9-digitalbase-common:risenet-y9boot-properties"))
    api(project(":y9-digitalbase-common:risenet-y9boot-common-util"))

    api("org.springframework.boot:spring-boot")
    api("org.springframework.boot:spring-boot-autoconfigure")
    api("org.springframework.boot:spring-boot-configuration-processor")
    api("org.springframework.boot:spring-boot-starter-actuator")
    api(libs.classgraph)
    api(libs.httpcomponents.httpclient)
    api(libs.etcd.java)
    compileOnly("jakarta.servlet:jakarta.servlet-api")
}

description = "risenet-y9boot-starter-apisix"
