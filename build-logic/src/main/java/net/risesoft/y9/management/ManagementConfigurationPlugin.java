package net.risesoft.y9.management;

import net.risesoft.y9.maven.ProvidedOptionalScopePlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaTestFixturesPlugin;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.VariantVersionMappingStrategy;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;

public class ManagementConfigurationPlugin implements Plugin<Project> {
	public static final String MANAGEMENT_CONFIGURATION_NAME = "management";

	@Override
	public void apply(Project project) {
		ConfigurationContainer configurations = project.getConfigurations();
		configurations.create(MANAGEMENT_CONFIGURATION_NAME, (management) -> {
			management.setVisible(false);
			management.setCanBeConsumed(false);
			management.setCanBeResolved(false);

			PluginContainer plugins = project.getPlugins();
			plugins.withType(JavaPlugin.class, (javaPlugin) -> {
				configurations.getByName(JavaPlugin.COMPILE_CLASSPATH_CONFIGURATION_NAME).extendsFrom(management);
				configurations.getByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME).extendsFrom(management);
				configurations.getByName(JavaPlugin.ANNOTATION_PROCESSOR_CONFIGURATION_NAME).extendsFrom(management);
				configurations.getByName(JavaPlugin.TEST_ANNOTATION_PROCESSOR_CONFIGURATION_NAME).extendsFrom(management);
				configurations.getByName(JavaPlugin.TEST_COMPILE_CLASSPATH_CONFIGURATION_NAME).extendsFrom(management);
				configurations.getByName(JavaPlugin.TEST_RUNTIME_CLASSPATH_CONFIGURATION_NAME).extendsFrom(management);
			});

			plugins.withType(JavaTestFixturesPlugin.class, (javaTestFixturesPlugin) -> {
				configurations.getByName("testFixturesCompileClasspath").extendsFrom(management);
				configurations.getByName("testFixturesRuntimeClasspath").extendsFrom(management);
			});

			plugins.withType(MavenPublishPlugin.class, (mavenPublish) -> {
				PublishingExtension publishing = project.getExtensions().getByType(PublishingExtension.class);
				publishing.getPublications().withType(MavenPublication.class, (mavenPublication -> {
					mavenPublication.versionMapping((versions) ->
							versions.allVariants(VariantVersionMappingStrategy::fromResolutionResult)
					);
				}));
			});

			plugins.withType(ProvidedOptionalScopePlugin.class, (p -> {
				configurations.getByName("optional").extendsFrom(management);
				configurations.getByName("provided").extendsFrom(management);
			}));
		});
	}
}