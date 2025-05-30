package y9.autoconfiguration.id.generator;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.risesoft.id.Y9SpringContext;
import net.risesoft.id.impl.SnowflakeIdGenerator;
import net.risesoft.id.impl.TimeBasedUuidGenerator;
import net.risesoft.y9.configuration.feature.idgenerator.Y9IdGeneratorProperties;

@Configuration
@EnableConfigurationProperties(Y9IdGeneratorProperties.class)
public class IdGeneratorConfiguration {
    @Bean
    public Y9SpringContext y9SpringContext() {
        return new Y9SpringContext();
    }

    @Bean
    public TimeBasedUuidGenerator timeBasedUuidGenerator() {
        return new TimeBasedUuidGenerator();
    }

    @Bean
    public SnowflakeIdGenerator snowflakeIdGenerator() {
        return new SnowflakeIdGenerator();
    }
}