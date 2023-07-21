package net.risesoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.Y9OrgBaseDeleted;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Repository
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface Y9OrgBaseDeletedRepository extends JpaRepository<Y9OrgBaseDeleted, String>, JpaSpecificationExecutor<Y9OrgBaseDeleted> {

    Y9OrgBaseDeleted findByOrgId(String orgId);
}
