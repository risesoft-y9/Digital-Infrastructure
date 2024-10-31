plugins {
    id("buildlogic.java-conventions")
    id("buildlogic.smart-doc")
}

dependencies {
    api(project(":y9-digitalbase-module:y9-module-platform:risenet-y9boot-support-platform-service"))
    api(project(":y9-digitalbase-module:y9-module-platform:risenet-y9boot-support-platform-jpa-repository"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-web"))
    api(project(":y9-digitalbase-module:y9-module-idcode:risenet-y9boot-idcode"))
    api(libs.org.jodd.jodd.http)
    api(libs.org.apache.commons.commons.lang3)
    api(libs.org.hibernate.validator.hibernate.validator)
    api(libs.com.google.guava.guava)
    api(libs.org.dom4j.dom4j)
    testImplementation(libs.org.springframework.boot.spring.boot.starter.test)
    compileOnly(libs.jakarta.servlet.jakarta.servlet.api)
    compileOnly(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)
    testCompileOnly(libs.org.projectlombok.lombok)
    testAnnotationProcessor(libs.org.projectlombok.lombok)
}

description = "risenet-y9boot-support-platform-web"