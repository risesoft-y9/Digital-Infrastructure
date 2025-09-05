package net.risesoft.y9public.repository;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.y9public.entity.Y9LogAccessLog;

@Transactional(value = "rsPublicTransactionManager", readOnly = true)
public interface Y9LogAccessLogRepository
    extends JpaRepository<Y9LogAccessLog, String>, JpaSpecificationExecutor<Y9LogAccessLog> {

    long countByMethodNameAndLogTimeBetween(String methodName, Date logTimeStart, Date logTimeEnd);

    long countBySuccessAndLogTimeBetweenAndUserNameNotNull(String success, Date logTimeStart, Date logTimeEnd);

    long countByTenantIdAndSuccessAndLogTimeBetweenAndUserNameNotNull(String tenantId, String success,
        Date logTimeStart, Date logTimeEnd);

    Page<Y9LogAccessLog> findByOperateType(String operateType, Pageable pageable);

}