package net.risesoft.y9.maven;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.javadoc.Javadoc;
import net.risesoft.y9.management.ManagementConfigurationPlugin;

/**
 * Plugin to allow 'optional' and 'provided' dependency configurations
 *
 * As stated in the maven documentation, provided scope "is only available on the compilation and test classpath,
 * and is not transitive".
 *
 * This plugin creates two new configurations, and each one:
 * <ul>
 * <li>is a parent of the compile configuration</li>
 * <li>is not visible, not transitive</li>
 * <li>all dependencies are excluded from the default configuration</li>
 * </ul>
 *
 * @author Phillip Webb
 * @author Brian Clozel
 * @author Rob Winch
 * @author Steve Riesenberg
 *
 * @see <a href="https://www.gradle.org/docs/current/userguide/java_plugin.html#N121CF">Maven documentation</a>
 * @see <a href="https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#Dependency_Scope">Gradle configurations</a>
 * @see ProvidedOptionalScopeEclipsePlugin
 * @see ProvidedOptionalScopeIdeaPlugin
 */
public class ProvidedOptionalScopePlugin implements Plugin<Project> {
	@Override
	public void apply(Project project) {
		project.getPlugins().withType(JavaPlugin.class, (javaPlugin) -> {
			Configuration provided = addConfiguration(project, "provided");
			Configuration optional = addConfiguration(project, "optional");

			Javadoc javadoc = (Javadoc) project.getTasks().getByName(JavaPlugin.JAVADOC_TASK_NAME);
			javadoc.setClasspath(javadoc.getClasspath().plus(provided).plus(optional));
		});
	}

	private Configuration addConfiguration(Project project, String name) {
		Configuration configuration = project.getConfigurations().create(name);
		configuration.extendsFrom(project.getConfigurations().getByName("implementation"));
		project.getPlugins().withType(JavaLibraryPlugin.class, (javaLibraryPlugin) ->
				configuration.extendsFrom(project.getConfigurations().getByName("api")));
		project.getPlugins().withType(ManagementConfigurationPlugin.class, (managementConfigurationPlugin) ->
				configuration.extendsFrom(project.getConfigurations().getByName("management")));

		JavaPluginExtension java = project.getExtensions().getByType(JavaPluginExtension.class);
		java.getSourceSets().all((sourceSet) -> {
			sourceSet.setCompileClasspath(sourceSet.getCompileClasspath().plus(configuration));
			sourceSet.setRuntimeClasspath(sourceSet.getRuntimeClasspath().plus(configuration));
		});

		return configuration;
	}
}
