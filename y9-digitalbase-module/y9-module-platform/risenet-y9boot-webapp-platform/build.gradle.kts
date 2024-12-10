plugins {
    id("net.risesoft.y9.java-conventions")
    id("net.risesoft.y9.lombok")
    id("net.risesoft.y9.docker")
    id("war")
    alias(libs.plugins.org.springframework.boot)
}

dependencies {
    management(platform(project(":y9-digitalbase-dependencies")))

    implementation(project(":y9-digitalbase-common:risenet-y9boot-common-util"))
    implementation(project(":y9-digitalbase-module:y9-module-platform:risenet-y9boot-support-platform-web"))
    implementation(project(":y9-digitalbase-module:y9-module-platform:risenet-y9boot-support-platform-jpa-repository"))
    implementation(project(":y9-digitalbase-starter:risenet-y9boot-starter-sso-oauth2-resource"))
    implementation(project(":y9-digitalbase-starter:risenet-y9boot-starter-security"))
    implementation(project(":y9-digitalbase-starter:risenet-y9boot-starter-log"))
    implementation(project(":y9-digitalbase-starter:risenet-y9boot-starter-cache-redis"))
    implementation(project(":y9-digitalbase-common:risenet-y9boot-common-nacos"))
    implementation(project(":y9-digitalbase-starter:risenet-y9boot-starter-multi-tenant"))
    implementation(project(":y9-digitalbase-support:risenet-y9boot-support-api-access-control"))
    
    implementation(libs.commons.pool2)
    implementation("org.springframework.boot:spring-boot-docker-compose")
    implementation(libs.dom4j)
    implementation(libs.jaxen)
    implementation(libs.google.guava)
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")
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