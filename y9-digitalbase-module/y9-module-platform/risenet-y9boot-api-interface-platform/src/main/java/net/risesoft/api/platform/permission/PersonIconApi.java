package net.risesoft.api.platform.permission;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.platform.PersonIconItem;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.pojo.Y9Result;

/**
 * 人员/岗位图标管理组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Validated
public interface PersonIconApi {

    /**
     * 刷新人员/岗位图标信息
     *
     * @param tenantId 租户ID
     * @param orgUnitId 人员/岗位id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.2
     */
    @PostMapping("/buildPersonalAppIconByOrgUnitId")
    Y9Result<Object> buildPersonalAppIconByOrgUnitId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId);

    /**
     * 根据人员ID和租户ID，返回个人图标列表
     *
     * @param tenantId 租户ID
     * @param orgUnitId 人员/岗位id
     * @return {@code Y9Result<List<PersonIconItem>>} 通用请求返回对象 - data 是个人图标列表
     * @since 9.6.2
     */
    @GetMapping("/listByOrgUnitId")
    Y9Result<List<PersonIconItem>> listByOrgUnitId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId);

    /**
     * 获取人员/岗位分页获取图标列表
     *
     * @param tenantId 租户ID
     * @param orgUnitId 人员/岗位id
     * @param pageQuery 分页查询参数
     * @return {@code Y9Page<PersonIconItem>} 通用请求返回对象 - data 是应用图标列表
     * @since 9.6.2
     */
    @GetMapping("/pageByOrgUnitId")
    Y9Page<PersonIconItem> pageByOrgUnitId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId, @Validated Y9PageQuery pageQuery);

}
