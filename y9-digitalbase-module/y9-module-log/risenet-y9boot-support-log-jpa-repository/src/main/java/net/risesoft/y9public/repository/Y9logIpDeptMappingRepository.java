package net.risesoft.y9public.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.y9public.entity.Y9logIpDeptMapping;

@Transactional(value = "rsPublicTransactionManager", readOnly = true)
public interface Y9logIpDeptMappingRepository
    extends JpaRepository<Y9logIpDeptMapping, String>, JpaSpecificationExecutor<Y9logIpDeptMapping> {
    List<Y9logIpDeptMapping> findByClientIpSection(String clientIpSection);

    List<Y9logIpDeptMapping> findByTenantIdAndClientIpSection(String tenantId, String clientIpSection);

}
