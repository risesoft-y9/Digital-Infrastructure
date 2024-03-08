package net.risesoft.service.authorization;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import net.risesoft.entity.permission.Y9Authorization;
import net.risesoft.enums.platform.AuthorityEnum;
import net.risesoft.enums.platform.AuthorizationPrincipalTypeEnum;
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
    List<Y9Authorization> listByPrincipalTypeAndResourceId(AuthorizationPrincipalTypeEnum principalType,
        String resourceId);

    /**
     * 根据授权主体类型principalType和资源id为条件查询，通过 principalType 排除某个授权主体类型
     *
     * @param principalType 授权主体类型
     * @param resourceId 资源id
     * @return {@link List}<{@link Y9Authorization}>
     */
    List<Y9Authorization> listByPrincipalTypeNotAndResourceId(AuthorizationPrincipalTypeEnum principalType,
        String resourceId);

    /**
     * 根据资源id、操作类型和人员id，获取该用户拥有的权限列表
     *
     * @param resourceId 资源id
     * @return {@link List}<{@link Y9Authorization}>
     */
    List<Y9Authorization> listByResourceId(String resourceId);

    /**
     * 根据RoleIds查询
     *
     * @param principalIds 授权主体的唯一标识列表
     * @param resourceId 资源id
     * @param authority 权限类型 {@link AuthorityEnum}
     * @return {@link List}<{@link Y9Authorization}>
     */
    List<Y9Authorization> listByRoleIds(List<String> principalIds, String resourceId, AuthorityEnum authority);

    /**
     * 根据资源id分页查询授权记录
     *
     * @param pageQuery 分页查询
     * @param resourceId 资源id
     * @param principalType
     * @return {@link Page}<{@link Y9Authorization}>
     */
    Page<Y9Authorization> page(Y9PageQuery pageQuery, String resourceId, AuthorizationPrincipalTypeEnum principalType);

    /**
     * 根据授权主体id，获取与此授权主体相关的权限分页记录
     *
     * @param principalId 授权主体的唯一标识
     * @param rows 每页显示的行数
     * @param page 当前第几页
     * @return Page<Authorization>
     */
    Page<Y9Authorization> pageByPrincipalId(String principalId, Integer rows, Integer page);

    void save(AuthorityEnum authority, String principalId, AuthorizationPrincipalTypeEnum principalType,
        String[] resourceIds);

    void saveByOrg(AuthorityEnum authority, String resourceId, String[] principleIds);

    void saveByRoles(AuthorityEnum authority, String resourceId, String[] roleIds);

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

}
