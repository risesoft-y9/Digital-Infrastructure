package net.risesoft.y9public.repository;

import java.util.List;
import java.util.Optional;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import net.risesoft.y9public.entity.Y9System;

/**
 * Y9SystemRepository
 *
 * @author shidaobang
 * @date 2022/3/2
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
@JaversSpringDataAuditable
public interface Y9SystemRepository extends JpaRepository<Y9System, String>, JpaSpecificationExecutor<Y9System> {

    List<Y9System> findByAutoInit(Boolean autoInit);

    List<Y9System> findByCnNameContainingOrderByTabIndexAsc(String cnName);

    List<Y9System> findByContextPath(String contextPath);

    List<Y9System> findByTenantIdOrderByTabIndexAsc(String tenantId);

    Optional<Y9System> findByName(String name);

    Optional<Y9System> findTopByOrderByTabIndexDesc();

}
