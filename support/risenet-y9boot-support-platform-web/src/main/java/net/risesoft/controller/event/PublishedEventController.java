package net.risesoft.controller.event;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.log.annotation.RiseLog;
import net.risesoft.pojo.Y9Page;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.event.Y9PublishedEvent;
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
@RequestMapping(value = "/api/rest/publishedEvent", produces = "application/json")
@RequiredArgsConstructor
@Validated
public class PublishedEventController {

    private final Y9PublishedEventService y9PublishedEventService;

    /**
     * 获取操作事件分页列表
     *
     * @param page 页数
     * @param rows 条数
     * @param eventName 事件名称
     * @param eventDescription 事件描述
     * @param startTime 开始时间 格式 yyyy-MM-dd HH:mm:ss
     * @param endTime 结束时间 格式 yyyy-MM-dd HH:mm:ss
     * @return
     * @since 9.6.1
     */
    @RiseLog(operationName = "分页获取操作事件")
    @RequestMapping("/pagePublishedEventList")
    public Y9Page<Y9PublishedEvent> pagePublishedEventList(@RequestParam int page, @RequestParam int rows,
        String eventName, String eventDescription, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
        Page<Y9PublishedEvent> pageList = y9PublishedEventService.page(page, rows, Y9LoginUserHolder.getTenantId(),
            eventName, eventDescription, startTime, endTime);
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), pageList.getContent(),
            "获取数据成功");
    }
}
