package net.risesoft.api.platform.permission.cache;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.platform.permission.cache.PersonalApp;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.pojo.Y9Result;

/**
 * 个性化应用管理组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Validated
public interface PersonalAppApi {

    /**
     * 刷新人员/岗位应用信息
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
     * 根据人员ID和租户ID，返回个人应用列表
     *
     * @param tenantId 租户ID
     * @param orgUnitId 人员/岗位id
     * @return {@code Y9Result<List<PersonIconItem>>} 通用请求返回对象 - data 是个人应用列表
     * @since 9.6.2
     */
    @GetMapping("/listByOrgUnitId")
    Y9Result<List<PersonalApp>> listByOrgUnitId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId);

    /**
     * 获取人员/岗位分页获取应用列表
     *
     * @param tenantId 租户ID
     * @param orgUnitId 人员/岗位id
     * @param categoryId 分类 id，非空时筛选对应分类的，为空时不筛选
     * @param pageQuery 分页查询参数
     * @return {@code Y9Page<PersonIconItem>} 通用请求返回对象 - data 是应用应用列表
     * @since 9.6.2
     */
    @GetMapping("/pageByOrgUnitId")
    Y9Page<PersonalApp> pageByOrgUnitId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId,
        @RequestParam(name = "categoryId", required = false) String categoryId, @Validated Y9PageQuery pageQuery);

    /**
     * 应用排序
     *
     * @param tenantId 租户 ID
     * @param orgUnitId 人员/岗位 id
     * @param appIdList 应用id列表
     * @return {@code Y9Result<Object> }
     */
    @PostMapping("/sort")
    Y9Result<Object> sort(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("appIdList") List<String> appIdList);
}
