package net.risesoft.y9public.service.resource;

import java.util.List;

import net.risesoft.enums.platform.ApiAccessControlType;
import net.risesoft.y9public.entity.resource.Y9ApiAccessControl;

/**
 * api 访问控制 service
 *
 * @author shidaobang
 * @date 2024/11/26
 * @since 9.6.8
 */
public interface Y9ApiAccessControlService {

    List<Y9ApiAccessControl> listByType(ApiAccessControlType type);

    List<Y9ApiAccessControl> listByTypeAndEnabled(ApiAccessControlType type);

    Y9ApiAccessControl saveOrUpdate(Y9ApiAccessControl apiAccessControl);

    Y9ApiAccessControl getById(String id);

    boolean isMatch(String key, String value, Boolean enabled);

    void delete(String id);

    Y9ApiAccessControl changeEnabled(String id);

    Y9ApiAccessControl saveAppIdSecret(Y9ApiAccessControl apiAccessControl);
}
