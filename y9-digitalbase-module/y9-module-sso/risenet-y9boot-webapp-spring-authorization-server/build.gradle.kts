plugins {
	java
	id("io.freefair.lombok") version "8.13.1"
	id("org.springframework.boot") version "3.5.0"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "net.risesoft"
version = "9.7.0"

java {
	sourceCompatibility = JavaVersion.VERSION_21
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

extra["springBootAdminVersion"] = "3.4.3"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server")
	implementation("org.springframework.boot:spring-boot-starter-security")
	//implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-web")
	//implementation("de.codecentric:spring-boot-admin-starter-server")
	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
	implementation("org.apache.commons:commons-lang3")
	implementation("commons-codec:commons-codec")
	implementation("cz.mallat.uasparser:uasparser:0.6.2")
	implementation("de.svenkubiak:jBCrypt:0.4.3")
	implementation("com.google.zxing:core:3.5.3")
	implementation("com.zaxxer:HikariCP")

	runtimeOnly("com.h2database:h2")
	runtimeOnly("com.mysql:mysql-connector-j")
	runtimeOnly("org.postgresql:postgresql")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
	imports {
		mavenBom("de.codecentric:spring-boot-admin-dependencies:${property("springBootAdminVersion")}")
	}
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
