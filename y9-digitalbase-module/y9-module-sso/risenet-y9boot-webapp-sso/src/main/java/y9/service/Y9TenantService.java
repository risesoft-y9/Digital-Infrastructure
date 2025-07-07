package y9.service;

import java.util.List;

import y9.entity.Y9Tenant;

public interface Y9TenantService {

    List<Y9Tenant> listByEnabled(Boolean enabled);

    Y9Tenant getById(String id);
}
