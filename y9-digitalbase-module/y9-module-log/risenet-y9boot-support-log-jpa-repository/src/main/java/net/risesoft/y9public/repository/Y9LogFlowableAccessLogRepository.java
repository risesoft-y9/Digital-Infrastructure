package net.risesoft.y9public.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import net.risesoft.y9public.entity.Y9LogFlowableAccessLog;

/**
 * @author qinman
 * @date 2025/05/22
 */
public interface Y9LogFlowableAccessLogRepository
    extends JpaRepository<Y9LogFlowableAccessLog, String>, JpaSpecificationExecutor<Y9LogFlowableAccessLog> {

}