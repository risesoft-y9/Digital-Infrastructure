plugins {
    id("net.risesoft.y9.conventions-java")
    id("net.risesoft.y9.lombok")
}

dependencies {
    api(platform(libs.spring.boot.bom))
    //api(platform(libs.spring.cloud.alibaba.bom))

    api(project(":y9-digitalbase-common:risenet-y9boot-common-model"))

    api("com.fasterxml.jackson.core:jackson-databind")
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-properties")
    api("org.apache.commons:commons-lang3")
    api("org.slf4j:slf4j-api")
    api("org.springframework:spring-web")
    api("org.springframework:spring-context")
    api(libs.commons.io)
    api(libs.commons.beanutils)
    api(libs.commons.httpclient)
    api(libs.alibaba.transmittable.thread.local)
    api(libs.jbcrypt)
    api(libs.poi.scratchpad)
    api(libs.poi.ooxml)
    api(libs.hutool.all)
    api(libs.bcprov.ext.jdk18on)
//    api(libs.axis)
//    api(libs.commons.discovery)
//    api(libs.javax.xml.rpc.api)
//    api(libs.jaxrpc.api)
//    api(libs.jaxws.rt) {
//        exclude(group = "com.sun.xml.bind", module = "jaxb-core")
//    }
    api(libs.axis2.spring)
    api(libs.axis2.corba) {
        exclude(group = "com.sun.xml.bind", module = "jaxb-core")
    }
    api(libs.axis2.jaxws) {
        exclude(group = "com.sun.xml.bind", module = "jaxb-core")
    }

    compileOnly("jakarta.servlet:jakarta.servlet-api")
}

description = "risenet-y9boot-common-util"
