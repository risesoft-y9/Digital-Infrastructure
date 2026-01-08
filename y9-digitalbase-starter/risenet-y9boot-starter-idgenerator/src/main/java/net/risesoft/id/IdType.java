package net.risesoft.id;

import lombok.Getter;

/**
 * Id类型
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Getter
public enum IdType {
    /** 分布式自增 */
    SNOWFLAKE(1),
    /** UUID */
    UUID(2);

    private final Integer value;

    IdType(Integer value) {
        this.value = value;
    }

}
