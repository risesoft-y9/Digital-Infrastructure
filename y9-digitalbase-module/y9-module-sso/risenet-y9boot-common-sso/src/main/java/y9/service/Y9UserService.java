package y9.service;

import java.util.List;

import y9.entity.Y9User;

public interface Y9UserService {
    Y9User save(Y9User y9User);

    Y9User findByPersonIdAndTenantId(String personId, String tenantId);

    List<Y9User> findByLoginNameAndOriginal(String loginName, Boolean original);

    List<Y9User> findByMobileAndOriginal(String mobile, Boolean original);

    List<Y9User> findByTenantShortNameAndLoginNameAndOriginal(String tenantShortName, String loginName,
        Boolean original);

    List<Y9User> findByTenantShortNameAndLoginNameAndParentId(String tenantShortName, String loginName,
        String parentId);

    List<Y9User> findByTenantShortNameAndMobileAndOriginal(String tenantShortName, String mobile, Boolean original);

    List<Y9User> findByTenantShortNameAndMobileAndParentId(String tenantShortName, String mobile, String parentId);

    List<Y9User> findByPersonIdAndOriginal(String personId, Boolean original);

}
