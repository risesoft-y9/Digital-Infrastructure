package net.risesoft.controller;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.model.platform.PublishedEvent;
import net.risesoft.permission.annotation.IsAnyManager;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.query.platform.PublishedEventQuery;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.service.event.Y9PublishedEventService;

/**
 * 操作事件管理
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/14
 */
@RestController
@RequestMapping(value = "/api/rest/publishedEvent", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
@IsAnyManager({ManagerLevelEnum.OPERATION_SECURITY_MANAGER, ManagerLevelEnum.SECURITY_MANAGER})
public class PublishedEventController {

    private final Y9PublishedEventService y9PublishedEventService;

    /**
     * 获取操作事件分页列表
     *
     * @param query 查询条件
     * @param pageQuery 分页
     * @return {@code Y9Page<Y9PublishedEvent>}
     *
     * @since 9.6.3
     */
    @RiseLog(operationName = "分页获取操作事件")
    @RequestMapping("/pagePublishedEventList")
    public Y9Page<PublishedEvent> pagePublishedEventList(PublishedEventQuery query, Y9PageQuery pageQuery) {
        if (Y9LoginUserHolder.getUserInfo().getManagerLevel().equals(ManagerLevelEnum.OPERATION_SECURITY_MANAGER)) {
            return y9PublishedEventService.page(pageQuery, query);
        } else {
            query.setTenantId(Y9LoginUserHolder.getTenantId());
            return y9PublishedEventService.page(pageQuery, query);
        }
    }
}
