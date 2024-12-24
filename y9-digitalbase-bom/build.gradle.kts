import kotlin.Boolean

plugins {
    `java-platform`
    id("net.risesoft.y9.javaPlatform-publish")
    id("net.risesoft.y9.javaPlatform-publish-central")
}

group = "net.risesoft"
description = "y9-digitalbase-bom"
version = findProperty("Y9BOM_VERSION") as String? ?: "v9.7.0-01"
println("version=$version")

fun matchProjectNamePattern(project: Project): Boolean {
    return project.name.matches("^risenet-y9boot-(properties|common|api|starter|support|idcode).*".toRegex())
}

dependencies {
    constraints {
        rootProject.subprojects.forEach { p ->
            if(matchProjectNamePattern(p)) {
                api(p)
            }
        }
    }
}