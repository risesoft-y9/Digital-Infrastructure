package net.risesoft.repository.idcode;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.idcode.Y9IdCode;

@Repository
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface Y9IdCodeRepository extends JpaRepository<Y9IdCode, String>, JpaSpecificationExecutor<Y9IdCode> {

    Y9IdCode findByOrgUnitId(String orgUnitId);
}
