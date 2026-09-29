package net.risesoft.manager.permission.cache;

import java.util.List;

import net.risesoft.entity.org.Y9Position;
import net.risesoft.entity.permission.Y9Authorization;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;

/**
 * 岗位权限缓存 Manager
 *
 * @author shidaobang
 * @date 2023/06/13
 * @since 9.6.2
 */
public interface Y9PositionToResourceAndAuthorityManager {
    /**
     * 删除岗位指定授权列表之外的权限缓存
     *
     * @param positionId 岗位id
     * @param authorizationIdList 保留的授权id列表
     */
    void deleteByPositionIdAndAuthorizationIdNotIn(String positionId, List<String> authorizationIdList);

    /**
     * 根据岗位id和资源id删除权限缓存
     *
     * @param positionId 岗位id
     * @param resourceId 资源id
     */
    void deleteByPositionIdAndResourceId(String positionId, String resourceId);

    /**
     * 保存或更新岗位权限缓存
     *
     * @param y9ResourceBase 资源信息
     * @param y9Position 岗位信息
     * @param y9Authorization 授权信息
     * @param inherit 是否继承
     */
    void saveOrUpdate(Y9ResourceBase y9ResourceBase, Y9Position y9Position, Y9Authorization y9Authorization,
        Boolean inherit);
}
