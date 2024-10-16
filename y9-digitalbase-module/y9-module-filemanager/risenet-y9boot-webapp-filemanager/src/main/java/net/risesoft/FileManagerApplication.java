package net.risesoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.configuration.app.y9filemanager.Y9FileManagerProperties;

@SpringBootApplication
@EnableConfigurationProperties({Y9Properties.class, Y9FileManagerProperties.class})
public class FileManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileManagerApplication.class, args);
    }
}