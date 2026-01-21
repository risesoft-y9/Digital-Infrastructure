package net.risesoft.api.id;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.platform.id.IdApi;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.pojo.Y9Result;

/**
 * 系统管理组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Primary
@RestController
@RequestMapping(value = "/services/rest/v1/id", produces = MediaType.APPLICATION_JSON_VALUE)
public class IdApiImpl implements IdApi {

    /**
     * 生成一个 id
     *
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是生成的id
     * @since 9.6.0
     */
    @Override
    public Y9Result<String> getNextId() {
        return Y9Result.success(Y9IdGenerator.genId());
    }

}
