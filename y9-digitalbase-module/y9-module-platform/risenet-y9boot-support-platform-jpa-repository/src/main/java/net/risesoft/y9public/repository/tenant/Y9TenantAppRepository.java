package net.risesoft.y9public.repository.tenant;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.y9public.entity.tenant.Y9TenantApp;

@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
public interface Y9TenantAppRepository
        extends JpaRepository<Y9TenantApp, String>, JpaSpecificationExecutor<Y9TenantApp> {

    List<Y9TenantApp> findByAppId(String appId);

    List<Y9TenantApp> findByAppIdAndTenancy(String appId, Boolean tenancy);

    List<Y9TenantApp> findByTenantIdAndAppId(String tenantId, String appId);

    Optional<Y9TenantApp> findByTenantIdAndAppIdAndTenancy(String tenantId, String appId, Boolean tenancy);

    List<Y9TenantApp> findByTenantIdAndSystemId(String tenantId, String systemId);

    List<Y9TenantApp> findByTenantIdAndTenancy(String tenantId, Boolean tenancy);

    List<Y9TenantApp> findByTenantIdAndVerifyAndTenancyOrderByCreateTimeDesc(String tenantId, Boolean verify,
                                                                             Boolean tenancy);

    List<Y9TenantApp> findByTenantIdAndSystemIdAndVerifyAndTenancy(String tenantId, String systemId, Boolean verify,
                                                                   Boolean tenancy);
}
