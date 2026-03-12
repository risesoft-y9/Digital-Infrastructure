package net.risesoft.y9.spring.boot;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.Banner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义 banner，打印一些有用信息
 *
 * @author shidaobang
 * @date 2026/03/11
 */
@Slf4j
public class Y9Banner implements Banner {

    private static final Properties GIT_PROPERTIES;

    private static final String TITLE = "  ____  _          ____         __ _        _                \n"
        + " |  _ \\(_)___  ___/ ___|  ___  / _| |_     / \\   _ __  _ __  \n"
        + " | |_) | / __|/ _ \\___ \\ / _ \\| |_| __|   / _ \\ | '_ \\| '_ \\ \n"
        + " |  _ <| \\__ \\  __/___) | (_) |  _| |_   / ___ \\| |_) | |_) |\n"
        + " |_| \\_\\_|___/\\___|____/ \\___/|_|  \\__| /_/   \\_\\ .__/| .__/ \n"
        + "                                                |_|   |_|    ";

    static {
        GIT_PROPERTIES = loadGitProperties();
    }

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
        String applicationName = environment.getProperty("spring.application.name");
        String applicationVersion = GIT_PROPERTIES.getProperty("git.build.version");
        String profile = environment.getProperty("spring.profiles.active", "default");
        String port = environment.getProperty("server.port");
        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];

        String gitBranch = GIT_PROPERTIES.getProperty("git.branch");
        String gitCommit = GIT_PROPERTIES.getProperty("git.commit.id.full");
        String gitBuildTime = GIT_PROPERTIES.getProperty("git.build.time");

        Runtime runtime = Runtime.getRuntime();
        int cpuCores = runtime.availableProcessors();
        String jvmFreeMemory = FileUtils.byteCountToDisplaySize(runtime.freeMemory());
        String jvmMaxMemory = FileUtils.byteCountToDisplaySize(runtime.maxMemory());
        String jvmTotalMemory = FileUtils.byteCountToDisplaySize(runtime.totalMemory());

        String javaHome = System.getProperty("java.home");
        String javaVendor = System.getProperty("java.vendor");
        String javaVersion = System.getProperty("java.version");

        String osArchitecture = System.getProperty("os.arch");
        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        LocalDateTime osDate = LocalDateTime.now(ZoneId.systemDefault());

        out.println();
        out.println(TITLE);
        out.println(" Application : " + applicationName);
        out.println(" Version : " + applicationVersion);
        out.println(" Profile : " + profile);
        out.println(" Port : " + port);
        out.println(" PID : " + pid);

        out.println(" Git Branch : " + gitBranch);
        out.println(" Git Commit : " + gitCommit);
        out.println(" Git Build Time : " + gitBuildTime);

        out.println(" CPU Cores : " + cpuCores);
        out.println(" JVM Free Memory : " + jvmFreeMemory);
        out.println(" JVM Maximum Memory : " + jvmMaxMemory);
        out.println(" JVM Total Memory : " + jvmTotalMemory);

        out.println(" Java Home : " + javaHome);
        out.println(" Java Vendor : " + javaVendor);
        out.println(" Java Version : " + javaVersion);

        out.println(" OS Architecture : " + osArchitecture);
        out.println(" OS Name : " + osName);
        out.println(" OS Version : " + osVersion);
        out.println(" OS Date/Time : " + osDate);
        out.println("==============================================================");
        out.println();
    }

    @SneakyThrows
    private static Properties loadGitProperties() {
        Properties properties = new Properties();
        ClassPathResource resource = new ClassPathResource("git.properties");
        if (doesResourceExist(resource)) {
            Properties loaded = PropertiesLoaderUtils.loadProperties(resource);
            for (String key : loaded.stringPropertyNames()) {
                properties.put(key, loaded.get(key));
            }
        }
        return properties;
    }

    /**
     * Does resource exist?
     * <p>
     * On Windows, reading one byte from a directory does not return length greater than zero so an explicit directory
     * check is needed.
     * 
     * @param res the res
     * @return true/false
     */
    public static boolean doesResourceExist(final Resource res) {
        if (res == null) {
            return false;
        }
        try {
            if (res.isFile() && FileUtils.isDirectory(res.getFile())) {
                return true;
            }
            try (InputStream input = res.getInputStream()) {
                IOUtils.read(input, new byte[1]);
                return res.contentLength() > 0;
            }
        } catch (final FileNotFoundException e) {
            LOGGER.trace(e.getMessage());
            return false;
        } catch (final Exception e) {
            LOGGER.trace(e.getMessage(), e);
            return false;
        }
    }
}
