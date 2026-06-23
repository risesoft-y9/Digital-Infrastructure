import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    id("io.freefair.lombok") version "8.13.1"
    id("org.springframework.boot") version "3.5.14"
    id("io.spring.dependency-management") version "1.1.7"
}

description = "risenet-y9boot-webapp-keycloak-server"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    maven {
        url = uri("https://maven.aliyun.com/repository/public")
    }
    mavenCentral()
}

val keycloakVersion = "24.0.5"
val resteasyVersion = "6.2.16.Final"

dependencies {
    // web
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // persistence
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.mysql:mysql-connector-j")

    // Keycloak server
    implementation("org.jboss.resteasy:resteasy-jackson2-provider:$resteasyVersion")
    implementation("org.jboss.resteasy:resteasy-multipart-provider:$resteasyVersion")

    implementation(platform("org.keycloak:keycloak-dependencies-server-all:$keycloakVersion"))
    implementation("org.keycloak:keycloak-crypto-default:$keycloakVersion")
    implementation("org.keycloak:keycloak-admin-ui:$keycloakVersion")
    implementation("org.keycloak:keycloak-services:$keycloakVersion")
    implementation("org.keycloak:keycloak-rest-admin-ui-ext:$keycloakVersion")

    // Liquibase
    implementation("org.liquibase:liquibase-core") {
        exclude(group = "org.yaml", module = "snakeyaml")
        exclude(group = "org.apache.commons", module = "commons-text")
    }

    // config properties processor
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("org.junit.vintage:junit-vintage-engine")
}

tasks.withType<BootJar> {
    mainClass.set("net.risesoft.AuthorizationServerApp")
}


tasks.test {
    useJUnitPlatform()
    //exclude("**/*")
}

tasks.withType<Test> { // OR tasks.test {
    onlyIf {
        properties["SKIP_TEST"].toString().toBoolean() == false
    }
    //enabled = false
}
// 跳过 Maven 发布
tasks.withType<PublishToMavenRepository> {
    enabled = false
}