package net.risesoft.y9public.repository.resource;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
//@JaversSpringDataAuditable
public interface Y9SystemRepository extends JpaRepository<Y9System, String>, JpaSpecificationExecutor<Y9System> {

    List<Y9System> findByAutoInit(Boolean autoInit);

    Y9System findByCnName(String cnName);

    Page<Y9System> findByCnNameContaining(String cnName, Pageable pageable);

    List<Y9System> findByCnNameContainingOrderByTabIndexAsc(String cnName);

    List<Y9System> findByContextPath(String contextPath);

    List<Y9System> findByContextPathContaining(String contextPath);

    List<Y9System> findByIsvGuidAndNameContaining(String isvGuid, String name);

    Page<Y9System> findByIsvGuidAndNameContaining(String isvGuid, String name, Pageable pageable);

    List<Y9System> findByIsvGuidOrderByTabIndexAsc(String isvGuid);

    Y9System findByName(String name);

    List<Y9System> findByNameContaining(String name);

    List<Y9System> findByNameIn(List<String> names);

    Y9System findTopByOrderByTabIndexDesc();

}
