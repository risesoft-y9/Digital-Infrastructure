package net.risesoft.y9public.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import net.risesoft.y9public.entity.Y9User;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
public interface Y9UserRepository extends JpaRepository<Y9User, String>, JpaSpecificationExecutor<Y9User> {

    List<Y9User> findByGuidPathContaining(String guidPath);

    List<Y9User> findByLoginName(String loginName);

    List<Y9User> findByTenantId(String tenantId);

    Optional<Y9User> findByTenantIdAndCaid(String tenantId, String caid);

    Optional<Y9User> findByLoginNameAndTenantIdAndOriginalTrue(String loginName, String tenantId);

    Optional<Y9User> findByPersonIdAndTenantId(String personId, String tenantId);
}
