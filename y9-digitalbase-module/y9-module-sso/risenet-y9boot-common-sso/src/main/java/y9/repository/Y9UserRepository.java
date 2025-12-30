package y9.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import y9.entity.Y9User;

@Transactional(readOnly = true, transactionManager = "rsPublicTransactionManager")
@Repository
public interface Y9UserRepository extends JpaRepository<Y9User, String> {

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
