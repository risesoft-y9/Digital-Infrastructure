plugins {
    id("net.risesoft.y9.conventions-java")
    id("net.risesoft.y9.lombok")
}

dependencies {
    api(project(":y9-digitalbase-common:risenet-y9boot-properties"))
    api(project(":y9-digitalbase-common:risenet-y9boot-common-model"))
    api(project(":y9-digitalbase-common:risenet-y9boot-common-util"))
    api(project(":y9-digitalbase-common:risenet-y9boot-common-tenant-datasource"))

    api(platform(libs.spring.boot.bom))
    api(platform(libs.ojdbc.bom))
    api("org.springframework.boot:spring-boot")
    api("org.springframework.boot:spring-boot-autoconfigure")
    api("org.springframework.boot:spring-boot-configuration-processor")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api(libs.reflections)
    api(libs.hibernate.ant)
    api(libs.mysql.connector.j)
    api(libs.mariadb.java.client)
    api("com.oracle.database.jdbc:ojdbc17")
    api(libs.postgresql)
    api(libs.dameng.dmdialect.hibernate62)
    api(libs.dameng.dmjdbcdriver18)
    api(libs.oceanbase.client)
    compileOnly("jakarta.servlet:jakarta.servlet-api")
}

description = "risenet-y9boot-starter-jpa-tenant"
