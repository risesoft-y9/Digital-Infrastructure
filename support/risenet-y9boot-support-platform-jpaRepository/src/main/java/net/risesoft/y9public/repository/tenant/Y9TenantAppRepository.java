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

    long countByTenantIdAndSystemId(String tenantId, String systemId);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByAppId(String appId);

    List<Y9TenantApp> findByAppId(String appId);

    List<Y9TenantApp> findByAppIdAndTenancy(String appId, Boolean tenancy);

    Page<Y9TenantApp> findBySystemIdAndTenancy(String systemId, Boolean tenancy, Pageable pageable);

    List<Y9TenantApp> findByTenantIdAndAppId(String tenantId, String appId);

    Optional<Y9TenantApp> findByTenantIdAndAppIdAndTenancy(String tenantId, String appId, Boolean tenancy);

    List<Y9TenantApp> findByTenantIdAndSystemId(String tenantId, String systemId);

    List<Y9TenantApp> findByTenantIdAndTenancy(String tenantId, Boolean tenancy);

    List<Y9TenantApp> findByTenantIdAndVerifyAndTenancyOrderByCreateTimeDesc(String tenantId, Boolean verify,
        Boolean tenancy);

    // 租户应用审核信息列表，根据当前租户ID获取租户应用申请的审核信息列表
    Page<Y9TenantApp> findPageByTenantIdAndTenancyOrderByVerify(String tenantId, Boolean tenancy, Pageable pageable);

}
