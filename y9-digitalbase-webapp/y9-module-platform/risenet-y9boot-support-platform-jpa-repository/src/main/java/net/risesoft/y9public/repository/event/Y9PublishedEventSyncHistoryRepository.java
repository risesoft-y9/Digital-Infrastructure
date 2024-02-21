package net.risesoft.y9public.repository.event;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.y9public.entity.event.Y9PublishedEventSyncHistory;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
public interface Y9PublishedEventSyncHistoryRepository
    extends JpaRepository<Y9PublishedEventSyncHistory, String>, JpaSpecificationExecutor<Y9PublishedEventSyncHistory> {

    Optional<Y9PublishedEventSyncHistory> findByTenantIdAndAppName(String tenantId, String appName);
}
