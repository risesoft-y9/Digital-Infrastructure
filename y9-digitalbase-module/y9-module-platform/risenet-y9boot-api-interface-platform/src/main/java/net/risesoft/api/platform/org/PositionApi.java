package net.risesoft.api.platform.org;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.platform.org.dto.CreatePositionDTO;
import net.risesoft.model.platform.org.Person;
import net.risesoft.model.platform.org.Position;
import net.risesoft.pojo.Y9Result;

/**
 * 岗位服务组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Validated
public interface PositionApi {

    /**
     * 向岗位增加人员
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param personId 人员id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @PostMapping("/addPerson")
    Y9Result<Object> addPerson(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("personId") @NotBlank String personId);

    /**
     * 创建岗位
     *
     * @param tenantId 租户id
     * @param position 岗位对象
     * @return {@code Y9Result<Position>} 通用请求返回对象 - data 是保存的岗位对象
     * @since 9.6.0
     */
    @PostMapping("/create")
    Y9Result<Position> create(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestBody @Validated CreatePositionDTO position);

    /**
     * 删除岗位
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @PostMapping("/delete")
    Y9Result<Object> delete(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId);

    /**
     * 根据id获得岗位对象
     *
     * @param tenantId 租户id
     * @param positionId 岗位唯一标识
     * @return {@code Y9Result<Position>} 通用请求返回对象 - data 是岗位对象
     * @since 9.6.0
     */
    @GetMapping("/get")
    Y9Result<Position> get(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId);

    /**
     * 根据人员id和岗位id判断该人员是否拥有此岗位
     *
     * @param tenantId 租户id
     * @param positionName 岗位名称
     * @param personId 岗位唯一标识
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 可判断否拥有该岗位
     * @since 9.6.0
     */
    @GetMapping("/hasPosition")
    Y9Result<Boolean> hasPosition(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionName") @NotBlank String positionName,
        @RequestParam("personId") @NotBlank String personId);

    /**
     * 根据父节点获取岗位列表（不包含禁用）
     *
     * @param tenantId 租户唯一标识
     * @param parentId 父节点ID
     * @return {@code Y9Result<List<Position>>} 通用请求返回对象 - data 是岗位对象集合
     * @since 9.6.0
     */
    @GetMapping("/listByParentId")
    Y9Result<List<Position>> listByParentId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("parentId") @NotBlank String parentId);

    /**
     * 获取人员拥有的岗位列表（不包含禁用）
     *
     * @param tenantId 租户唯一标识
     * @param personId 人员ID
     * @return {@code Y9Result<List<Position>>} 通用请求返回对象 - data 是岗位对象集合
     * @since 9.6.0
     */
    @GetMapping("/listByPersonId")
    Y9Result<List<Position>> listByPersonId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId);

    /**
     * 获取岗位绑定的人员列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param positionId 岗位唯一标识
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @GetMapping("/listPersonsByPositionId")
    Y9Result<List<Person>> listPersonsByPositionId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId);

    /**
     * 从岗位移除人员
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param personId 人员id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @PostMapping("/removePerson")
    Y9Result<Object> removePerson(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("personId") @NotBlank String personId);

    /**
     * 更新岗位
     *
     * @param tenantId 租户id
     * @param createPositionDTO 岗位对象
     * @return {@code Y9Result<Position>} 通用请求返回对象 - data 是岗位对象
     * @since 9.6.0
     */
    @PostMapping("/updatePosition")
    Y9Result<Position> updatePosition(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestBody @Validated CreatePositionDTO createPositionDTO);
}
