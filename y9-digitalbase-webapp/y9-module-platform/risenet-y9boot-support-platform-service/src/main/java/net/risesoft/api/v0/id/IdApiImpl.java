package net.risesoft.api.v0.id;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.platform.v0.id.IdApi;
import net.risesoft.id.Y9IdGenerator;

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
@RestController(value = "v0IdApiImpl")
@RequestMapping(value = "/services/rest/id", produces = MediaType.APPLICATION_JSON_VALUE)
@Deprecated
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
    public String getNextId() {
        return y9IdGenerator.getNextId();
    }

}
