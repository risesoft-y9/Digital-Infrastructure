package net.risesoft.y9public.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.y9public.entity.event.Y9OrgSyncRole;

@Repository
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
public interface Y9OrgSyncRoleRepository extends JpaRepository<Y9OrgSyncRole, String>, JpaSpecificationExecutor<Y9OrgSyncRole> {
	
}
