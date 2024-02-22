package net.risesoft.y9public.repository.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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

    List<Y9User> findByGuidPathContaining(String guidPath);

    List<Y9User> findByLoginName(String loginName);

    Optional<Y9User> findByTenantIdAndLoginNameAndOriginalTrue(String tenantId, String loginName);

    Optional<Y9User> findByTenantIdAndPersonId(String tenantId, String personId);

    List<Y9User> findByTenantId(String tenantId);

    Optional<Y9User> findByTenantIdAndCaid(String tenantId, String caid);
}
