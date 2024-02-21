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

import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Position;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.service.relation.Y9PersonsToPositionsService;
import net.risesoft.util.ModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9ModelConvertUtil;

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

        if (y9PositionService.existsById(positionId) && orgPersonService.existsById(personId)) {
            orgPositionsPersonsService.addPersons(positionId, new String[] {personId});
            return true;
        }
        return false;
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

        Y9Position y9Position = Y9JsonUtil.readValue(positionJson, Y9Position.class);
        y9Position = y9PositionService.createPosition(y9Position);
        return Y9ModelConvertUtil.convert(y9Position, Position.class);
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

        Y9OrgBase parent = compositeOrgBaseService.findOrgUnitParent(positionId).orElse(null);
        return ModelConvertUtil.orgBaseToOrgUnit(parent);
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

        Y9Position y9Position = y9PositionService.findById(positionId).orElse(null);
        return Y9ModelConvertUtil.convert(y9Position, Position.class);
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
     * @return List<Position> 岗位对象集合
     * @since 9.6.0
     */
    @Override
    public List<Position> listByParentId(@RequestParam String tenantId, @RequestParam String parentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Position> y9PositionList = y9PositionService.listByParentId(parentId);
        return Y9ModelConvertUtil.convert(y9PositionList, Position.class);
    }

    /**
     * 根据用户ID,获取岗位列表
     *
     * @param tenantId 租户唯一标识
     * @param personId 人员ID
     * @return List<Position> 岗位对象集合
     * @since 9.6.0
     */
    @Override
    public List<Position> listByPersonId(@RequestParam String tenantId, @RequestParam String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Position> y9PositionList = y9PositionService.listByPersonId(personId);
        return Y9ModelConvertUtil.convert(y9PositionList, Position.class);
    }

    /**
     * 获取岗位的人员列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位唯一标识
     * @return List<Person> 人员对象集合
     * @since 9.6.0
     */
    @Override
    public List<Person> listPersons(@RequestParam String tenantId, @RequestParam String positionId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Person> y9PersonList = orgPersonService.listByPositionId(positionId);
        return Y9ModelConvertUtil.convert(y9PersonList, Person.class);
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

        if (y9PositionService.existsById(positionId) && orgPersonService.existsById(personId)) {
            orgPositionsPersonsService.deletePersons(positionId, new String[] {personId});
            return true;
        }

        return false;
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

        Y9Position y9Position = Y9JsonUtil.readValue(positionJson, Y9Position.class);
        y9Position = y9PositionService.saveOrUpdate(y9Position);
        return Y9ModelConvertUtil.convert(y9Position, Position.class);
    }
}
