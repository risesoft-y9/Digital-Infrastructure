package net.risesoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import net.risesoft.y9.configuration.Y9Properties;

@SpringBootApplication
@EnableConfigurationProperties(Y9Properties.class)
public class RisenetY9demoFileApplication {

    public static void main(String[] args) {
        SpringApplication.run(RisenetY9demoFileApplication.class, args);
    }

}
