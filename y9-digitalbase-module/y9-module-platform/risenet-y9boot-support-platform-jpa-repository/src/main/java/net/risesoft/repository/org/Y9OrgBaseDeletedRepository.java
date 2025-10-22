package net.risesoft.repository.org;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import net.risesoft.entity.org.Y9OrgBaseDeleted;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Repository
public interface Y9OrgBaseDeletedRepository
    extends JpaRepository<Y9OrgBaseDeleted, String>, JpaSpecificationExecutor<Y9OrgBaseDeleted> {

    Optional<Y9OrgBaseDeleted> findByOrgId(String orgId);
}
