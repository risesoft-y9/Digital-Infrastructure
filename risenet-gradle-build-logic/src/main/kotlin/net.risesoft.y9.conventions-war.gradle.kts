plugins {
    war
    id("net.risesoft.y9.java-publish")
    id("org.springframework.boot")
}

group = "net.risesoft"
version = providers.gradleProperty("Y9_VERSION").get()

dependencies {
    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat:3.4.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
//    withJavadocJar()
//    withSourcesJar()
}

tasks.withType<JavaCompile> {
    //sourceCompatibility = JavaVersion.VERSION_21.toString()
    //targetCompatibility = JavaVersion.VERSION_21.toString()
    options.encoding = "UTF-8"
    val compilerArgs = options.compilerArgs
    compilerArgs.add("-parameters")
    compilerArgs.add("-Xlint:all")
    compilerArgs.add("-Xdiags:verbose")
}

tasks.withType<Javadoc> {
    //enabled = false
    isFailOnError = false
    val doclet = options as StandardJavadocDocletOptions
    doclet.encoding = "UTF-8"
    doclet.docEncoding = "UTF-8"
    doclet.addBooleanOption("html5", true)
    //doclet.addStringOption("Xdoclint:all,-missing", "-quiet")
    doclet.addStringOption("Xdoclint:none", "-quiet")  // 禁用所有文档检查
}

tasks.test {
    useJUnitPlatform()
    //exclude("**/*")
}

tasks.withType<Test> { // OR tasks.test {
    onlyIf {
        properties["SKIP_TEST"].toString().toBoolean()==false
    }
    //enabled = false
}

interface Y9WarPluginExtension {
    val archiveBaseName: Property<String>
}

val extension = project.extensions.create<Y9WarPluginExtension>("y9War")

project.afterEvaluate {
    tasks.bootWar {
        archiveFileName.set("${extension.archiveBaseName.getOrElse(project.name)}.${archiveExtension.get()}")
    }

    tasks.war {
        archiveFileName.set("${extension.archiveBaseName.getOrElse(project.name)}-plain.${archiveExtension.get()}")
    }

}
