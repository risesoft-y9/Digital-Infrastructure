import io.freefair.gradle.plugins.aspectj.AjcAction

plugins {
    id("java-library")
    id("io.freefair.aspectj.post-compile-weaving")
}

dependencies {
    api("org.aspectj:aspectjrt:1.9.24")
    api("org.aspectj:aspectjtools:1.9.24")
    api("org.aspectj:aspectjweaver:1.9.24")

    aspect("org.springframework:spring-aspects:6.2.8")
    testAspect("org.springframework:spring-aspects:6.2.8")
}

interface Y9AspectjPluginExtension {
    val aspectjVersion: Property<String>
}

// Add the 'greeting' extension object to project
val extension = project.extensions.create<Y9AspectjPluginExtension>("y9Aspectj")

// Set a default value for 'message'
extension.aspectjVersion.convention("1.9.24")

tasks.named("compileJava") {
    configure<AjcAction> {
        enabled = true
        classpath
        options {
            aspectpath.setFrom(configurations.aspect)
            compilerArgs = listOf("-showWeaveInfo", "-verbose")
        }
    }
}

tasks.named("compileTestJava") {
    configure<AjcAction> {
        enabled = true
        classpath
        options {
            aspectpath.setFrom(configurations.aspect)
            compilerArgs = listOf("-showWeaveInfo", "-verbose")
        }
    }
}