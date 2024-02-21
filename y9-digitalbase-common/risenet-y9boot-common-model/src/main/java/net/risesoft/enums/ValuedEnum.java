package net.risesoft.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 获取枚举实例中的 value 值 <br/>
 * 用于将接口参数转成 value 对应的枚举
 * 
 * @author shidaobang
 * @date 2023/11/14
 * @since 9.6.3
 */
public interface ValuedEnum<T> {

    @JsonValue
    T getValue();
}
