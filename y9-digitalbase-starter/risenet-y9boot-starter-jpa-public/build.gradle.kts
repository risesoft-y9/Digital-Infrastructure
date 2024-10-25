plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    api(project(":y9-digitalbase-common:risenet-y9boot-common-util"))
    api(libs.org.springframework.boot.spring.boot)
    api(libs.org.springframework.boot.spring.boot.autoconfigure)
    api(libs.org.springframework.boot.spring.boot.configuration.processor)
    api(libs.org.springframework.boot.spring.boot.starter.data.jpa)
    api(libs.com.mysql.mysql.connector.j)
    api(libs.org.mariadb.jdbc.mariadb.java.client.x1)
    api(libs.com.oracle.database.jdbc.ojdbc11.x1)
    api(libs.org.postgresql.postgresql)
    api(libs.com.dameng.dmdialect.for.hibernate5.v6)
    api(libs.com.dameng.dmjdbcdriver18)
    api(libs.cn.com.kingbase.kingbase8)
    api(libs.cn.com.kingbase.hibernate.dialect)
    api(libs.cn.com.kingbase.hibernate.jpa.api)
    api(libs.com.oceanbase.oceanbase.client)
    compileOnly(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)
}

description = "risenet-y9boot-starter-jpa-public"
