package net.risesoft.api.permission;

import java.util.List;

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
public interface PersonIconApi {

    /**
     * 刷新人员图标信息
     *
     * @param tenantId 租户ID
     * @param personId 人员id
     * @return Y9Result&lt;Boolean&gt; 操作结果
     * @since 9.6.2
     *
     */
    Y9Result<Boolean> buildPersonalAppIconForPerson(String tenantId, String personId);

    /**
     * 刷新岗位图标信息
     *
     * @param tenantId 租户ID
     * @param positionId 岗位id
     * @return Y9Result&lt;Boolean&gt; 操作结果
     * @since 9.6.2
     */
    Y9Result<Boolean> buildPersonalAppIconForPosition(String tenantId, String positionId);

    /**
     * 根据人员ID和租户ID，返回个人图标列表
     *
     * @param tenantId 租户ID
     * @param orgUnitId 人员/岗位id
     * @return List&lt;PersonIconItem&gt; 应用图标列表
     * @since 9.6.2
     */
    List<PersonIconItem> listByOrgUnitId(String tenantId, String orgUnitId);

    /**
     * 根据人员/岗位id和图标类别，获取图标信息列表
     *
     * @param tenantId 租户ID
     * @param orgUnitId 人员/岗位id
     * @param iconType 图标类别 1:普通的 2:常用图标,3:个人排序后的图标
     * @return List&lt;PersonIconItem&gt; 应用图标列表
     * @since 9.6.2
     */
    List<PersonIconItem> listByOrgUnitIdAndIconType(String tenantId, String orgUnitId, Integer iconType);

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
    Y9Page<PersonIconItem> pageByOrgUnitId(String tenantId, String orgUnitId, int page, int rows);

    /**
     * 设置常用应用
     *
     * @param tenantId 租户ID
     * @param orgUnitId 人员/岗位id
     * @param appIds 应用ids
     * @return Y9Result&lt;Boolean&gt; 操作结果
     * @since 9.6.2
     */
    Y9Result<Boolean> setCommApps(String tenantId, String orgUnitId, String[] appIds);

}
