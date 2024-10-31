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
        api(libs.risenet.y9boot.properties)
        api(libs.risenet.y9boot.properties)
        api(libs.risenet.y9boot.properties)
        api(libs.risenet.y9boot.properties)
        api(libs.risenet.y9boot.properties)
        api(libs.risenet.y9boot.properties)
        api(libs.risenet.y9boot.properties)
        api(libs.risenet.y9boot.properties)
        api(libs.risenet.y9boot.properties)
        api(libs.risenet.y9boot.properties)
        api(libs.risenet.y9boot.properties)
        api(libs.risenet.y9boot.properties)
        api(libs.risenet.y9boot.properties)
        api(libs.risenet.y9boot.properties)
        api(libs.risenet.y9boot.properties)
        api(libs.risenet.y9boot.properties)
        api(libs.risenet.y9boot.properties)
        api(libs.risenet.y9boot.properties)
        api(libs.risenet.y9boot.properties)
        api(libs.risenet.y9boot.properties)
        api(libs.risenet.y9boot.properties)
        api(libs.risenet.y9boot.properties)
        api(libs.risenet.y9boot.properties)
        api(libs.risenet.y9boot.properties)
        api(libs.risenet.y9boot.properties)
        api(libs.risenet.y9boot.properties)
        api(libs.risenet.y9boot.properties)
        api(libs.risenet.y9boot.properties)
    }
}