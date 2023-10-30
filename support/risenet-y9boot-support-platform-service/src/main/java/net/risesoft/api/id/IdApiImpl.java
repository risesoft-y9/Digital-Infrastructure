package net.risesoft.api.id;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    private final Y9IdGenerator y9IdGenerator;

    public IdApiImpl(@Qualifier("snowflakeIdGenerator") Y9IdGenerator y9IdGenerator) {
        this.y9IdGenerator = y9IdGenerator;
    }

    /**
     * 获取 snowflake id
     *
     * @return String id
     * @since 9.6.0
     */
    @Override
    public Y9Result<String> getNextId() {
        return Y9Result.success(y9IdGenerator.getNextId());
    }

}
