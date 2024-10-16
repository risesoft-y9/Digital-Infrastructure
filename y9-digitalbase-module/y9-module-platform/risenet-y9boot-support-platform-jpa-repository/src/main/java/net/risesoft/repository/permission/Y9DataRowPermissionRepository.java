package net.risesoft.repository.permission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.permission.Y9DataRowPermission;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Repository
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface Y9DataRowPermissionRepository
    extends JpaRepository<Y9DataRowPermission, String>, JpaSpecificationExecutor<Y9DataRowPermission> {

}
