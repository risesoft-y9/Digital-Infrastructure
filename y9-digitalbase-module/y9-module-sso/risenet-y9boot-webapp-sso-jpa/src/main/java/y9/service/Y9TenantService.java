package y9.service;

import java.util.List;

import y9.entity.Y9Tenant;

public interface Y9TenantService {

    /**
     * 获取启用的租户列表
     * 
     * @param enabled
     * @return
     */
    List<Y9Tenant> listByEnabled(Boolean enabled);

}
