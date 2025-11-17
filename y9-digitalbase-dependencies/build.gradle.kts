plugins {
    `java-platform`
    id("net.risesoft.y9.javaPlatform-publish")
}

javaPlatform {
    allowDependencies()
}

description = "y9-digitalbase-dependencies"

dependencies {
    //bom
    api(platform(libs.spring.boot.bom))
    api(platform(libs.spring.cloud.bom))
    api(platform(libs.cas.server.bom))
    api(platform(libs.spring.cloud.alibaba.bom))
    api(platform(libs.ojdbc.bom))

    constraints {
        api(libs.alibaba.transmittable.thread.local)
        api(libs.hutool.all)
        api(libs.commons.beanutils)
        api(libs.commons.codec)
        api(libs.commons.httpclient)
        api(libs.commons.io)
        api(libs.commons.net)
        api(libs.httpcomponents.httpclient)
        api(libs.httpcomponents.httpmime)
        api(libs.jbcrypt)
        api(libs.bcprov.ext.jdk18on)
        api(libs.poi.scratchpad)
        api(libs.poi.ooxml)
        api(libs.java.uuid.generator)
        api(libs.kingbase.kesdialect.hibernate4)
        api(libs.kingbase.kingbase8)
        api(libs.dameng.dmdialect.hibernate62)
        api(libs.dameng.dmjdbcdriver18)
        api(libs.h2database)
        api(libs.mysql.connector.j)
        api(libs.oceanbase.client)
        api(libs.google.gson)
        api(libs.google.guava)
        api(libs.google.zxing.core)
        api(libs.googlecode.cqengine)
        api(libs.etcd.java)
        api(libs.lettucemod)
        api(libs.imageio.jpeg)
        api(libs.uasparser)
        api(libs.ipaddress)
        api(libs.reflections)
        api(libs.classgraph)
        api(libs.micrometer.registry.prometheus)
        api(libs.jaxen)
        api(libs.shedlock.spring)
        api(libs.shedlock.provider.jdbc.template)
        api(libs.dom4j)
        api(libs.hibernate.ant)
        api(libs.hibernate.core)
        api(libs.hibernate.validator)
        api(libs.imgscalr.lib)
        api(libs.javers.spring.boot.starter.sql)
        api(libs.jodd.http)
        api(libs.liquibase.core)
        api(libs.mariadb.java.client)
        api(libs.antisamy)
        api(libs.postgresql)
        api(libs.auth0.jwt)
        api(libs.auth0.jwks)
        api(libs.fastexcel)
    }
}
