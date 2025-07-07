plugins {
    id("net.risesoft.y9.conventions-war")
    id("net.risesoft.y9.lombok")
    id("net.risesoft.y9.docker")
}

dependencies {
    implementation(platform(project(":y9-digitalbase-dependencies")))

    implementation(project(":y9-digitalbase-module:y9-module-log:risenet-y9boot-support-log-web"))
    implementation(project(":y9-digitalbase-module:y9-module-log:risenet-y9boot-api-interface-log"))
    implementation(project(":y9-digitalbase-starter:risenet-y9boot-starter-security"))
    implementation(project(":y9-digitalbase-starter:risenet-y9boot-starter-sso-oauth2-resource"))
    implementation(project(":y9-digitalbase-starter:risenet-y9boot-starter-apisix"))
    implementation(project(":y9-digitalbase-starter:risenet-y9boot-starter-web"))
    implementation(project(":y9-digitalbase-common:risenet-y9boot-common-nacos"))

    implementation(libs.jxls)
    implementation(libs.jxls.poi)
    implementation(libs.jxls.jexcel)
    implementation(libs.jxls.reader)
    implementation(libs.micrometer.registry.prometheus)
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")

    compileOnly("jakarta.servlet:jakarta.servlet-api")
}

description = "risenet-y9boot-server-log"

val finalName = "server-log"

y9Docker {
    appName = finalName
}

y9War {
    archiveBaseName = finalName
}

