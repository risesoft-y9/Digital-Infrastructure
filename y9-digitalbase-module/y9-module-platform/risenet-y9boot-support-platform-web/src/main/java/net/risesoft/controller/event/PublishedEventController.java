package net.risesoft.controller.event;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.permission.annotation.IsAnyManager;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.event.Y9PublishedEvent;
import net.risesoft.y9public.service.event.Y9PublishedEventService;
import net.risesoft.y9public.specification.query.PublishedEventQuery;

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
     * @param eventName 事件名称
     * @param eventDescription 事件描述
     * @param startTime 开始时间 格式 yyyy-MM-dd HH:mm:ss
     * @param endTime 结束时间 格式 yyyy-MM-dd HH:mm:ss
     * @param pageQuery 分页
     * @return {@code Y9Page<Y9PublishedEvent>}
     *
     * @since 9.6.3
     */
    @RiseLog(operationName = "分页获取操作事件")
    @RequestMapping("/pagePublishedEventList")
    public Y9Page<Y9PublishedEvent> pagePublishedEventList(String eventName, String eventDescription,
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime, Y9PageQuery pageQuery) {
        Page<Y9PublishedEvent> pageList;
        if (Y9LoginUserHolder.getUserInfo().getManagerLevel().equals(ManagerLevelEnum.OPERATION_SECURITY_MANAGER)) {
            PublishedEventQuery query = PublishedEventQuery.builder()
                .eventName(eventName)
                .eventDescription(eventDescription)
                .startTime(startTime)
                .endTime(endTime)
                .build();
            pageList = y9PublishedEventService.page(pageQuery, query);
        } else {
            PublishedEventQuery query = PublishedEventQuery.builder()
                .tenantId(Y9LoginUserHolder.getTenantId())
                .eventName(eventName)
                .eventDescription(eventDescription)
                .startTime(startTime)
                .endTime(endTime)
                .build();
            pageList = y9PublishedEventService.page(pageQuery, query);
        }
        return Y9Page.success(pageQuery.getPage(), pageList.getTotalPages(), pageList.getTotalElements(),
            pageList.getContent(), "获取数据成功");
    }
}
