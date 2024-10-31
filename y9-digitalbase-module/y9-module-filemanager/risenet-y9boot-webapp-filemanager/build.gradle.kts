plugins {
    id("buildlogic.java-conventions")
    id("buildlogic.docker")
    id("war")
    alias(libs.plugins.org.springframework.boot)
}

dependencies {
    api(project(":y9-digitalbase-support:risenet-y9boot-support-file-jpa-repository"))
    api(project(":y9-digitalbase-support:risenet-y9boot-support-file-service-rest"))
    api(project(":y9-digitalbase-common:risenet-y9boot-common-nacos"))
    api(libs.com.google.code.gson.gson)
    api(libs.commons.io.commons.io)
    api(libs.commons.codec.commons.codec)
    api(libs.org.springframework.boot.spring.boot.starter.web)
    api(libs.jakarta.servlet.jsp.jstl.jakarta.servlet.jsp.jstl.api)
    api(libs.org.imgscalr.imgscalr.lib)
    api(libs.com.twelvemonkeys.imageio.imageio.jpeg)
    providedCompile(libs.jakarta.servlet.jakarta.servlet.api)
    providedRuntime(libs.org.springframework.boot.spring.boot.starter.tomcat)
    compileOnly(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)
}

description = "risenet-y9boot-webapp-filemanager"

val finalName = "filemanager"
jib.container.appRoot = "/usr/local/tomcat/webapps/${finalName}"

tasks.bootWar {
    archiveFileName.set("${finalName}.${archiveExtension.get()}")
}