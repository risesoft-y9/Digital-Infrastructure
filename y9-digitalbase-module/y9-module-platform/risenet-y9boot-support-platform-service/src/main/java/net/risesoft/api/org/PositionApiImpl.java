package net.risesoft.api.org;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.dto.platform.CreatePositionDTO;
import net.risesoft.model.platform.org.Person;
import net.risesoft.model.platform.org.Position;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.service.relation.Y9PersonsToPositionsService;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;

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

    private final Y9PersonService y9PersonService;
    private final Y9PositionService y9PositionService;
    private final Y9PersonsToPositionsService y9PersonsToPositionsService;

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
    public Y9Result<Object> addPerson(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        y9PersonsToPositionsService.addPersons(positionId, new String[] {personId});
        return Y9Result.success();
    }

    /**
     * 创建岗位
     *
     * @param tenantId 租户id
     * @param createPositionDTO 岗位对象
     * @return {@code Y9Result<Position>} 通用请求返回对象 - data 是保存的岗位对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<Position> create(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestBody @Validated CreatePositionDTO createPositionDTO) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Position position = PlatformModelConvertUtil.convert(createPositionDTO, Position.class);
        return Y9Result.success(y9PositionService.saveOrUpdate(position));
    }

    /**
     * 删除岗位
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> delete(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        y9PositionService.deleteById(positionId);
        return Y9Result.success();
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
    public Y9Result<Position> get(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9PositionService.findById(positionId).orElse(null));
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
    public Y9Result<Boolean> hasPosition(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionName") @NotBlank String positionName,
        @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9PositionService.hasPosition(positionName, personId));
    }

    /**
     * 根据id列表批量获取岗位对象
     *
     * @param tenantId 租户id
     * @param ids 岗位唯一标识列表
     * @return {@code Y9Result<List<Position>>} 通用请求返回对象 - data 是岗位对象列表
     * @since 9.6.10
     */
    @Override
    public Y9Result<List<Position>> listByIds(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("ids") @NotBlank List<String> ids) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9PositionService.listByIds(ids));
    }

    /**
     * 根据父节点获取岗位列表（不包含禁用）
     *
     * @param tenantId 租户唯一标识
     * @param parentId 父节点ID
     * @return {@code Y9Result<List<Position>>} 通用请求返回对象 - data 是岗位对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Position>> listByParentId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("parentId") @NotBlank String parentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9PositionService.listByParentId(parentId, Boolean.FALSE));
    }

    /**
     * 获取人员拥有的岗位列表（不包含禁用）
     *
     * @param tenantId 租户唯一标识
     * @param personId 人员ID
     * @return {@code Y9Result<List<Position>>} 通用请求返回对象 - data 是岗位对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Position>> listByPersonId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9PositionService.listByPersonId(personId, Boolean.FALSE));
    }

    /**
     * 获取岗位绑定的人员列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param positionId 岗位唯一标识
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Person>> listPersonsByPositionId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9PersonService.listByPositionId(positionId, Boolean.FALSE));
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
    public Y9Result<Object> removePerson(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        y9PersonsToPositionsService.deletePersons(positionId, new String[] {personId});
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
    public Y9Result<Position> updatePosition(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestBody @Validated CreatePositionDTO createPositionDTO) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Position position = PlatformModelConvertUtil.convert(createPositionDTO, Position.class);
        return Y9Result.success(y9PositionService.saveOrUpdate(position));
    }
}
