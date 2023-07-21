package y9.client.platform.permission;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.permission.PersonIconApi;
import net.risesoft.model.PersonIconItem;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * 人员图标管理组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "PersonIconApiClient", name = "y9platform", url = "${y9.common.y9DigitalBaseUrl}", path = "/services/rest/personIcon")
public interface PersonIconApiClient extends PersonIconApi {

    /**
     * 刷新人员图标信息
     *
     * @param tenantId 租户ID
     * @param personId 人员id
     * @return Y9Result&lt;Boolean&gt; 操作结果
     * @since 9.6.2
     */
    @Override
    @PostMapping("/buildPersonalAppIconForPerson")
    Y9Result<Boolean> buildPersonalAppIconForPerson(@RequestParam("tenantId") String tenantId, @RequestParam("personId") String personId);

    /**
     * 刷新岗位图标信息
     *
     * @param tenantId 租户ID
     * @param positionId 岗位id
     * @return Y9Result&lt;Boolean&gt; 操作结果
     * @since 9.6.2
     */
    @Override
    @PostMapping("/buildPersonalAppIconForPosition")
    Y9Result<Boolean> buildPersonalAppIconForPosition(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId);

    /**
     * 根据人员ID和租户ID，返回个人图标列表
     *
     * @param tenantId 租户ID
     * @param orgUnitId 人员/岗位id
     * @return List&lt;PersonIconItem&gt; 应用图标列表
     * @since 9.6.2
     */
    @Override
    @GetMapping("/listByOrgUnitId")
    List<PersonIconItem> listByOrgUnitId(@RequestParam("tenantId") String tenantId, @RequestParam("orgUnitId") String orgUnitId);

    /**
     * 根据人员/岗位id和图标类别，获取图标信息列表
     *
     * @param tenantId 租户ID
     * @param orgUnitId 人员/岗位id
     * @param iconType 图标类别 1:普通的 2:常用图标,3:个人排序后的图标
     * @return List&lt;PersonIconItem&gt; 应用图标列表
     * @since 9.6.2
     */
    @Override
    @GetMapping("/listByOrgUnitIdAndIconType")
    List<PersonIconItem> listByOrgUnitIdAndIconType(@RequestParam("tenantId") String tenantId, @RequestParam("orgUnitId") String orgUnitId, @RequestParam("iconType") Integer iconType);

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
    @Override
    @GetMapping("/pageByOrgUnitId")
    Y9Page<PersonIconItem> pageByOrgUnitId(@RequestParam("tenantId") String tenantId, @RequestParam("orgUnitId") String orgUnitId, @RequestParam("page") int page, @RequestParam("rows") int rows);

    /**
     * 设置常用应用
     *
     * @param tenantId 租户ID
     * @param orgUnitId 人员/岗位id
     * @param appIds 应用ids
     * @return Y9Result&lt;Boolean&gt; 操作结果
     * @since 9.6.2
     */
    @Override
    @PostMapping("/setCommApps")
    Y9Result<Boolean> setCommApps(@RequestParam("tenantId") String tenantId, @RequestParam("orgUnitId") String orgUnitId, @RequestParam("appIds") String[] appIds);
}
