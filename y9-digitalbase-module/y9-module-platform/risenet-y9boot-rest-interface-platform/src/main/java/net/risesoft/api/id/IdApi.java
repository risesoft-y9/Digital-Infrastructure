package net.risesoft.api.id;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

import net.risesoft.pojo.Y9Result;

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
public interface IdApi {

    /**
     * 生成一个 id
     *
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是生成的id
     * @since 9.6.0
     */
    @GetMapping("/getNextId")
    Y9Result<String> getNextId();
}
