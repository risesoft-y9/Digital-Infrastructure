package net.risesoft.y9public.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.y9public.entity.Y9ClickedApp;

@Transactional(value = "rsPublicTransactionManager", readOnly = true)
public interface Y9ClickedAppRepository
    extends JpaRepository<Y9ClickedApp, String>, JpaSpecificationExecutor<Y9ClickedApp> {

    List<Y9ClickedApp> findByTenantIdAndPersonId(String tenantId, String personId);

}
