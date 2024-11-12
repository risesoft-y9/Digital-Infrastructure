plugins {
    id("java-library")
    id("net.risesoft.y9.y9-publish")
}

group = "net.risesoft"

val versionCatalog = versionCatalogs.named("libs")
val y9version = versionCatalog.findVersion("y9-version")
if(y9version.isPresent) {
    version = y9version.get().displayName
} else {
    version = "v9.7.0-SNAPSHOT"
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    withJavadocJar()
    withSourcesJar()
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
}



