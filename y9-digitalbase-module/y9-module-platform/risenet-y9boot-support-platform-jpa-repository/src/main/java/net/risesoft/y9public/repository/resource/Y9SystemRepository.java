package net.risesoft.y9public.repository.resource;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.y9public.entity.resource.Y9System;

/**
 * Y9SystemRepository
 *
 * @author shidaobang
 * @date 2022/3/2
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
// @JaversSpringDataAuditable
public interface Y9SystemRepository extends JpaRepository<Y9System, String>, JpaSpecificationExecutor<Y9System> {

    List<Y9System> findByAutoInit(Boolean autoInit);

    List<Y9System> findByCnNameContainingOrderByTabIndexAsc(String cnName);

    List<Y9System> findByContextPath(String contextPath);

    List<Y9System> findByTenantIdOrderByTabIndexAsc(String tenantId);

    Optional<Y9System> findByName(String name);

    Optional<Y9System> findTopByOrderByTabIndexDesc();

}
