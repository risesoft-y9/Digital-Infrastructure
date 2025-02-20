plugins {
    id("net.risesoft.y9.conventions-java")
    id("net.risesoft.y9.lombok")
    id("net.risesoft.y9.smart-doc")
    id("net.risesoft.y9.aspectj")
}

dependencies {
    api(project(":y9-digitalbase-module:y9-module-platform:risenet-y9boot-support-platform-jpa-repository"))
    api(project(":y9-digitalbase-module:y9-module-platform:risenet-y9boot-api-interface-platform"))
    api(project(":y9-digitalbase-module:y9-module-log:risenet-y9boot-api-feignclient-log"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-cache-redis"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-kafka"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-multi-tenant"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-publish-kafka"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-log"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-idgenerator"))
    api(project(":y9-digitalbase-support:risenet-y9boot-support-file-service-ftp"))
    api(project(":y9-digitalbase-support:risenet-y9boot-support-file-service-local"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-permission"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-web"))

    api(libs.dom4j)
    api(libs.jodd.http)
    api(libs.shedlock.spring)
    api(libs.shedlock.provider.jdbc.template)
    api(libs.google.guava)
    api(libs.ipaddress)
    api(libs.fastexcel)
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    compileOnly("jakarta.servlet:jakarta.servlet-api")
}

description = "risenet-y9boot-support-platform-service"
