package net.risesoft.service;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import net.risesoft.model.log.AccessLog;
import net.risesoft.model.log.LogInfoModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.y9public.entity.Y9logAccessLog;

/**
 *
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
public interface Y9logAccessLogService {

    Map<String, Object> getAppClickCount(String orgId, String orgType, String tenantId, String startDay, String endDay)
        throws UnknownHostException;

    Map<String, Object> getModuleNameCount(String orgId, String orgType, String tenantId, String startDay,
        String endDay);

    Map<String, Object> getOperateStatusCount(String selectedDate);

    List<String> listAccessLog(String startTime, String endTime, String loginName, String tenantId);

    List<Long> listOperateTimeCount(String startDay, String endDay);

    Page<Y9logAccessLog> page(int page, int rows, String sort);

    Y9Page<AccessLog> pageByCondition(LogInfoModel search, String startTime, String endTime, Integer page,
        Integer rows);

    Y9Page<AccessLog> pageByOperateType(String operateType, Integer page, Integer rows);

    Y9Page<AccessLog> pageByOrgType(String tenantId, String orgId, String orgType, String operateType, Integer page,
        Integer rows);

    Page<Y9logAccessLog> pageByTenantIdAndManagerLevelAndUserId(String tenantId, String managerLevel, String userId,
        Integer page, Integer rows, String sort);

    Page<Y9logAccessLog> pageElapsedTimeByCondition(LogInfoModel search, String startDay, String endDay,
        String startTime, String endTime, Integer page, Integer rows) throws ParseException;

    Page<Y9logAccessLog> pageOperateStatusByOperateStatus(LogInfoModel search, String operateStatus, String date,
        String hour, Integer page, Integer rows) throws ParseException;

    Page<Y9logAccessLog> pageSearchByCondition(LogInfoModel search, String startTime, String endTime, Integer page,
        Integer rows);

    void save(Y9logAccessLog y9logAccessLog);

    Page<Y9logAccessLog> searchQuery(String tenantId, String managerLevel, LogInfoModel loginInfoModel, Integer page,
        Integer rows);
}
