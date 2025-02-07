pluginManagement {
    includeBuild("risenet-gradle-build-logic")
    includeBuild("risenet-gradle-version-catalog")

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    /*versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
            //from("net.risesoft.y9:risenet-gradle-version-catalog:0.0.3")
        }
    }*/
}

rootProject.name = "y9-digitalbase"

include(":y9-digitalbase-bom")
include(":y9-digitalbase-dependencies")

include(":y9-digitalbase-common:risenet-y9boot-properties")
include(":y9-digitalbase-common:risenet-y9boot-common-model")
include(":y9-digitalbase-common:risenet-y9boot-common-nacos")
include(":y9-digitalbase-common:risenet-y9boot-common-util")
include(":y9-digitalbase-common:risenet-y9boot-common-tenant-datasource")
include(":y9-digitalbase-common:risenet-y9boot-common-sqlddl")
include(":y9-digitalbase-common:risenet-y9boot-3rd-jpa")

include(":y9-digitalbase-starter:risenet-y9boot-starter-apisix")
include(":y9-digitalbase-starter:risenet-y9boot-starter-cache-redis")
include(":y9-digitalbase-starter:risenet-y9boot-starter-idgenerator")
include(":y9-digitalbase-starter:risenet-y9boot-starter-jpa-public")
include(":y9-digitalbase-starter:risenet-y9boot-starter-jpa-tenant")
include(":y9-digitalbase-starter:risenet-y9boot-starter-jpa-dedicated")
include(":y9-digitalbase-starter:risenet-y9boot-starter-kafka")
include(":y9-digitalbase-starter:risenet-y9boot-starter-liquibase")
include(":y9-digitalbase-starter:risenet-y9boot-starter-publish-kafka")
include(":y9-digitalbase-starter:risenet-y9boot-starter-listener-kafka")
include(":y9-digitalbase-starter:risenet-y9boot-starter-multi-tenant")
include(":y9-digitalbase-starter:risenet-y9boot-starter-log")
include(":y9-digitalbase-starter:risenet-y9boot-starter-openfeign")
include(":y9-digitalbase-starter:risenet-y9boot-starter-permission")
include(":y9-digitalbase-starter:risenet-y9boot-starter-security")
include(":y9-digitalbase-starter:risenet-y9boot-starter-sso-oauth2-resource")
include(":y9-digitalbase-starter:risenet-y9boot-starter-web")
include(":y9-digitalbase-starter:risenet-y9boot-starter-elasticsearch")

include(":y9-digitalbase-support:risenet-y9boot-support-file-jpa-repository")
include(":y9-digitalbase-support:risenet-y9boot-support-file-service-local")
include(":y9-digitalbase-support:risenet-y9boot-support-file-service-rest")
include(":y9-digitalbase-support:risenet-y9boot-support-file-service-ftp")
include(":y9-digitalbase-support:risenet-y9boot-support-history")

include(":y9-digitalbase-idcode:risenet-y9boot-idcode")

include(":y9-digitalbase-module:y9-module-filemanager:risenet-y9boot-webapp-filemanager")

include(":y9-digitalbase-module:y9-module-log:risenet-y9boot-api-interface-log")
include(":y9-digitalbase-module:y9-module-log:risenet-y9boot-api-feignclient-log")
include(":y9-digitalbase-module:y9-module-log:risenet-y9boot-support-log-elasticsearch-repository")
include(":y9-digitalbase-module:y9-module-log:risenet-y9boot-support-log-jpa-repository")
include(":y9-digitalbase-module:y9-module-log:risenet-y9boot-support-log-service")
include(":y9-digitalbase-module:y9-module-log:risenet-y9boot-support-log-web")
include(":y9-digitalbase-module:y9-module-log:risenet-y9boot-webapp-log")

include(":y9-digitalbase-module:y9-module-platform:risenet-y9boot-api-interface-platform")
include(":y9-digitalbase-module:y9-module-platform:risenet-y9boot-api-feignclient-platform")
include(":y9-digitalbase-module:y9-module-platform:risenet-y9boot-support-platform-jpa-repository")
include(":y9-digitalbase-module:y9-module-platform:risenet-y9boot-support-platform-service")
include(":y9-digitalbase-module:y9-module-platform:risenet-y9boot-support-platform-web")
include(":y9-digitalbase-module:y9-module-platform:risenet-y9boot-webapp-platform")

include(":y9-digitalbase-module:y9-module-sso:risenet-y9boot-webapp-cas-server")
include(":y9-digitalbase-module:y9-module-sso:risenet-y9boot-webapp-sso-server")
include(":y9-digitalbase-module:y9-module-sso:risenet-y9boot-webapp-spring-authorization-server")