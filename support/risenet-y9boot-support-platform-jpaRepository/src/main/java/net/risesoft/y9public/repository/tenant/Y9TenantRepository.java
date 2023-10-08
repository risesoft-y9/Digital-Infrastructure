package net.risesoft.y9public.repository.tenant;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.y9public.entity.tenant.Y9Tenant;

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
public interface Y9TenantRepository extends JpaRepository<Y9Tenant, String>, JpaSpecificationExecutor<Y9Tenant> {

    long countByShortName(String shortName);

    long countByShortNameAndIdIsNot(String shortName, String tenantId);

    List<Y9Tenant> findByGuidPathContaining(String guidPath);

    Optional<Y9Tenant> findByName(String name);

    List<Y9Tenant> findByParentIdIsNullOrderByTabIndexAsc();

    List<Y9Tenant> findByParentIdOrderByTabIndexAsc(String parentId);

    Optional<Y9Tenant> findByShortName(String shortName);

    List<Y9Tenant> findByTenantTypeAndParentIdIsNullOrderByTabIndexAsc(Integer tenantType);

    List<Y9Tenant> findByTenantTypeIn(List<Integer> tenantType);

    List<Y9Tenant> findByTenantTypeOrderByTabIndexAsc(Integer tenantType);

    Optional<Y9Tenant> findTopByOrderByTabIndexDesc();

}
