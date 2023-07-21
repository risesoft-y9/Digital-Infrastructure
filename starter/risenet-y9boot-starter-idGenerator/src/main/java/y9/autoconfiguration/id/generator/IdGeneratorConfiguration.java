package y9.autoconfiguration.id.generator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import net.risesoft.id.Y9IpUtil;
import net.risesoft.id.Y9SpringContext;
import net.risesoft.id.impl.SnowflakeIdGenerator;
import net.risesoft.id.impl.TimeBasedUuidGenerator;

@Configuration
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
    public SnowflakeIdGenerator snowflakeIdGenerator(RedisTemplate<Object, Object> redisTemplate) {
        long machineId = 0;
        String ip = Y9IpUtil.getIp();
        if (redisTemplate.hasKey("snowflake:" + ip)) {
            machineId = (Long)redisTemplate.opsForValue().get("snowflake:" + ip);
        } else {
            int count = redisTemplate.keys("snowflake:*").size();
            machineId = count + 1;
            redisTemplate.opsForValue().set("snowflake:" + ip, machineId);
        }

        return new SnowflakeIdGenerator(machineId);
    }
}