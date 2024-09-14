package net.risesoft.api.platform.v0.org;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
@Deprecated
public interface OrgSyncApi {

    /**
     * 根据机构id，全量获取整个组织机构数据
     *
     * @param appName 应用名称
     * @param tenantId 租户id
     * @param organizationId 机构id
     * @return Y9Result&lt;MessageOrg&gt; 整个组织机构对象集合
     * @since 9.6.0
     */
    @GetMapping("/fullSync")
    Y9Result<MessageOrg> fullSync(@RequestParam("appName") @NotBlank String appName,
        @RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("organizationId") @NotBlank String organizationId);

    /**
     * 增量获取组织操作列表 系统记录了上一次同步的时间，从上一次同步时间往后获取数据
     *
     * @param appName 应用名称
     * @param tenantId 租户id
     * @return Y9Result&lt;List&lt;MessageOrg&gt;&gt; 事件列表
     * @since 9.6.0
     */
    @RequestMapping("/incrSync")
    Y9Result<List<MessageOrg>> incrSync(@RequestParam("appName") @NotBlank String appName,
        @RequestParam("tenantId") @NotBlank String tenantId);

}
