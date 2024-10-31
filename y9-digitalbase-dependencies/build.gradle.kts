plugins {
    id("java-platform")
    id("buildlogic.y9-repository")
}

javaPlatform {
    allowDependencies()
}

description = "y9-digitalbase-dependencies"

dependencies {
    constraints {
        api(libs.risenet.y9boot.properties)
        api(libs.risenet.y9boot.common.model)
        api(libs.risenet.y9boot.common.util)
        api(libs.risenet.y9boot.common.tenant.datasource)
        api(libs.risenet.y9boot.third.jpa)
        api(libs.risenet.y9boot.common.sqlddl)
        api(libs.risenet.y9boot.idcode)
        api(libs.risenet.y9boot.starter.cache.redis)
        api(libs.risenet.y9boot.starter.idgenerator)
        api(libs.risenet.y9boot.starter.jpa.public)
        api(libs.risenet.y9boot.starter.kafka)
        api(libs.risenet.y9boot.starter.liquibase)
        api(libs.risenet.y9boot.starter.publish.kafka)
        api(libs.risenet.y9boot.starter.listener.kafka)
        api(libs.risenet.y9boot.starter.multi.tenant)
        api(libs.risenet.y9boot.starter.log)
        api(libs.risenet.y9boot.starter.permission)
        api(libs.risenet.y9boot.starter.security)
        api(libs.risenet.y9boot.starter.sso.oauth2.resource)
        api(libs.risenet.y9boot.starter.elasticsearch)
        api(libs.risenet.y9boot.starter.web)
        api(libs.risenet.y9boot.starter.openfeign)
        api(libs.risenet.y9boot.support.file.jpa.repository)
        api(libs.risenet.y9boot.support.file.service.ftp)
        api(libs.risenet.y9boot.support.file.service.local)
        api(libs.risenet.y9boot.support.file.service.rest)
        api(libs.risenet.y9boot.support.history)
        api(libs.risenet.y9boot.api.feignclient.log)
        api(libs.risenet.y9boot.apiinterface.log)
        api(libs.risenet.y9boot.support.log.elasticsearch.repository)
        api(libs.risenet.y9boot.support.log.jpa.repository)
        api(libs.risenet.y9boot.support.log.service)
        api(libs.risenet.y9boot.support.log.web)
        api(libs.risenet.y9boot.api.feignclient.platform)
        api(libs.risenet.y9boot.apiinterface.platform)
        api(libs.risenet.y9boot.support.platform.jpa.repository)
        api(libs.risenet.y9boot.support.platform.service)
        api(libs.risenet.y9boot.support.platform.web)
    }
}