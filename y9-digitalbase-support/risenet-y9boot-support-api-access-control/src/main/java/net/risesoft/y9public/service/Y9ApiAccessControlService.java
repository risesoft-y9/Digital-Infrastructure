package net.risesoft.y9public.service;

import java.util.List;

import net.risesoft.enums.platform.ApiAccessControlType;
import net.risesoft.y9public.entity.Y9ApiAccessControl;

/**
 * api 访问控制 service
 *
 * @author shidaobang
 * @date 2024/11/26
 * @since 9.6.8
 */
public interface Y9ApiAccessControlService {

    List<Y9ApiAccessControl> listByTypeAndEnabled(ApiAccessControlType type);

    Y9ApiAccessControl getById(String id);

}
