package net.risesoft.y9public.service.event.impl;

import static net.risesoft.consts.JpaPublicConsts.PUBLIC_TRANSACTION_MANAGER;

import java.util.Date;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.y9public.entity.event.Y9PublishedEventSyncHistory;
import net.risesoft.y9public.repository.event.Y9PublishedEventSyncHistoryRepository;
import net.risesoft.y9public.service.event.Y9PublishedEventSyncHistoryService;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service
@RequiredArgsConstructor
public class Y9PublishedEventSyncHistoryServiceImpl implements Y9PublishedEventSyncHistoryService {

    private final Y9PublishedEventSyncHistoryRepository y9PublishedEventSyncHistoryRepository;

    @Override
    public Optional<Y9PublishedEventSyncHistory> findByTenantIdAndAppName(String tenantId, String appName) {
        return y9PublishedEventSyncHistoryRepository.findByTenantIdAndAppName(tenantId, appName);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public Y9PublishedEventSyncHistory saveOrUpdate(String tenantId, String appName, Date syncTime, Integer status) {
        Optional<Y9PublishedEventSyncHistory> y9PublishedEventSyncHistoryOptional =
            y9PublishedEventSyncHistoryRepository.findByTenantIdAndAppName(tenantId, appName);
        if (y9PublishedEventSyncHistoryOptional.isPresent()) {
            Y9PublishedEventSyncHistory history = y9PublishedEventSyncHistoryOptional.get();
            history.setStatus(status);
            if (status == 1) {
                history.setLastSyncTime(syncTime);
            } else {
                history.setSinceSyncTime(syncTime);
            }
            return y9PublishedEventSyncHistoryRepository.save(history);
        }
        Y9PublishedEventSyncHistory history = new Y9PublishedEventSyncHistory();
        history.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        history.setAppName(appName);
        history.setTenantId(tenantId);
        history.setLastSyncTime(syncTime);
        history.setStatus(status);
        return y9PublishedEventSyncHistoryRepository.save(history);
    }

}
