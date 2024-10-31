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
    }
}