package net.risesoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.configuration.app.y9log.Y9LogProperties;
import net.risesoft.y9.spring.boot.Y9Banner;

@SpringBootApplication
@EnableConfigurationProperties({Y9Properties.class, Y9LogProperties.class})
public class LogApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(LogApplication.class);
        springApplication.setBanner(new Y9Banner());
        springApplication.run(args);
    }
}
