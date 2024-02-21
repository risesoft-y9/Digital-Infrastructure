package net.risesoft.api.platform.v0.id;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 唯一标识组件
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Validated
@Deprecated
public interface IdApi {

    /**
     * 获取snowflake id
     *
     * @return String id
     * @since 9.6.0
     */
    @GetMapping("/getNextId")
    String getNextId();
}
