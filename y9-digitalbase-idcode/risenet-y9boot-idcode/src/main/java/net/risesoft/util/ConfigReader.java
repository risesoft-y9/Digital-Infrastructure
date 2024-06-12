package net.risesoft.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static final Properties properties;

    public static String API_CODE;

    public static String API_KEY;

    public static String IDCODE_URL;

    public static String MAIN_CODE;

    public static String ANALYZE_URL;

    public static String GOTO_URL;

    public static String SAMPLE_URL;

    static {
        try (InputStream inputStream = ConfigReader.class.getResourceAsStream("/application.yml")) {
            if (inputStream != null) {
                properties = new Properties();
                properties.load(inputStream);

                API_CODE= properties.getProperty("api_code");
                API_KEY= properties.getProperty("api_key");
                IDCODE_URL= properties.getProperty("idCode_url");
                MAIN_CODE= properties.getProperty("main_code");
                ANALYZE_URL= properties.getProperty("analyze_url");
                GOTO_URL= properties.getProperty("goto_url");
                SAMPLE_URL= properties.getProperty("sample_url");
            } else {
                throw new FileNotFoundException("配置文件未找到");
            }
        } catch (IOException e) {
            throw new RuntimeException("无法加载配置文件", e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
