import io.freefair.gradle.plugins.aspectj.AjcAction

plugins {
    id("java-library")
    id("io.freefair.aspectj.post-compile-weaving")
}

val versionCatalog = versionCatalogs.named("libs")


dependencies {
    aspect(platform(versionCatalog.findLibrary("spring-boot-bom").get()))
    testAspect(platform(versionCatalog.findLibrary("spring-boot-bom").get()))

    api(versionCatalog.findLibrary("org-aspectj-aspectjrt").get())
    api(versionCatalog.findLibrary("org-aspectj-aspectjtools").get())
    api(versionCatalog.findLibrary("org-aspectj-aspectjweaver").get())

    aspect("org.springframework:spring-aspects")
    testAspect("org.springframework:spring-aspects")
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