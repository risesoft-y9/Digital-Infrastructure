package net.risesoft.y9public.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.y9public.entity.Y9LogIpDeptMapping;

@Transactional(value = "rsPublicTransactionManager", readOnly = true)
public interface Y9LogIpDeptMappingRepository
    extends JpaRepository<Y9LogIpDeptMapping, String>, JpaSpecificationExecutor<Y9LogIpDeptMapping> {
    List<Y9LogIpDeptMapping> findByClientIpSection(String clientIpSection);

    List<Y9LogIpDeptMapping> findByTenantIdAndClientIpSection(String tenantId, String clientIpSection);

    List<Y9LogIpDeptMapping> findByTenantId(String tenantId, Sort sort);
}
