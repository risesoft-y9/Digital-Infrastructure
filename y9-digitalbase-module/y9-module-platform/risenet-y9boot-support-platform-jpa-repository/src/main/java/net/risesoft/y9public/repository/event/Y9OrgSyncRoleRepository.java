package net.risesoft.y9public.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import net.risesoft.y9public.entity.event.Y9OrgSyncRole;

@Repository
public interface Y9OrgSyncRoleRepository
    extends JpaRepository<Y9OrgSyncRole, String>, JpaSpecificationExecutor<Y9OrgSyncRole> {

}
