package net.risesoft.id;

import net.risesoft.id.impl.SnowflakeIdGenerator;
import net.risesoft.id.impl.TimeBasedUuidGenerator;

public class Y9IdGenerator {

    private static IdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator();
    private static IdGenerator timeBasedUuidGenerator = new TimeBasedUuidGenerator();
    
    protected void setSnowflakeIdGenerator(IdGenerator snowflakeIdGenerator) {
        Y9IdGenerator.snowflakeIdGenerator = snowflakeIdGenerator;
    }
    
    /**
     * 默认使用雪花算法生成id
     *
     * @return {@link String}
     */
    public static String genId() {
        return genId(IdType.SNOWFLAKE);
    }

    public static String genId(IdType idType) {
        if (IdType.SNOWFLAKE.equals(idType)) {
            return snowflakeIdGenerator.getNextId();
        } else {
            return timeBasedUuidGenerator.getNextId();
        }
    }
}