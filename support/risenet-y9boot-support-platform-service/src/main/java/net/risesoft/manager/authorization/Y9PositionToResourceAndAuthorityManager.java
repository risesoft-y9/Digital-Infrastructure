package net.risesoft.manager.authorization;

import net.risesoft.entity.Y9Position;
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
    void deleteByAuthorizationId(String authorizationId);

    void saveOrUpdate(Y9ResourceBase y9ResourceBase, Y9Position y9Position, Y9Authorization y9Authorization, Boolean inherit);
}
