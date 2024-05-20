package net.risesoft.y9public.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.y9public.entity.Y9CommonAppForPerson;

@Transactional(value = "rsPublicTransactionManager", readOnly = true)
public interface Y9CommonAppForPersonRepository
    extends JpaRepository<Y9CommonAppForPerson, String>, JpaSpecificationExecutor<Y9CommonAppForPerson> {

    Y9CommonAppForPerson findByPersonId(String personId);

    Y9CommonAppForPerson findByTenantIdAndPersonId(String tenantId, String personId);
}
