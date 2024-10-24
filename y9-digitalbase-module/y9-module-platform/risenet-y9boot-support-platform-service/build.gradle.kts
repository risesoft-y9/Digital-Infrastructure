plugins {
    id("buildlogic.java-conventions")
    id("buildlogic.smart-doc")
    id("buildlogic.aspectj")
}

dependencies {
    api(project(":y9-digitalbase-module:y9-module-platform:risenet-y9boot-support-platform-jpa-repository"))
    api(project(":y9-digitalbase-module:y9-module-platform:risenet-y9boot-api-interface-platform"))
    api(project(":y9-digitalbase-module:y9-module-log:risenet-y9boot-api-feignclient-log"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-cache-redis"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-kafka"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-publish-kafka"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-log"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-idgenerator"))
    api(project(":y9-digitalbase-support:risenet-y9boot-support-file-service-ftp"))
    api(project(":y9-digitalbase-support:risenet-y9boot-support-file-service-local"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-permission"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-web"))
    api(libs.org.dom4j.dom4j)
    api(libs.org.jodd.jodd.http)
    api(libs.org.springframework.spring.aspects)
    api(libs.org.jxls.jxls.poi)
    api(libs.org.jxls.jxls.jexcel)
    api(libs.org.jxls.jxls.reader)
    api(libs.net.javacrumbs.shedlock.shedlock.spring)
    api(libs.net.javacrumbs.shedlock.shedlock.provider.jdbc.template)
    api(libs.com.google.guava.guava)
    testImplementation(libs.org.springframework.boot.spring.boot.starter.test)
    compileOnly(libs.jakarta.servlet.jakarta.servlet.api)
    compileOnly(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)
    testCompileOnly(libs.org.projectlombok.lombok)
    testAnnotationProcessor libs.org.projectlombok.lombok
}

description = "risenet-y9boot-support-platform-service"
