package y9.client.platform.org;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.org.PositionApi;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.Person;
import net.risesoft.model.Position;

/**
 * 岗位服务组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "PositionApiClient", name = "y9platform", url = "${y9.common.orgBaseUrl}", path = "/services/rest/position")
public interface PositionApiClient extends PositionApi {

    /**
     * 向岗位增加人员
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param personId 人员id
     * @return boolean true 移除成功，false 移除失败
     * @since 9.6.0
     */
    @Override
    @PostMapping("/addPerson")
    boolean addPerson(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("personId") String personId);

    /**
     * 创建岗位
     *
     * @param tenantId 租户id
     * @param positionJson 岗位对象
     * @return Position 岗位对象
     * @since 9.6.0
     */
    @Override
    @PostMapping("/createPosition")
    Position createPosition(@RequestParam("tenantId") String tenantId, @RequestParam("positionJson") String positionJson);

    /**
     * 根据岗位id删除岗位
     *
     * @param tenantId 租户id
     * @param positoinId 岗位id
     * @return boolean s是否删除成功
     * @since 9.6.0
     */
    @Override
    @PostMapping("/deletePosition")
    boolean deletePosition(@RequestParam("tenantId") String tenantId, @RequestParam("positoinId") String positoinId);

    /**
     * 获取岗位父节点
     *
     * @param tenantId 租户id
     * @param positionId 岗位唯一标识
     * @return OrgUnit 机构对象
     * @since 9.6.0
     */
    @Override
    @GetMapping("/getParent")
    OrgUnit getParent(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId);

    /**
     * 根据id获得岗位对象
     *
     * @param tenantId 租户id
     * @param positionId 岗位唯一标识
     * @return Position 岗位对象
     * @since 9.6.0
     */
    @Override
    @GetMapping("/getPosition")
    Position getPosition(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId);

    /**
     * 根据人员id和岗位id判断该人员是否拥有此岗位
     *
     * @param tenantId 租户id
     * @param positionName 岗位名称
     * @param personId 岗位唯一标识
     * @return boolean 是否拥有该岗位
     * @since 9.6.0
     */
    @Override
    @GetMapping("/hasPosition")
    boolean hasPosition(@RequestParam("tenantId") String tenantId, @RequestParam("positionName") String positionName, @RequestParam("personId") String personId);

    /**
     * 根据父节点获取岗位列表
     *
     * @param tenantId 租户唯一标识
     * @param parentId 父节点ID
     * @return List&lt;Position&gt; 岗位对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listByParentId")
    List<Position> listByParentId(@RequestParam("tenantId") String tenantId, @RequestParam("parentId") String parentId);

    /**
     * 根据用户ID,获取岗位列表
     *
     * @param tenantId 租户唯一标识
     * @param personId 人员ID
     * @return List&lt;Position&gt; 岗位对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listByPersonId")
    List<Position> listByPersonId(@RequestParam("tenantId") String tenantId, @RequestParam("personId") String personId);

    /**
     * 获取所在岗位的人员列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位唯一标识
     * @return List&lt;Person&gt; 人员对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listPersons")
    List<Person> listPersons(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId);

    /**
     * 从岗位移除人员
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param personId 人员id
     * @return boolean true 移除成功，false 移除失败
     * @since 9.6.0
     */
    @Override
    @PostMapping("/removePerson")
    boolean removePerson(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("personId") String personId);

    /**
     * 更新岗位
     *
     * @param tenantId 租户id
     * @param positionJson 岗位对象
     * @return Position 岗位对象
     * @since 9.6.0
     */
    @Override
    @PostMapping("/updatePosition")
    Position updatePosition(@RequestParam("tenantId") String tenantId, @RequestParam("positionJson") String positionJson);

}
