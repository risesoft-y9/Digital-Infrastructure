package net.risesoft.manager.permission.cache;

import java.util.List;

import net.risesoft.entity.org.Y9Person;
import net.risesoft.entity.permission.Y9Authorization;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;

/**
 * 人员权限缓存 Manager
 *
 * @author shidaobang
 * @date 2023/06/13
 * @since 9.6.2
 */
public interface Y9PersonToResourceAndAuthorityManager {
    /**
     * 删除人员指定授权列表之外的权限缓存
     *
     * @param personId 人员id
     * @param authorizationIdList 保留的授权id列表
     */
    void deleteByPersonIdAndAuthorizationIdNotIn(String personId, List<String> authorizationIdList);

    /**
     * 根据人员id和资源id删除权限缓存
     *
     * @param personId 人员id
     * @param resourceId 资源id
     */
    void deleteByPersonIdAndResourceId(String personId, String resourceId);

    /**
     * 保存或更新人员权限缓存
     *
     * @param y9ResourceBase 资源信息
     * @param person 人员信息
     * @param y9Authorization 授权信息
     * @param inherit 是否继承
     */
    void saveOrUpdate(Y9ResourceBase y9ResourceBase, Y9Person person, Y9Authorization y9Authorization, Boolean inherit);
}
