package net.risesoft.manager.authorization;

import net.risesoft.entity.Y9Person;
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
    void deleteByAuthorizationId(String authorizationId);

    void saveOrUpdate(Y9ResourceBase y9ResourceBase, Y9Person person, Y9Authorization y9Authorization, Boolean inherit);
}
