package y9.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import y9.entity.Y9User;

@Transactional(readOnly = true, transactionManager = "rsPublicTransactionManager")
@Repository
public interface Y9UserRepository extends JpaRepository<Y9User, String> {

    Y9User findByPersonIdAndTenantId(String personId, String tenantId);

    List<Y9User> findByLoginNameAndOriginal(String loginName, Boolean original);

    List<Y9User> findByLoginNameContainingAndOriginalOrderByTenantShortName(String loginName, Boolean original);

    List<Y9User> findByMobileAndOriginal(String mobile, Boolean original);

    List<Y9User> findByOriginalAndLoginNameStartingWith(Boolean original, String loginName);

    List<Y9User> findByTenantIdAndLoginNameAndOriginal(String tenantId, String loginName, Boolean original);

    List<Y9User> findByTenantShortNameAndLoginName(String tenantShortName, String loginName);

    List<Y9User> findByTenantShortNameAndLoginNameAndOriginal(String tenantShortName, String loginName,
        Boolean original);

    List<Y9User> findByTenantShortNameAndLoginNameAndParentId(String tenantShortName, String loginName,
        String parentId);

    List<Y9User> findByTenantShortNameAndLoginNameAndPassword(String tenantShortName, String loginName,
        String password);

    List<Y9User> findByTenantShortNameAndLoginNameAndPasswordAndOriginal(String tenantShortName, String loginName,
        String password, Boolean original);

    List<Y9User> findByTenantShortNameAndMobile(String tenantShortName, String mobile);

    List<Y9User> findByTenantShortNameAndMobileAndOriginal(String tenantShortName, String mobile, Boolean original);

    List<Y9User> findByTenantShortNameAndMobileAndParentId(String tenantShortName, String mobile, String parentId);

    List<Y9User> findByTenantShortNameAndMobileAndPassword(String tenantShortName, String mobile, String password);

    List<Y9User> findByTenantShortNameNotInAndLoginNameAndOriginal(List<String> tenantlist, String loginName,
        Boolean original);

    List<Y9User> findByTenantNameAndLoginNameAndOriginal(String tenantName, String loginName, Boolean original);

    List<Y9User> findByPersonIdAndOriginal(String personId, Boolean original);

    @Query("select distinct u.tenantShortName as tenantShortName, u.tenantName as tenantName from Y9User u")
    List<Map<String, String>> listAllTenants();

}
