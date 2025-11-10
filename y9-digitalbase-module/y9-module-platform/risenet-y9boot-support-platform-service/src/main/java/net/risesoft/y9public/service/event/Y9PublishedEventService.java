package net.risesoft.y9public.service.event;

import java.util.Date;
import java.util.List;

import net.risesoft.model.platform.PublishedEvent;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.query.platform.PublishedEventQuery;

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
    List<PublishedEvent> listByTenantId(String tenantId, Date startTime);

    /**
     * 分页查询
     *
     * @param pageQuery 分页信息
     * @param query 查询条件
     * @return {@code Page<Y9PublishedEvent>}
     */
    Y9Page<PublishedEvent> page(Y9PageQuery pageQuery, PublishedEventQuery query);
}
