package net.risesoft.repository.org;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.org.Y9OrgBaseMoved;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Repository
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface Y9OrgBaseMovedRepository
    extends JpaRepository<Y9OrgBaseMoved, String>, JpaSpecificationExecutor<Y9OrgBaseMoved> {

}
