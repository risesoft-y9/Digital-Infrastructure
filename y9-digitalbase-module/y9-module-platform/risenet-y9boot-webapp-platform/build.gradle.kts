plugins {
    id("buildlogic.java-conventions")
    id("buildlogic.docker")
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
    api(libs.org.apache.commons.commons.pool2)
    api(libs.org.springframework.boot.spring.boot.docker.compose.x1)
    api(libs.org.dom4j.dom4j)
    api(libs.jaxen.jaxen)
    api(libs.com.google.guava.guava)
    api(libs.org.springframework.boot.spring.boot.starter.actuator)
    api(libs.io.micrometer.micrometer.registry.prometheus)
    testImplementation(libs.org.springframework.boot.spring.boot.starter.test)
    testImplementation(libs.com.h2database.h2)
    providedCompile libs.jakarta.servlet.jakarta.servlet.api
    compileOnly(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)
    testCompileOnly(libs.org.projectlombok.lombok)
    testAnnotationProcessor libs.org.projectlombok.lombok
    providedRuntime libs.org.springframework.boot.spring.boot.starter.tomcat
}

description = "risenet-y9boot-webapp-platform"

ext {
    finalName = "platform"
}

jib.container.appRoot = "/usr/local/tomcat/webapps/${finalName}"

tasks.bootWar {
    archiveFileName.set("${finalName}.${archiveExtension.get()}")
}