plugins {
    id("buildlogic.java-conventions")
    id("buildlogic.docker")
    id("war")
    alias(libs.plugins.org.springframework.boot)
}

dependencies {
    api(libs.org.apereo.cas.cas.server.core)
    api(libs.org.apereo.cas.cas.server.core.authentication.api)
    api(libs.org.apereo.cas.cas.server.core.authentication)
    api(libs.org.apereo.cas.cas.server.support.person.directory)
    api(libs.org.apereo.cas.cas.server.core.rest.api)
    api(libs.org.apereo.cas.cas.server.core.web.api)
    api(libs.org.apereo.cas.cas.server.core.util.api)
    api(libs.org.apereo.cas.cas.server.core.services.api)
    api(libs.org.apereo.cas.cas.server.core.services.registry)
    api(libs.com.googlecode.cqengine.cqengine)
    api(libs.org.antlr.antlr4.runtime)
    api(libs.org.apereo.cas.cas.server.core.services.authentication)
    api(libs.org.apereo.cas.cas.server.core.tickets.api)
    api(libs.org.apereo.cas.cas.server.core.api.ticket)
    api(libs.org.apereo.cas.cas.server.core.validation.api)
    api(libs.org.apereo.cas.cas.server.core.webflow.api)
    api(libs.org.apereo.cas.cas.server.core.cookie.api)
    api(libs.org.apereo.cas.cas.server.core.cookie)
    api(libs.org.apereo.cas.cas.server.support.jpa.util)
    api(libs.org.apereo.cas.cas.server.support.jpa.service.registry)
    api(libs.org.apereo.cas.cas.server.core.authentication.attributes)
    api(libs.org.springframework.security.spring.security.config)
    api(libs.org.apereo.cas.cas.server.support.ldap.core)
    api(libs.org.hibernate.orm.hibernate.core)
    api(libs.org.apereo.cas.cas.server.support.redis.core)
    api(libs.org.apereo.cas.cas.server.support.redis.ticket.registry)
    api(libs.org.apereo.cas.cas.server.support.session.redis)
    api(libs.org.springframework.data.spring.data.redis)
    api(libs.com.redis.lettucemod)
    api(libs.org.apereo.cas.cas.server.core.api.logout)
    api(libs.org.apereo.cas.cas.server.core.logout)
    api(libs.org.apereo.cas.cas.server.core.logout.api)
    api(libs.org.apereo.cas.cas.server.core.logging)
    api(libs.org.springframework.spring.context.indexer)
    api(libs.cz.mallat.uasparser.uasparser)
    api(libs.de.svenkubiak.jbcrypt)
    api(libs.com.google.zxing.core)
    api(libs.org.apereo.cas.cas.server.support.token.tickets)
    api(libs.org.apereo.cas.cas.server.support.token.core.api)
    api(libs.org.apereo.cas.cas.server.support.oauth.webflow)
    api(libs.org.apereo.cas.cas.server.support.oauth.api)
    api(libs.org.apereo.cas.cas.server.support.oauth.core.api)
    api(libs.org.apereo.cas.cas.server.support.oauth.core)
    api(libs.org.apereo.cas.cas.server.support.oauth.services)
    api(libs.org.apereo.cas.cas.server.support.pac4j.api)
    api(libs.org.apereo.cas.cas.server.webapp.init)
    api(libs.org.apereo.cas.cas.server.support.webconfig)
    api(libs.org.apereo.cas.cas.server.webapp.starter.tomcat)
    api(project(":y9-digitalbase-common:risenet-y9boot-common-nacos"))
    api(libs.org.springframework.boot.spring.boot.docker.compose)
    api(libs.com.mysql.mysql.connector.j)
    api(libs.org.mariadb.jdbc.mariadb.java.client)
    api(libs.org.postgresql.postgresql)
    api(libs.com.oracle.database.jdbc.ojdbc11)
    api(libs.cn.com.kingbase.kingbase8)
    api(libs.cn.com.kingbase.kesdialect.for.hibernate4)
    api(libs.com.dameng.dmdialect.for.hibernate5.v6)
    api(libs.com.dameng.dmjdbcdriver18)
    providedCompile libs.org.projectlombok.lombok.x1
    providedRuntime libs.org.springframework.boot.spring.boot.starter.tomcat
    annotationProcessor(libs.org.projectlombok.lombok.x1)
}

description = "risenet-y9boot-webapp-sso-server"

ext {
    finalName = "sso"
}

jib.container.appRoot = "/usr/local/tomcat/webapps/${finalName}"

tasks.bootWar {
    archiveFileName.set("${finalName}.${archiveExtension.get()}")
}