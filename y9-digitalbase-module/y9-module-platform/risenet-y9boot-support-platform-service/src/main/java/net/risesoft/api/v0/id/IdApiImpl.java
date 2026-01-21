package net.risesoft.api.v0.id;

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

    /**
     * 获取 snowflake id
     *
     * @return String id
     * @since 9.6.0
     */
    @Override
    public String getNextId() {
        return Y9IdGenerator.genId();
    }

}
