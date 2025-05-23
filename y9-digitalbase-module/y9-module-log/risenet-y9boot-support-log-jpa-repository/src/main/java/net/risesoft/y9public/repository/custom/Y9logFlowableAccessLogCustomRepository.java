package net.risesoft.y9public.repository.custom;

import java.text.ParseException;
import java.util.List;

import org.springframework.data.domain.Page;

import net.risesoft.model.log.FlowableAccessLog;
import net.risesoft.model.log.LogInfoModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.y9public.entity.Y9logFlowableAccessLog;

/**
 *
 * @author qinman
 *
 */
public interface Y9logFlowableAccessLogCustomRepository {

    List<Long> listOperateTimeCount(String startDay, String endDay, Integer tenantType);

    Page<Y9logFlowableAccessLog> page(int page, int rows, String sort);

    Y9Page<FlowableAccessLog> pageByCondition(LogInfoModel search, String startTime, String endTime, Integer page,
        Integer rows);

    Page<Y9logFlowableAccessLog> pageElapsedTimeByCondition(LogInfoModel search, String startDay, String endDay,
        String startTime, String endTime, Integer tenantType, Integer page, Integer rows) throws ParseException;

    Page<Y9logFlowableAccessLog> pageOperateStatusByOperateStatus(LogInfoModel search, String operateStatus,
        String date, String hour, Integer tenantType, Integer page, Integer rows) throws ParseException;

    Page<Y9logFlowableAccessLog> pageSearchByCondition(LogInfoModel search, String startTime, String endTime,
        Integer tenantType, Integer page, Integer rows);

    void save(Y9logFlowableAccessLog y9logFlowableAccessLog);
}
