plugins {
    id("net.risesoft.y9.conventions-war")
    id("net.risesoft.y9.docker")
    id("net.risesoft.y9.lombok")
}

dependencies {
    implementation(enforcedPlatform(libs.cas.server.bom))
    //implementation(enforcedPlatform(libs.spring.bom))
    annotationProcessor(enforcedPlatform(libs.spring.bom))

    implementation("org.apereo.cas:cas-server-core")
    implementation("org.apereo.cas:cas-server-core-authentication-api")
    implementation("org.apereo.cas:cas-server-core-authentication")
    implementation("org.apereo.cas:cas-server-support-person-directory")
    implementation("org.apereo.cas:cas-server-core-rest-api")
    implementation("org.apereo.cas:cas-server-core-web-api")
    implementation("org.apereo.cas:cas-server-core-util-api")
    implementation("org.apereo.cas:cas-server-core-services-api")
    implementation("org.apereo.cas:cas-server-core-services-registry")

    implementation(libs.googlecode.cqengine) {
        exclude(group = "org.antlr", module = "antlr4-runtime")
    }
    implementation("org.antlr:antlr4-runtime:4.13.2")

    implementation("org.apereo.cas:cas-server-core-services-authentication")
    implementation("org.apereo.cas:cas-server-core-tickets-api")
    implementation("org.apereo.cas:cas-server-core-api-ticket")
    implementation("org.apereo.cas:cas-server-core-validation-api")
    implementation("org.apereo.cas:cas-server-core-webflow-api")
    implementation("org.apereo.cas:cas-server-core-cookie-api")
    implementation("org.apereo.cas:cas-server-core-cookie")
    implementation("org.apereo.cas:cas-server-support-jpa-util")
    implementation("org.apereo.cas:cas-server-support-jpa-service-registry")
    implementation("org.apereo.cas:cas-server-core-authentication-attributes")
    implementation("org.springframework.security:spring-security-config")

    //implementation("org.apereo.cas:cas-server-support-jpa-ticket-registry")

    implementation("org.apereo.cas:cas-server-support-redis-core")
    implementation("org.apereo.cas:cas-server-support-redis-modules")
    implementation("org.apereo.cas:cas-server-support-redis-ticket-registry")
    implementation("org.apereo.cas:cas-server-support-session-redis")

    implementation("org.apereo.cas:cas-server-core-api-logout")
    implementation("org.apereo.cas:cas-server-core-logout")
    implementation("org.apereo.cas:cas-server-core-logout-api")
    implementation("org.apereo.cas:cas-server-core-logging")
    implementation("org.springframework:spring-context-indexer")
    implementation(libs.uasparser)
    implementation(libs.jbcrypt)
    implementation(libs.google.zxing.core)
    implementation("org.apereo.cas:cas-server-support-token-tickets")
    implementation("org.apereo.cas:cas-server-support-token-core-api")
    implementation("org.apereo.cas:cas-server-support-oauth-webflow")
    implementation("org.apereo.cas:cas-server-support-oauth-api")
    implementation("org.apereo.cas:cas-server-support-oauth-core-api")
    implementation("org.apereo.cas:cas-server-support-oauth-services")
    //implementation("org.apereo.cas:cas-server-support-oauth")

    implementation("org.apereo.cas:cas-server-support-oidc")
    implementation("org.apereo.cas:cas-server-support-oidc-core-api")
    implementation("org.apereo.cas:cas-server-support-oidc-core")
    implementation("org.apereo.cas:cas-server-support-oidc-services")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa") {
        // 排除项目依赖
        exclude("org.springframework.boot", "spring-boot-starter-logging")
        exclude("org.antlr", "gantlr4-runtime")
    }

    implementation("org.apereo.cas:cas-server-support-pac4j-api")
    implementation("org.apereo.cas:cas-server-webapp-init")
    implementation("org.apereo.cas:cas-server-support-webconfig")

    implementation("org.apereo.cas:cas-server-webapp-starter-tomcat") {
        // 排除项目依赖
        exclude("org.springframework.cloud", "spring-cloud-config-client")
    }
    implementation(project(":y9-digitalbase-common:risenet-y9boot-common-nacos"))

    //implementation("org.springframework.boot:spring-boot-docker-compose")
    implementation(libs.mysql.connector.j)
    implementation(libs.mariadb.java.client)
    implementation(libs.postgresql)
    implementation("com.oracle.database.jdbc:ojdbc17")
    implementation(libs.kingbase.kingbase8)
    implementation(libs.kingbase.kesdialect.hibernate4)
    implementation(libs.dameng.dmdialect.hibernate62)
    implementation(libs.dameng.dmjdbcdriver18)
    implementation("org.springframework.kafka:spring-kafka")

    annotationProcessor("org.springframework:spring-context-indexer")

    configurations {
        all {
            exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
        }
    }
}

description = "risenet-y9boot-webapp-sso-server"

val finalName = "sso"

y9Docker {
    appName = finalName
}

y9War {
    archiveBaseName = finalName
}
