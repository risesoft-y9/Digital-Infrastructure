plugins {
    id("net.risesoft.y9.java-conventions")
    id("net.risesoft.y9.lombok")
    id("net.risesoft.y9.docker")
    id("war")
    alias(libs.plugins.org.springframework.boot)
}

dependencies {
    api(project(":y9-digitalbase-support:risenet-y9boot-support-file-jpa-repository"))
    api(project(":y9-digitalbase-support:risenet-y9boot-support-file-service-rest"))
    api(project(":y9-digitalbase-common:risenet-y9boot-common-nacos"))
    
    api(platform(libs.spring.boot.bom))
    
    api(libs.google.gson)
    api(libs.commons.io)
    api(libs.commons.codec)
    api("org.springframework.boot:spring-boot-starter-web")
    api("jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api")
    api(libs.imgscalr.lib)
    api(libs.imageio.jpeg)
    providedCompile("jakarta.servlet:jakarta.servlet-api")
    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")

}

description = "risenet-y9boot-webapp-filemanager"

val finalName = "filemanager"
jib.container.appRoot = "/usr/local/tomcat/webapps/${finalName}"

tasks.bootWar {
    archiveFileName.set("${finalName}.${archiveExtension.get()}")
}