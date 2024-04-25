package net.risesoft.y9public.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.y9public.entity.Y9logAccessLog;

@Transactional(value = "rsPublicTransactionManager", readOnly = true)
public interface Y9logAccessLogRepository
    extends JpaRepository<Y9logAccessLog, String>, JpaSpecificationExecutor<Y9logAccessLog> {

    long countBySuccessAndLogTimeBetweenAndUserNameNotNull(String success, long logTimeStart, long logTimeEnd);

    long countByTenantIdAndSuccessAndLogTimeBetweenAndUserNameNotNull(String tenantId, String success,
        long logTimeStart, long logTimeEnd);

    long countByModularNameAndLogTimeBetween(String ModularName, long logTimeStart, long logTimeEnd);

    Page<Y9logAccessLog> findByOperateType(String operateType, Pageable pageable);

}