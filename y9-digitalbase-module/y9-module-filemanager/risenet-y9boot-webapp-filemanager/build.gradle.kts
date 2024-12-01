plugins {
    id("net.risesoft.y9.java-conventions")
    id("net.risesoft.y9.lombok")
    id("net.risesoft.y9.docker")
    id("war")
    alias(libs.plugins.org.springframework.boot)
}

dependencies {
    management(platform(project(":y9-digitalbase-dependencies")))

    implementation(project(":y9-digitalbase-support:risenet-y9boot-support-file-jpa-repository"))
    implementation(project(":y9-digitalbase-support:risenet-y9boot-support-file-service-rest"))
    implementation(project(":y9-digitalbase-common:risenet-y9boot-common-nacos"))

    implementation(libs.google.gson)
    implementation(libs.commons.io)
    implementation(libs.commons.codec)
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api")
    implementation(libs.imgscalr.lib)
    implementation(libs.imageio.jpeg)
    compileOnly("jakarta.servlet:jakarta.servlet-api")

    implementation("org.springframework.boot:spring-boot-starter-web")
    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")

}

description = "risenet-y9boot-webapp-filemanager"

val finalName = "filemanager"
jib.container.appRoot = "/usr/local/tomcat/webapps/${finalName}"

tasks.bootWar {
    archiveFileName.set("${finalName}.${archiveExtension.get()}")
}