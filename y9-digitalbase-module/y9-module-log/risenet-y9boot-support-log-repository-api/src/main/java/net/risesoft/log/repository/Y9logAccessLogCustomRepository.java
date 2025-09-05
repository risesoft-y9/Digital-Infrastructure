package net.risesoft.log.repository;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import net.risesoft.log.domain.Y9LogAccessLogDO;
import net.risesoft.model.log.AccessLog;
import net.risesoft.model.log.LogInfoModel;
import net.risesoft.pojo.Y9Page;

/**
 *
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
public interface Y9logAccessLogCustomRepository {

    Map<String, Object> getAppClickCount(String tenantId, String guidPath, String startDay, String endDay)
        throws UnknownHostException;

    Map<String, Object> getModuleNameCount(String tenantId, String guidPath, String startDay, String endDay);

    Map<String, Object> getOperateStatusCount(String selectedDate);

    List<String> listAccessLog(String tenantId, String loginName, String startTime, String endTime);

    List<Long> listOperateTimeCount(String startDay, String endDay);

    Page<Y9LogAccessLogDO> page(int page, int rows, String sort);

    Y9Page<AccessLog> pageByCondition(LogInfoModel search, String startTime, String endTime, Integer page,
        Integer rows);

    Y9Page<AccessLog> pageByOperateType(String operateType, Integer page, Integer rows);

    Y9Page<AccessLog> pageByOrgType(String tenantId, List<String> personIds, String operateType, Integer page,
        Integer rows);

    Page<Y9LogAccessLogDO> pageByTenantIdAndManagerLevelAndUserId(String tenantId, String managerLevel, String userId,
        Integer page, Integer rows, String sort);

    Page<Y9LogAccessLogDO> pageElapsedTimeByCondition(LogInfoModel search, String startDay, String endDay,
        String startTime, String endTime, Integer page, Integer rows) throws ParseException;

    Page<Y9LogAccessLogDO> pageOperateStatusByOperateStatus(LogInfoModel search, String operateStatus, String date,
        String hour, Integer page, Integer rows) throws ParseException;

    Page<Y9LogAccessLogDO> pageSearchByCondition(LogInfoModel search, String startTime, String endTime, Integer page,
        Integer rows);

    void save(Y9LogAccessLogDO y9LogAccessLogDO);

    Page<Y9LogAccessLogDO> searchQuery(String tenantId, String managerLevel, LogInfoModel loginInfoModel, Integer page,
        Integer rows);
}
