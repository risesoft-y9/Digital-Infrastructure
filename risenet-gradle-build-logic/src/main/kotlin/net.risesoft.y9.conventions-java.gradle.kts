plugins {
    `java-library`
    id("net.risesoft.y9.java-publish")
}

group = "net.risesoft"
version = providers.gradleProperty("Y9_VERSION").get()

java {
    sourceCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    //withJavadocJar() //com.vanniktech.maven.publish自动创建
    //withSourcesJar() //com.vanniktech.maven.publish自动创建
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    val compilerArgs = options.compilerArgs
    compilerArgs.add("-parameters")
    //compilerArgs.add("-Xlint:all")
    compilerArgs.add("-Xdiags:verbose")
    compilerArgs.add("-Xlint:-serial")
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
        properties["SKIP_TEST"].toString().toBoolean() == false
    }
    //enabled = false
}

