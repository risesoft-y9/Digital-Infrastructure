plugins {
    id("net.risesoft.y9.java-conventions")
    id("net.risesoft.y9.lombok")
    id("net.risesoft.y9.smart-doc")
}

dependencies {
    management(platform(project(":y9-digitalbase-dependencies")))

    api(project(":y9-digitalbase-module:y9-module-platform:risenet-y9boot-support-platform-service"))
    api(project(":y9-digitalbase-module:y9-module-platform:risenet-y9boot-support-platform-jpa-repository"))
    api(project(":y9-digitalbase-starter:risenet-y9boot-starter-web"))
    api(project(":y9-digitalbase-module:y9-module-idcode:risenet-y9boot-idcode"))

    api(libs.jodd.http)
    api(libs.commons.lang3)
    api(libs.hibernate.validator)
    api(libs.google.guava)
    api(libs.dom4j)
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    compileOnly("jakarta.servlet:jakarta.servlet-api")
}

description = "risenet-y9boot-support-platform-web"
