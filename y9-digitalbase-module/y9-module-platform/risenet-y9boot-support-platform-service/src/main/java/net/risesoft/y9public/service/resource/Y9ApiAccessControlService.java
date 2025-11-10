package net.risesoft.y9public.service.resource;

import java.util.List;

import net.risesoft.enums.platform.ApiAccessControlType;
import net.risesoft.model.platform.ApiAccessControl;

/**
 * api 访问控制 service
 *
 * @author shidaobang
 * @date 2024/11/26
 * @since 9.6.8
 */
public interface Y9ApiAccessControlService {

    List<ApiAccessControl> listByType(ApiAccessControlType type);

    ApiAccessControl saveOrUpdate(ApiAccessControl apiAccessControl);

    void delete(String id);

    ApiAccessControl changeEnabled(String id);

    ApiAccessControl saveAppIdSecret(ApiAccessControl apiAccessControl);
}
