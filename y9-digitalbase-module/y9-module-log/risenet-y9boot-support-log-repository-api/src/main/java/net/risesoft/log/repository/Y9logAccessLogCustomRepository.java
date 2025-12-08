package net.risesoft.log.repository;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import net.risesoft.log.domain.Y9LogAccessLogDO;
import net.risesoft.model.log.AccessLog;
import net.risesoft.model.log.AccessLogQuery;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;

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

    List<Long> listOperateTimeCount(String startDay, String endDay);

    Y9Page<AccessLog> pageByOperateType(String operateType, Integer page, Integer rows);

    Y9Page<AccessLog> pageByOrgType(List<String> personIds, String operateType, Integer page, Integer rows);

    Page<Y9LogAccessLogDO> pageByTenantIdAndManagerLevelAndUserId(String tenantId, String managerLevel, String userId,
        Integer page, Integer rows, String sort);

    Page<Y9LogAccessLogDO> pageElapsedTimeByCondition(AccessLogQuery search, String startDay, String endDay,
        String startTime, String endTime, Integer page, Integer rows) throws ParseException;

    Page<Y9LogAccessLogDO> pageOperateStatusByOperateStatus(AccessLogQuery search, String operateStatus, String date,
        String hour, Integer page, Integer rows) throws ParseException;

    Y9Page<AccessLog> pageSearchByCondition(AccessLogQuery search, Y9PageQuery pageQuery);

    void save(Y9LogAccessLogDO y9LogAccessLogDO);

    Page<Y9LogAccessLogDO> searchQuery(String tenantId, String managerLevel, AccessLogQuery loginInfoModel,
        Integer page, Integer rows);
}
