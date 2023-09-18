package net.risesoft.service.authorization;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;
import net.risesoft.entity.permission.Y9Authorization;
import net.risesoft.enums.AuthorizationPrincipalTypeEnum;
import net.risesoft.pojo.Y9PageQuery;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9AuthorizationService {

    /**
     * 根据id删除
     *
     * @param id 唯一标识
     */
    void delete(String id);

    /**
     * 根据多个权限id删除权限
     *
     * @param ids 资源id数组
     */
    void delete(String[] ids);

    /**
     * 根据主键，获取权限配置对象
     *
     * @param id 唯一标识
     * @return Authorization
     */
    Optional<Y9Authorization> findById(String id);

    /**
     * 根据授权主体id，获取所有与此授权主体相关的权限记录
     *
     * @param principalId 授权主体的唯一标识
     * @return {@link List}<{@link Y9Authorization}>
     */
    List<Y9Authorization> listByPrincipalId(String principalId);

    /**
     * 根据授权主体id和授权主体类型查找
     *
     * @param principalId 授权主体的唯一标识
     * @param principalTypeEnum 授权主体类型 {@link AuthorizationPrincipalTypeEnum}
     * @return {@link List}<{@link Y9Authorization}>
     */
    List<Y9Authorization> listByPrincipalIdAndPrincipalType(String principalId,
        AuthorizationPrincipalTypeEnum principalTypeEnum);

    /**
     * 根据principalIds和resourceId查询
     *
     * @param principalId 授权主体的唯一标识
     * @param resourceId 资源id
     * @return {@link List}<{@link Y9Authorization}>
     */
    List<Y9Authorization> listByPrincipalIdAndResourceId(String principalId, String resourceId);

    /**
     * 根据授权主体类型principalType和资源Id查询
     *
     * @param principalType 授权主体类型
     * @param resourceId 资源id
     * @return {@link List}<{@link Y9Authorization}>
     */
    List<Y9Authorization> listByPrincipalTypeAndResourceId(Integer principalType, String resourceId);

    /**
     * 根据授权主体类型principalType和资源id为条件查询，通过 principalType 排除某个授权主体类型
     *
     * @param principalType 授权主体类型
     * @param resourceId 资源id
     * @return {@link List}<{@link Y9Authorization}>
     */
    List<Y9Authorization> listByPrincipalTypeNotAndResourceId(Integer principalType, String resourceId);

    /**
     * 根据资源id、操作类型和人员id，获取该用户拥有的权限列表
     *
     * @param resourceId 资源id
     * @return {@link List}<{@link Y9Authorization}>
     */
    List<Y9Authorization> listByResourceId(String resourceId);

    /**
     * 根据资源id找到与其相关的授权（继承的）
     *
     * @param resourceId 资源id
     * @return {@link List}<{@link Y9Authorization}>
     */
    List<Y9Authorization> listByResourceIdRelated(String resourceId);

    /**
     * 根据资源id，授权主体id,和code模糊查询
     *
     * @param resourceIds 资源id数组
     * @param principalId 授权主体的唯一标识
     * @param authority 权限类型 {@link net.risesoft.enums.AuthorityEnum}
     * @return {@link List}<{@link Y9Authorization}>
     */
    List<Y9Authorization> listByResourceIds(List<String> resourceIds, String principalId, Integer authority);

    /**
     * 根据RoleIds查询
     *
     * @param principalIds 授权主体的唯一标识列表
     * @param resourceId 资源id
     * @param authority 权限类型 {@link net.risesoft.enums.AuthorityEnum}
     * @return {@link List}<{@link Y9Authorization}>
     */
    List<Y9Authorization> listByRoleIds(List<String> principalIds, String resourceId, Integer authority);

    /**
     * 根据principalIds和authority查询
     *
     * @param principalIds 授权主体的唯一标识列表
     * @param authority 权限类型 {@link net.risesoft.enums.AuthorityEnum}
     * @return {@link List}<{@link String}>
     */
    List<String> listResourceIdByPrincipalIdsAndAuthority(List<String> principalIds, Integer authority);

    /**
     * 根据资源id分页查询授权记录
     *
     * @param pageQuery 分页查询
     * @param resourceId 资源id
     * @param principalType
     * @return {@link Page}<{@link Y9Authorization}>
     */
    Page<Y9Authorization> page(Y9PageQuery pageQuery, String resourceId, Integer principalType);

    /**
     * 根据授权主体id，获取与此授权主体相关的权限分页记录
     *
     * @param principalId 授权主体的唯一标识
     * @param rows 每页显示的行数
     * @param page 当前第几页
     * @return Page<Authorization>
     */
    Page<Y9Authorization> pageByPrincipalId(String principalId, Integer rows, Integer page);

    void save(Integer authority, String principalId, Integer principalType, String[] resourceIds);

    void saveByOrg(Integer authority, String resourceId, String[] principleIds);

    void saveByRoles(Integer authority, String resourceId, String[] roleIds);

    /**
     * 保存或者修改权限配置
     *
     * @param y9Authorization 权限配置对象
     * @return Authorization
     */
    Y9Authorization saveOrUpdate(Y9Authorization y9Authorization);

    /**
     * 保存或者修改资源关联组织信息
     *
     * @param y9Authorization 权限配置对象
     * @return Authorization
     */
    Y9Authorization saveOrUpdateOrg(Y9Authorization y9Authorization);

    /**
     * 保存或者修改资源关联角色信息
     *
     * @param y9Authorization 权限配置对象
     * @return Authorization
     */
    Y9Authorization saveOrUpdateRole(Y9Authorization y9Authorization);

    /**
     * 同步orgUnitId相关的权限配置至身份（人员或者岗位）对资源的权限缓存表
     *
     * @param orgUnitId 组织节点id
     */
    void syncToIdentityResourceAndAuthority(String orgUnitId);

    /**
     * 同步人员相关的权限配置至人员对资源的权限缓存表
     *
     * @param person 人员对象
     */
    void syncToIdentityResourceAndAuthority(Y9Person person);

    /**
     * 同步岗位相关的权限配置至岗位对资源的权限缓存表
     *
     * @param position 岗位对象
     */
    void syncToIdentityResourceAndAuthority(Y9Position position);

    /**
     * 获取资源id相关的权限配置同步至权限缓存表
     *
     * @param resourceId 资源id
     */
    void syncToIdentityResourceAndAuthorityByResourceId(String resourceId);
}
