package net.risesoft.y9public.service.event;

import java.util.Date;
import java.util.Optional;

import net.risesoft.y9public.entity.event.Y9PublishedEventSyncHistory;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9PublishedEventSyncHistoryService {

    /**
     * 根据租户id和应用名查找
     *
     * @param tenantId 租户id
     * @param appName 应用名
     * @return {@link Y9PublishedEventSyncHistory}
     */
    Optional<Y9PublishedEventSyncHistory> findByTenantIdAndAppName(String tenantId, String appName);

    /**
     * 保存
     *
     * @param tenantId 租户id
     * @param appName 应用名
     * @param syncTime
     * @return {@link Y9PublishedEventSyncHistory}
     */
    Y9PublishedEventSyncHistory saveOrUpdate(String tenantId, String appName, Date syncTime);
}
