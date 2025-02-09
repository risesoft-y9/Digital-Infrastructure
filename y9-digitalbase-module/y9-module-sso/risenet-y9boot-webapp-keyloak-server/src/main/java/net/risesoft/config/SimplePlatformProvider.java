package net.risesoft.config;

import java.io.File;

import org.keycloak.Config.Scope;
import org.keycloak.common.Profile;
import org.keycloak.common.profile.PropertiesFileProfileConfigResolver;
import org.keycloak.common.profile.PropertiesProfileConfigResolver;
import org.keycloak.platform.PlatformProvider;
import org.keycloak.services.ServicesLogger;

public class SimplePlatformProvider implements PlatformProvider {
    
    public SimplePlatformProvider() {
        Profile.configure(new PropertiesProfileConfigResolver(System.getProperties()), new PropertiesFileProfileConfigResolver());
    }

	Runnable shutdownHook;

	@Override
	public void onStartup(Runnable startupHook) {
		startupHook.run();
	}

	@Override
	public void onShutdown(Runnable shutdownHook) {
		this.shutdownHook = shutdownHook;
	}

	@Override
	public void exit(Throwable cause) {
		ServicesLogger.LOGGER.fatal(cause);
		exit(1);
	}

	private void exit(int status) {
		new Thread() {
			@Override
			public void run() {
				System.exit(status);
			}
		}.start();
	}

	@Override
	public File getTmpDirectory() {
		return new File(System.getProperty("java.io.tmpdir"));
	}

    @Override
    public ClassLoader getScriptEngineClassLoader(Scope scriptProviderConfig) {
        return null;
    }

    @Override
    public String name() {
        return "oauth-authorization-server";
    }

}