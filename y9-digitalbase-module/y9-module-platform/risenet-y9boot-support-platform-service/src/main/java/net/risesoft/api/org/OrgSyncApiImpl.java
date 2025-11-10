package net.risesoft.api.org;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.validation.constraints.NotBlank;

import org.springframework.context.annotation.Primary;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.OrgSyncApi;
import net.risesoft.model.platform.MessageOrg;
import net.risesoft.model.platform.PublishedEvent;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9public.service.event.Y9PublishedEventService;

/**
 * 组织同步组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Primary
@Validated
@RestController
@RequestMapping(value = "/services/rest/v1/orgSync", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class OrgSyncApiImpl implements OrgSyncApi {

    private final Y9PublishedEventService y9PublishedEventService;

    /**
     * 从上一次同步成功的时间后开始获取组织节点实体事件列表
     * 
     * @param since 上次成功同步时间，日期格式为 yyyy-MM-dd HH:mm:ss
     * @param tenantId 租户id
     * @return {@code Y9Result<List<MessageOrg>>} 通用请求返回对象 - data 是事件列表
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<MessageOrg<Serializable>>> incrSync(
        @RequestParam("since") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date since,
        @RequestParam("tenantId") @NotBlank String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<PublishedEvent> list = y9PublishedEventService.listByTenantId(tenantId, since);
        List<MessageOrg<Serializable>> eventList = new ArrayList<>();
        for (PublishedEvent event : list) {
            MessageOrg<Serializable> riseEvent = new MessageOrg<>(
                Y9JsonUtil.readValue(event.getEntityJson(), Serializable.class), event.getEventType(), tenantId);
            eventList.add(riseEvent);
        }
        return Y9Result.success(eventList, "获取成功！");
    }

}
