plugins {
    id("net.risesoft.y9.java-conventions")
    id("net.risesoft.y9.lombok")
    id("net.risesoft.y9.docker")
    id("war")
    alias(libs.plugins.org.springframework.boot)
}

dependencies {
    api(project(":y9-digitalbase-module:y9-module-log:risenet-y9boot-support-log-web"))
    api(project(":y9-digitalbase-module:y9-module-log:risenet-y9boot-api-interface-log"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-security"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-sso-oauth2-resource"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-apisix"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-web"))
    api(project(":y9-digitalbase-common:risenet-y9boot-common-nacos"))
    
    api(platform(libs.spring.boot.bom))
    
    api("org.springframework.kafka:spring-kafka")
    api(libs.jxls)
    api(libs.jxls.poi)
    api(libs.jxls.jexcel)
    api(libs.jxls.reader)
    api("org.springframework.boot:spring-boot-starter-actuator")
    api(libs.micrometer.registry.prometheus)
    providedCompile("jakarta.servlet:jakarta.servlet-api")
    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
}

description = "risenet-y9boot-webapp-log"

val finalName = "log"
jib.container.appRoot = "/usr/local/tomcat/webapps/${finalName}"

tasks.bootWar {
    archiveFileName.set("${finalName}.${archiveExtension.get()}")
}
