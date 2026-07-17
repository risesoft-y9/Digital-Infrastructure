package net.risesoft.api.v0.org;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.v0.org.PositionApi;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.Person;
import net.risesoft.model.platform.org.Position;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.service.relation.Y9PersonsToPositionsService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * 岗位服务组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Primary
@Validated
@RestController(value = "v0PositionApiImpl")
@RequestMapping(value = "/services/rest/position", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Deprecated
public class PositionApiImpl implements PositionApi {

    private final CompositeOrgBaseService compositeOrgBaseService;
    private final Y9PersonService orgPersonService;
    private final Y9PositionService y9PositionService;
    private final Y9PersonsToPositionsService orgPositionsPersonsService;

    /**
     * 岗位增加人员
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param personId 人员id
     * @return boolean true 移除成功，false 移除失败
     * @since 9.6.0
     */
    @Override
    public boolean addPerson(@RequestParam String tenantId, @RequestParam String positionId,
        @RequestParam String personId) {
        if (StringUtils.isBlank(tenantId) || StringUtils.isBlank(positionId) || StringUtils.isBlank(personId)) {
            return false;
        }
        Y9LoginUserHolder.setTenantId(tenantId);

        orgPositionsPersonsService.addPersons(positionId, new String[] {personId});
        return true;
    }

    /**
     * 创建岗位
     *
     * @param tenantId 租户id
     * @param positionJson 岗位对象
     * @return Position 岗位对象
     * @since 9.6.0
     */
    @Override
    public Position createPosition(@RequestParam String tenantId, @RequestParam String positionJson) {
        if (StringUtils.isBlank(tenantId) || StringUtils.isBlank(positionJson)) {
            return null;
        }
        Y9LoginUserHolder.setTenantId(tenantId);

        Position y9Position = Y9JsonUtil.readValue(positionJson, Position.class);
        return y9PositionService.saveOrUpdate(y9Position);
    }

    /**
     * 根据岗位id删除岗位
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return boolean 是否删除成功
     * @since 9.6.0
     */
    @Override
    public boolean deletePosition(@RequestParam String tenantId, @RequestParam String positionId) {
        if (StringUtils.isBlank(positionId)) {
            return false;
        }
        Y9LoginUserHolder.setTenantId(tenantId);

        y9PositionService.deleteById(positionId);
        return true;
    }

    /**
     * 获取岗位父节点
     *
     * @param tenantId 租户id
     * @param positionId 岗位唯一标识
     * @return OrgUnit 组织节点对象（部门或组织机构）
     * @since 9.6.0
     */
    @Override
    public OrgUnit getParent(@RequestParam String tenantId, @RequestParam String positionId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return compositeOrgBaseService.findOrgUnitParent(positionId).orElse(null);
    }

    /**
     * 根据id获得岗位对象
     *
     * @param tenantId 租户id
     * @param positionId 岗位唯一标识
     * @return Position 岗位对象
     * @since 9.6.0
     */
    @Override
    public Position getPosition(@RequestParam String tenantId, @RequestParam String positionId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return y9PositionService.findById(positionId).orElse(null);
    }

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
    public boolean hasPosition(@RequestParam String tenantId, @RequestParam String positionName,
        @RequestParam String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return y9PositionService.hasPosition(positionName, personId);
    }

    /**
     * 根据父节点获取岗位列表
     *
     * @param tenantId 租户唯一标识
     * @param parentId 父节点ID
     * @return {@code List<Position>} 岗位对象集合
     * @since 9.6.0
     */
    @Override
    public List<Position> listByParentId(@RequestParam String tenantId, @RequestParam String parentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return y9PositionService.listByParentId(parentId, false);
    }

    /**
     * 根据用户ID,获取岗位列表
     *
     * @param tenantId 租户唯一标识
     * @param personId 人员ID
     * @return {@code List<Position>} 岗位对象集合
     * @since 9.6.0
     */
    @Override
    public List<Position> listByPersonId(@RequestParam String tenantId, @RequestParam String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return y9PositionService.listByPersonId(personId, Boolean.FALSE);
    }

    /**
     * 获取岗位的人员列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位唯一标识
     * @return {@code List<Person>}人员对象集合
     * @since 9.6.0
     */
    @Override
    public List<Person> listPersons(@RequestParam String tenantId, @RequestParam String positionId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return orgPersonService.listByPositionId(positionId, Boolean.FALSE);
    }

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
    public boolean removePerson(@RequestParam String tenantId, @RequestParam String positionId,
        @RequestParam String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        orgPositionsPersonsService.deletePersons(positionId, new String[] {personId});
        return true;
    }

    /**
     * 更新岗位
     *
     * @param tenantId 租户id
     * @param positionJson 岗位对象
     * @return Position 岗位对象
     * @since 9.6.0
     */
    @Override
    public Position updatePosition(@RequestParam String tenantId, @RequestParam String positionJson) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Position y9Position = Y9JsonUtil.readValue(positionJson, Position.class);
        return y9PositionService.saveOrUpdate(y9Position);
    }
}
