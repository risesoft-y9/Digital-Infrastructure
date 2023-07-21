package net.risesoft.id;

import net.risesoft.id.impl.SnowflakeIdGenerator;
import net.risesoft.id.impl.TimeBasedUuidGenerator;

public interface Y9IdGenerator {
    String getNextId();


    /**
     * 默认使用雪花算法生成id
     *
     * @return {@link String}
     */
    static String genId() {
        return genId(IdType.SNOWFLAKE);
    }

    static String genId(IdType idType) {
        if (IdType.SNOWFLAKE.equals(idType)) {
            SnowflakeIdGenerator idGenerator = Y9SpringContext.getBean(SnowflakeIdGenerator.class);
            return idGenerator.getNextId();
        } else {
            TimeBasedUuidGenerator idGenerator = Y9SpringContext.getBean(TimeBasedUuidGenerator.class);
            return idGenerator.getNextId();
        }
    }
}