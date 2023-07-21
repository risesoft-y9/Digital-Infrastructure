package net.risesoft.y9public.service.event.impl;

import java.util.Date;

import org.springframework.stereotype.Service;

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
    public Y9PublishedEventSyncHistory findByTenantIdAndAppName(String tenantId, String appName) {
        return y9PublishedEventSyncHistoryRepository.findByTenantIdAndAppName(tenantId, appName);
    }

    @Override
    public Y9PublishedEventSyncHistory saveOrUpdate(String tenantId, String appName, Date syncTime) {
        Y9PublishedEventSyncHistory history = y9PublishedEventSyncHistoryRepository.findByTenantIdAndAppName(tenantId, appName);
        if (history != null) {
            history.setLastSyncTime(syncTime);
            return y9PublishedEventSyncHistoryRepository.save(history);
        }
        history = new Y9PublishedEventSyncHistory();
        history.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        history.setAppName(appName);
        history.setTenantId(tenantId);
        history.setLastSyncTime(syncTime);
        return y9PublishedEventSyncHistoryRepository.save(history);
    }

}
