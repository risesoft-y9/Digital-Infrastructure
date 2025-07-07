plugins {
    id("net.risesoft.y9.conventions-war")
    id("net.risesoft.y9.lombok")
    id("net.risesoft.y9.docker")
}

dependencies {
    implementation(platform(project(":y9-digitalbase-dependencies")))

    implementation(project(":y9-digitalbase-common:risenet-y9boot-common-util"))
    implementation(project(":y9-digitalbase-module:y9-module-platform:risenet-y9boot-support-platform-web"))
    implementation(project(":y9-digitalbase-module:y9-module-platform:risenet-y9boot-support-platform-jpa-repository"))
    implementation(project(":y9-digitalbase-starter:risenet-y9boot-starter-sso-oauth2-resource"))
    implementation(project(":y9-digitalbase-starter:risenet-y9boot-starter-security"))
    implementation(project(":y9-digitalbase-starter:risenet-y9boot-starter-log"))
    implementation(project(":y9-digitalbase-starter:risenet-y9boot-starter-cache-redis"))
    implementation(project(":y9-digitalbase-common:risenet-y9boot-common-nacos"))
    implementation(project(":y9-digitalbase-starter:risenet-y9boot-starter-multi-tenant"))

    implementation("org.apache.commons:commons-pool2")
    implementation("org.springframework.boot:spring-boot-docker-compose")
    implementation(libs.dom4j)
    implementation(libs.jaxen)
    implementation(libs.google.guava)
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(libs.h2database)

    compileOnly("jakarta.servlet:jakarta.servlet-api")
}

description = "risenet-y9boot-server-platform"

val finalName = "server-platform"

y9Docker {
    appName = finalName
}

y9War {
    archiveBaseName = finalName
}
