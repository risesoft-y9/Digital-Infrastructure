val management = configurations.create("management")
val provided = configurations.create("provided")
val optional = configurations.create("optional")

configurations.named("management") {
    isVisible = false
    isCanBeResolved = false
    isCanBeConsumed = false
}

plugins.withType<JavaPlugin> {
    configurations.getByName(JavaPlugin.COMPILE_CLASSPATH_CONFIGURATION_NAME).extendsFrom(management);
    configurations.getByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME).extendsFrom(management);
    configurations.getByName(JavaPlugin.ANNOTATION_PROCESSOR_CONFIGURATION_NAME).extendsFrom(management);
    configurations.getByName(JavaPlugin.TEST_ANNOTATION_PROCESSOR_CONFIGURATION_NAME).extendsFrom(management);
    configurations.getByName(JavaPlugin.TEST_COMPILE_CLASSPATH_CONFIGURATION_NAME).extendsFrom(management);
    configurations.getByName(JavaPlugin.TEST_RUNTIME_CLASSPATH_CONFIGURATION_NAME).extendsFrom(management);
};

plugins.withType<WarPlugin> {
    configurations.getByName("providedCompile").extendsFrom(management);
    configurations.getByName("providedRuntime").extendsFrom(management);
}

plugins.withType<JavaTestFixturesPlugin> {
    configurations.getByName("testFixturesCompileClasspath").extendsFrom(management);
    configurations.getByName("testFixturesRuntimeClasspath").extendsFrom(management);
}

tasks.named<Javadoc>(JavaPlugin.JAVADOC_TASK_NAME) {
    classpath = classpath.plus(provided).plus(optional)
}

configurations.named("provided") {
    extendsFrom(configurations.getByName("implementation"))
    plugins.withType<JavaLibraryPlugin> {
        extendsFrom(configurations.getByName("api"))
    }
    extendsFrom(management)
}

configurations.named("optional") {
    extendsFrom(configurations.getByName("implementation"))
    plugins.withType<JavaLibraryPlugin> {
        extendsFrom(configurations.getByName("api"))
    }
    extendsFrom(management)
}

val java = project.extensions.getByType<JavaPluginExtension>()
java.sourceSets.forEach { t ->
    t.compileClasspath +=  optional.plus(provided)
    t.runtimeClasspath +=  optional.plus(provided)
}

//val dependency = project.dependencies.platform(project.dependencies.project(":y9-digitalbase-dependencies"))
//val dependency = project.dependencies.platform("net.risesoft.y9:y9-digitalbase-dependencies:0.0.1")
//management.dependencies.add(dependency)
