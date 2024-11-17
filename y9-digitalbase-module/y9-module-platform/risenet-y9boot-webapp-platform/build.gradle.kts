plugins {
    id("net.risesoft.y9.java-conventions")
    id("net.risesoft.y9.lombok")
    id("net.risesoft.y9.docker")
    id("war")
    alias(libs.plugins.org.springframework.boot)
}

dependencies {
    api(project(":y9-digitalbase-common:risenet-y9boot-common-util"))
    api(project(":y9-digitalbase-module:y9-module-platform:risenet-y9boot-support-platform-web"))
    api(project(":y9-digitalbase-module:y9-module-platform:risenet-y9boot-support-platform-jpa-repository"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-sso-oauth2-resource"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-security"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-log"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-cache-redis"))
    api(project(":y9-digitalbase-common:risenet-y9boot-common-nacos"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-multi-tenant"))
    
    api(platform(libs.spring.boot.bom))
    api(libs.commons.pool2)
    api("org.springframework.boot:spring-boot-docker-compose")
    api(libs.dom4j)
    api(libs.jaxen)
    api(libs.google.guava)
    api("org.springframework.boot:spring-boot-starter-actuator")
    api("io.micrometer:micrometer-registry-prometheus")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(libs.h2database)
    providedCompile("jakarta.servlet:jakarta.servlet-api")
    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
}

description = "risenet-y9boot-webapp-platform"

val finalName = "platform"
jib.container.appRoot = "/usr/local/tomcat/webapps/${finalName}"

tasks.bootWar {
    archiveFileName.set("${finalName}.${archiveExtension.get()}")
}