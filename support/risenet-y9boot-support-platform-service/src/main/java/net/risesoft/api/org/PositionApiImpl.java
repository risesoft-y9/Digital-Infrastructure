package net.risesoft.api.org;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.org.dto.CreatePositionDTO;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.Person;
import net.risesoft.model.Position;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.service.relation.Y9PersonsToPositionsService;
import net.risesoft.util.ModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;
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
@RestController
@RequestMapping(value = "/services/rest/v1/position", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PositionApiImpl implements PositionApi {

    private final CompositeOrgBaseService compositeOrgBaseService;
    private final Y9PersonService orgPersonService;
    private final Y9PositionService y9PositionService;
    private final Y9PersonsToPositionsService orgPositionsPersonsService;

    /**
     * 向岗位增加人员
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param personId 人员id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> addPerson(@RequestParam String tenantId, @RequestParam String positionId,
        @RequestParam String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        orgPositionsPersonsService.addPersons(positionId, new String[] {personId});
        return Y9Result.success();
    }

    /**
     * 创建岗位
     *
     * @param tenantId 租户id
     * @param position 岗位对象
     * @return {@code Y9Result<Position>} 通用请求返回对象 - data 是保存的岗位对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<Position> createPosition(@RequestParam String tenantId,
        @RequestBody @Validated CreatePositionDTO position) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9Position y9Position = Y9ModelConvertUtil.convert(position, Y9Position.class);
        y9Position = y9PositionService.saveOrUpdate(y9Position);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9Position, Position.class));
    }

    /**
     * 根据岗位id删除岗位
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> deletePosition(@RequestParam String tenantId, @RequestParam String positionId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        y9PositionService.deleteById(positionId);
        return Y9Result.success();
    }

    /**
     * 获取岗位父节点
     *
     * @param tenantId 租户id
     * @param positionId 岗位唯一标识
     * @return {@code Y9Result<OrgUnit>} 通用请求返回对象 - data 是组织节点对象（部门或组织机构）
     * @since 9.6.0
     */
    @Override
    public Y9Result<OrgUnit> getParent(@RequestParam String tenantId, @RequestParam String positionId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9OrgBase parent = compositeOrgBaseService.findOrgUnitParent(positionId).orElse(null);
        return Y9Result.success(ModelConvertUtil.orgBaseToOrgUnit(parent));
    }

    /**
     * 根据id获得岗位对象
     *
     * @param tenantId 租户id
     * @param positionId 岗位唯一标识
     * @return {@code Y9Result<Position>} 通用请求返回对象 - data 是岗位对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<Position> getPosition(@RequestParam String tenantId, @RequestParam String positionId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9Position y9Position = y9PositionService.findById(positionId).orElse(null);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9Position, Position.class));
    }

    /**
     * 根据人员id和岗位id判断该人员是否拥有此岗位
     *
     * @param tenantId 租户id
     * @param positionName 岗位名称
     * @param personId 岗位唯一标识
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 可判断否拥有该岗位
     * @since 9.6.0
     */
    @Override
    public Y9Result<Boolean> hasPosition(@RequestParam String tenantId, @RequestParam String positionName,
        @RequestParam String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9PositionService.hasPosition(positionName, personId));
    }

    /**
     * 根据父节点获取岗位列表
     *
     * @param tenantId 租户唯一标识
     * @param parentId 父节点ID
     * @return {@code Y9Result<List<Position>>} 通用请求返回对象 - data 是岗位对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Position>> listByParentId(@RequestParam String tenantId, @RequestParam String parentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Position> y9PositionList = y9PositionService.listByParentId(parentId);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9PositionList, Position.class));
    }

    /**
     * 根据用户ID,获取岗位列表
     *
     * @param tenantId 租户唯一标识
     * @param personId 人员ID
     * @return {@code Y9Result<List<Position>>} 通用请求返回对象 - data 是岗位对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Position>> listByPersonId(@RequestParam String tenantId, @RequestParam String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Position> y9PositionList = y9PositionService.listByPersonId(personId);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9PositionList, Position.class));
    }

    /**
     * 获取所在岗位的人员列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位唯一标识
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Person>> listPersons(@RequestParam String tenantId, @RequestParam String positionId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Person> y9PersonList = orgPersonService.listByPositionId(positionId);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9PersonList, Person.class));
    }

    /**
     * 从岗位移除人员
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param personId 人员id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> removePerson(@RequestParam String tenantId, @RequestParam String positionId,
        @RequestParam String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        orgPositionsPersonsService.deletePersons(positionId, new String[] {personId});
        return Y9Result.success();
    }

    /**
     * 更新岗位
     *
     * @param tenantId 租户id
     * @param createPositionDTO 岗位对象
     * @return {@code Y9Result<Position>} 通用请求返回对象 - data 是岗位对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<Position> updatePosition(@RequestParam String tenantId,
        @RequestBody @Validated CreatePositionDTO createPositionDTO) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9Position y9Position = Y9ModelConvertUtil.convert(createPositionDTO, Y9Position.class);
        y9Position = y9PositionService.saveOrUpdate(y9Position);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9Position, Position.class));
    }
}
