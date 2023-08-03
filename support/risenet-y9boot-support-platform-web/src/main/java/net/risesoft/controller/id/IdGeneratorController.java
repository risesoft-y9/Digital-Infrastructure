package net.risesoft.controller.id;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.id.Y9IdGenerator;
import net.risesoft.log.annotation.RiseLog;

/**
 * id生成管理
 *
 * @author dingzhaojun
 * @date 2022/6/14
 */
@RestController
@RequestMapping(value = "/api/rest/id", produces = "application/json")
public class IdGeneratorController {

    private final Y9IdGenerator y9IdGenerator;

    public IdGeneratorController(@Qualifier("snowflakeIdGenerator") Y9IdGenerator y9IdGenerator) {
        this.y9IdGenerator = y9IdGenerator;
    }

    /**
     * 生成id
     *
     * @return
     */
    @RiseLog(operationName = "生成id")
    @RequestMapping(value = "/getNextId")
    public String getSnowflakeId() {
        return y9IdGenerator.getNextId();
    }
}
