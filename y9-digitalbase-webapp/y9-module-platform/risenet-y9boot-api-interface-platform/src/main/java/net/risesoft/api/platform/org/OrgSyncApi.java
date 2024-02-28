package net.risesoft.api.platform.org;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.MessageOrg;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.SyncOrgUnits;
import net.risesoft.pojo.Y9Page;
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
     * 根据机构id，全量获取整个组织机构所有组织节点数据
     *
     * @param appName 应用名称
     * @param tenantId 租户id
     * @param organizationId 机构id
     * @return {@code Y9Result<MessageOrg>} 通用请求返回对象 - data 是整个组织机构所有组织节点集合
     * @since 9.6.0
     */
    @GetMapping("/fullSync")
    Y9Result<MessageOrg<SyncOrgUnits>> fullSync(@RequestParam("appName") @NotBlank String appName,
        @RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("organizationId") @NotBlank String organizationId);

    /**
     * 增量获取组织操作列表 系统记录了上一次同步的时间，从上一次同步时间往后获取数据
     *
     * @param appName 应用名称
     * @param tenantId 租户id
     * @return {@code Y9Result<List<MessageOrg>>} 通用请求返回对象 - data 是事件列表
     * @since 9.6.0
     */
    @GetMapping("/incrSync")
    Y9Result<List<MessageOrg<OrgUnit>>> incrSync(@RequestParam("appName") @NotBlank String appName,
        @RequestParam("tenantId") @NotBlank String tenantId);
    
    /**
     * 返回增量接口处理结果，刷新同步时间
     * @param appName 应用名称
     * @param tenantId 租户id
     * @return
     */
    @GetMapping("/syncTime")
    Y9Result<String> syncTime(@RequestParam String appName, @RequestParam String tenantId);
    
    /**
     * 分页获取部门数据
     * @param appName 应用名称
     * @param tenantId 租户id
     * @param page 页数，初始值为1
     * @param rows 每页返回数
     * @return
     */
    @GetMapping("/fullSyncDept")
    public Y9Page<Department> fullSyncDept(@RequestParam String appName, @RequestParam String tenantId, @RequestParam int page, @RequestParam int rows);
    
    /**
     * 分页获取人员数据
     * @param appName 应用名称
     * @param tenantId 租户id
     * @param type 0-查全量，1-查询没被禁用的
     * @param page 页数，初始值为1
     * @param rows 每页返回数
     * @return
     */
    @GetMapping("/fullSyncUser")
    public Y9Page<Person> fullSyncUser(@RequestParam String appName, @RequestParam String tenantId, @RequestParam String type, 
    		@RequestParam int page, @RequestParam int rows);

}
