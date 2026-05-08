import io.freefair.gradle.plugins.aspectj.AjcAction

plugins {
    id("java-library")
    id("io.freefair.aspectj.post-compile-weaving")
}

interface Y9AspectjPluginExtension {
    val aspectjVersion: Property<String>
    val springAspectsVersion: Property<String>
}

// Add the 'y9Aspectj' extension object to project
val extension = project.extensions.create<Y9AspectjPluginExtension>("y9Aspectj")

// Set default values
extension.aspectjVersion.convention("1.9.25.1")
extension.springAspectsVersion.convention("6.2.18") // Spring Boot 3.5.14 corresponds to Spring Framework 6.2.18

dependencies {
    api("org.aspectj:aspectjrt:${extension.aspectjVersion.get()}")
    api("org.aspectj:aspectjtools:${extension.aspectjVersion.get()}")
    api("org.aspectj:aspectjweaver:${extension.aspectjVersion.get()}")

    aspect("org.springframework:spring-aspects:${extension.springAspectsVersion.get()}")
    testAspect("org.springframework:spring-aspects:${extension.springAspectsVersion.get()}")
}

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