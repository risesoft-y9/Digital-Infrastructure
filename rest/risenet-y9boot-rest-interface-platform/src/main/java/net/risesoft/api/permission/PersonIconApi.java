package net.risesoft.api.permission;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.PersonIconItem;
import net.risesoft.pojo.Y9Page;
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
     * 刷新人员图标信息
     *
     * @param tenantId 租户ID
     * @param personId 人员id
     * @return Y9Result&lt;Boolean&gt; 操作结果
     * @since 9.6.2
     */
    @PostMapping("/buildPersonalAppIconForPerson")
    Y9Result<Boolean> buildPersonalAppIconForPerson(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId);

    /**
     * 刷新岗位图标信息
     *
     * @param tenantId 租户ID
     * @param positionId 岗位id
     * @return Y9Result&lt;Boolean&gt; 操作结果
     * @since 9.6.2
     */
    @PostMapping("/buildPersonalAppIconForPosition")
    Y9Result<Boolean> buildPersonalAppIconForPosition(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId);

    /**
     * 根据人员ID和租户ID，返回个人图标列表
     *
     * @param tenantId 租户ID
     * @param orgUnitId 人员/岗位id
     * @return List&lt;PersonIconItem&gt; 应用图标列表
     * @since 9.6.2
     */
    @GetMapping("/listByOrgUnitId")
    Y9Result<List<PersonIconItem>> listByOrgUnitId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId);

    /**
     * 根据人员/岗位id和图标类别，获取图标信息列表
     *
     * @param tenantId 租户ID
     * @param orgUnitId 人员/岗位id
     * @param iconType 图标类别 1:普通的 2:常用图标
     * @return List&lt;PersonIconItem&gt; 应用图标列表
     * @since 9.6.2
     */
    @GetMapping("/listByOrgUnitIdAndIconType")
    Y9Result<List<PersonIconItem>> listByOrgUnitIdAndIconType(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId, @RequestParam("iconType") Integer iconType);

    /**
     * 获取人员/岗位图标分页列表
     * 
     * @param tenantId 租户ID
     * @param orgUnitId 人员/岗位id
     * @param page 页数
     * @param rows 条数
     * @return Y9Page&lt;PersonIconItem&gt; 应用图标分页列表
     * @since 9.6.2
     */
    @GetMapping("/pageByOrgUnitId")
    Y9Page<PersonIconItem> pageByOrgUnitId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId, @RequestParam("page") int page,
        @RequestParam("rows") int rows);

    /**
     * 根据图标类别，获取人员/岗位图标分页列表
     *
     * @param tenantId 租户ID
     * @param orgUnitId 人员/岗位id
     * @param iconType 图标类别 1:普通的 2:常用图标
     * @param page 页数
     * @param rows 条数
     * @return
     */
    @GetMapping("/pageByOrgUnitIdAndIconType")
    Y9Page<PersonIconItem> pageByOrgUnitIdAndIconType(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId, @RequestParam("iconType") Integer iconType,
        @RequestParam("page") int page, @RequestParam("rows") int rows);

    /**
     * 根据分类类别id，获取人员/岗位图标分页列表
     *
     * @param tenantId 租户ID
     * @param orgUnitId 人员/岗位id
     * @param systemId 系统id(设置排序时为分类类别id)
     * @param page 页数
     * @param rows 条数
     * @return
     */
    @GetMapping("/pageByOrgUnitIdAndSystemId")
    Y9Page<PersonIconItem> pageByOrgUnitIdAndSystemId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId, @RequestParam("systemId") String systemId,
        @RequestParam("page") int page, @RequestParam("rows") int rows);

    /**
     * 设置常用应用
     *
     * @param tenantId 租户ID
     * @param orgUnitId 人员/岗位id
     * @param appIds 应用ids
     * @return Y9Result&lt;Boolean&gt; 操作结果
     * @since 9.6.2
     */
    @PostMapping("/setCommApps")
    Y9Result<Boolean> setCommApps(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId, @RequestParam("appIds") @NotEmpty String[] appIds);

}
