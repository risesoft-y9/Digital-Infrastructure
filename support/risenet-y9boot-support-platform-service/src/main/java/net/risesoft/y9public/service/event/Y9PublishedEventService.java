package net.risesoft.y9public.service.event;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;

import net.risesoft.y9public.entity.event.Y9PublishedEvent;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9PublishedEventService {
    /**
     * 根据租户id，获取所有同步事件
     *
     * @param tenantId 租户id
     * @return {@link List}<{@link Y9PublishedEvent}>
     */
    List<Y9PublishedEvent> listByTenantId(String tenantId);

    /**
     * 根据开始时间和租户id，获取从开始时间以及之后的所有同步事件
     *
     * @param tenantId 租户id
     * @param startTime 开始事件
     * @return {@link List}<{@link Y9PublishedEvent}>
     */
    List<Y9PublishedEvent> listByTenantId(String tenantId, Date startTime);

    /**
     * 分页查询
     *
     * @param page 页数
     * @param rows 每页行数
     * @param eventName 事件名
     * @param eventDescription 事件描述
     * @param startTime 开始事件
     * @param endTime 结束事件
     * @return {@link Page}<{@link Y9PublishedEvent}>
     */
    Page<Y9PublishedEvent> page(int page, int rows, String eventName, String eventDescription, Date startTime, Date endTime);

    /**
     * 分页查询
     *
     * @param page 页数
     * @param rows 每页的行数
     * @param tenantId 租户id
     * @param eventName 事件名
     * @param eventDescription 事件描述
     * @param startTime 开始事件
     * @param endTime 结束事件
     * @return {@link Page}<{@link Y9PublishedEvent}>
     */
    Page<Y9PublishedEvent> page(int page, int rows, String tenantId, String eventName, String eventDescription, Date startTime, Date endTime);
}
