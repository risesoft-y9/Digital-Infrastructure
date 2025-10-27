package net.risesoft.y9public.service.event;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;

import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.y9public.entity.event.Y9PublishedEvent;
import net.risesoft.y9public.specification.query.PublishedEventQuery;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9PublishedEventService {

    /**
     * 根据开始时间和租户id，获取从开始时间以及之后的所有同步事件
     *
     * @param tenantId 租户id
     * @param startTime 开始事件
     * @return {@code List<Y9PublishedEvent>}
     */
    List<Y9PublishedEvent> listByTenantId(String tenantId, Date startTime);

    /**
     * 分页查询
     *
     * @param pageQuery 分页信息
     * @param query 查询条件
     * @return {@code Page<Y9PublishedEvent>}
     */
    Page<Y9PublishedEvent> page(Y9PageQuery pageQuery, PublishedEventQuery query);
}
