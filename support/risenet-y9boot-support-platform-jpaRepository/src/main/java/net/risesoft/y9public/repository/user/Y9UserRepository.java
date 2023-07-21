package net.risesoft.y9public.repository.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.y9public.entity.user.Y9User;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
public interface Y9UserRepository extends JpaRepository<Y9User, String>, JpaSpecificationExecutor<Y9User> {

    List<Y9User> findByGuidPathContaining(String idstr);

    List<Y9User> findByLoginName(String loginName);

    Y9User findByLoginNameAndMobileAndTenantId(String loginName, String mobile, String tenantId);

    Y9User findByLoginNameAndTenantId(String loginName, String tenantId);

    Y9User findByLoginNameAndTenantIdAndOriginalTrue(String fakeLoginName, String tenantId);

    Y9User findByPersonId(String personId);

    Y9User findByPersonIdAndTenantId(String personId, String tenantId);

    Y9User findByPersonIdIsNotAndLoginNameAndTenantId(String personId, String loginName, String tenantId);

    Y9User findByPersonIdIsNotAndMobileAndTenantId(String personId, String mobile, String tenantId);

    @Query(value = "from Y9User where tenantId = ?1 and caid = ?2 and personId <> ?3")
    Y9User findByTenantIdAndCaidAndPersonIdNot(String tenantId, String caid, String personId);

    List<Y9User> findByTenantId(String tenantId);

    Y9User findByTenantIdAndCaid(String tenantId, String caid);
}
