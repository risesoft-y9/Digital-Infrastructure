package net.risesoft.util;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.pubsub.Y9PublishService;
import net.risesoft.y9.pubsub.message.Y9MessageCommon;
import net.risesoft.y9.pubsub.message.Y9MessageOrg;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Slf4j
public class Y9PublishServiceUtil {
    private static boolean inited = false;
    private static List<Y9PublishService> y9PublishServices = new ArrayList<>();
    private static JdbcTemplate jdbcTemplate;

    private static void checkBeans() {
        if (inited) {
            return;
        }

        try {
            Map<String, Y9PublishService> beans = Y9Context.getBeans(Y9PublishService.class);
            y9PublishServices.addAll(beans.values());
            jdbcTemplate = Y9Context.getBean("jdbcTemplate4Public");
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }

        inited = true;
    }

    public static void persistAndPublishMessageOrg(Y9MessageOrg msg, String eventName, String description) {
        checkBeans();

        publishMessageOrg(msg);

        persistMessageOrg(msg, eventName, description);
    }

    private static void persistMessageCommon(Y9MessageCommon msg, String eventName, String description,
        String clientIp) {
        try {
            String objId = "";
            String tenantId = "";
            String operator =
                Y9LoginUserHolder.getUserInfo() == null ? "系统" : Y9LoginUserHolder.getUserInfo().getName();
            jdbcTemplate.update(
                "insert into Y9_PUBLISHED_EVENT(ID,TENANT_ID,EVENT_TYPE,EVENT_NAME,OBJ_ID,OPERATOR,CLIENT_IP,EVENT_DESCRIPTION, CREATE_TIME) values(?,?,?,?,?,?,?,?,?)",
                Y9IdGenerator.genId(IdType.SNOWFLAKE), tenantId, msg.getEventType(), eventName, objId, operator,
                clientIp, description, new Date());
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    private static void persistMessageOrg(Y9MessageOrg msg, String eventName, String description) {
        try {
            String operator =
                Y9LoginUserHolder.getUserInfo() == null ? "系统" : Y9LoginUserHolder.getUserInfo().getName();
            String clientIp = null;
            ServletRequestAttributes sra = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
            if (sra != null) {
                HttpServletRequest request = sra.getRequest();
                clientIp = Y9Context.getIpAddr(request);
            }
            if (StringUtils.isBlank(clientIp)) {
                clientIp = Y9Context.getHostIp();
            }
            String objId = "";
            Object orgObj = msg.getOrgObj();
            if (orgObj instanceof OrgUnit) {
                OrgUnit orgUnit = (OrgUnit)orgObj;
                objId = orgUnit.getId();
            }

            jdbcTemplate.update(
                "insert into Y9_PUBLISHED_EVENT(ID,TENANT_ID,EVENT_TYPE,EVENT_NAME,OBJ_ID,OPERATOR,CLIENT_IP,EVENT_DESCRIPTION,ENTITY_JSON, CREATE_TIME) values(?,?,?,?,?,?,?,?,?,?)",
                Y9IdGenerator.genId(IdType.SNOWFLAKE), msg.getTenantId(), msg.getEventType(), eventName, objId,
                operator, clientIp, description, Y9JsonUtil.writeValueAsString(orgObj), new Date());
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    public static void persistPublishMessageCommon(Y9MessageCommon msg, String eventName, String description,
        String clientIp) {
        checkBeans();

        persistMessageCommon(msg, eventName, description, clientIp);
    }

    public static void publishMessageOrg(Y9MessageOrg msg) {
        checkBeans();

        for (Y9PublishService s : y9PublishServices) {
            s.publishMessageOrg(msg);
        }
    }

}
