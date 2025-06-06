package net.risesoft.api.platform.org;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.platform.MessageOrg;
import net.risesoft.pojo.Y9Result;

/**
 * 组织同步组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Validated
public interface OrgSyncApi {

    /**
     * 从上一次同步成功的时间后开始获取组织节点实体事件列表
     *
     * @param since 上次成功同步时间，日期格式为 yyyy-MM-dd HH:mm:ss
     * @param tenantId 租户id
     * @return {@code Y9Result<List<MessageOrg>>} 通用请求返回对象 - data 是事件列表
     * @since 9.6.0
     */
    @GetMapping("/incrSync")
    Y9Result<List<MessageOrg<Serializable>>> incrSync(@RequestParam("since") Date since,
        @RequestParam("tenantId") @NotBlank String tenantId);

}
