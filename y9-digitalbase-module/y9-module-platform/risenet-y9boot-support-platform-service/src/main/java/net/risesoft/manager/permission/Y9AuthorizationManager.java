package net.risesoft.manager.permission;

import net.risesoft.entity.permission.Y9Authorization;
import net.risesoft.y9.exception.Y9NotFoundException;

/**
 * 授权 Manager
 *
 * @author shidaobang
 * @date 2025/08/12
 */
public interface Y9AuthorizationManager {

    /**
     * 新增授权信息
     *
     * @param y9Authorization 授权信息
     * @return {@link Y9Authorization}
     * @throws Y9NotFoundException 授权信息对应的资源不存在的情况
     */
    Y9Authorization insert(Y9Authorization y9Authorization);

}
