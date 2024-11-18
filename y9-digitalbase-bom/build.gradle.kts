import kotlin.Boolean

plugins {
    id("java-platform")
    id("net.risesoft.y9.javaPlatform-publish")
}

group = "net.risesoft"
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

/*publishing {
    publications {
        named<MavenPublication>("mavenJava") {
            from(components["javaPlatform"])
            pom {
                packaging = "pom"
            }
        }
    }
}*/




