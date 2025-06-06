import kotlin.Boolean

plugins {
    `java-platform`
    id("net.risesoft.y9.javaPlatform-publish")
}

description = "y9-digitalbase-bom"

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