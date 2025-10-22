package net.risesoft.y9public.repository.tenant;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import net.risesoft.y9public.entity.tenant.Y9TenantSystem;

@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
public interface Y9TenantSystemRepository
    extends JpaRepository<Y9TenantSystem, String>, JpaSpecificationExecutor<Y9TenantSystem> {

    long countBySystemId(String systemId);

    long countByTenantId(String tenantId);

    List<Y9TenantSystem> findBySystemId(String systemId);

    List<Y9TenantSystem> findByTenantDataSource(String tenantDataSource);

    List<Y9TenantSystem> findByTenantId(String tenantId);

    Page<Y9TenantSystem> findByTenantId(String tenantId, Pageable pageable);

    Optional<Y9TenantSystem> findByTenantIdAndSystemId(String tenantId, String systemId);

}
