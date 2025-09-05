package net.risesoft.log.repository;

import java.text.ParseException;
import java.util.List;

import org.springframework.data.domain.Page;

import net.risesoft.log.domain.Y9LogFlowableAccessLogDO;
import net.risesoft.model.log.FlowableAccessLog;
import net.risesoft.model.log.LogInfoModel;
import net.risesoft.pojo.Y9Page;

/**
 *
 * @author qinman
 *
 */
public interface Y9logFlowableAccessLogCustomRepository {

    List<Long> listOperateTimeCount(String startDay, String endDay);

    Page<Y9LogFlowableAccessLogDO> page(int page, int rows, String sort);

    Y9Page<FlowableAccessLog> pageByCondition(LogInfoModel search, String startTime, String endTime, Integer page,
        Integer rows);

    Page<Y9LogFlowableAccessLogDO> pageElapsedTimeByCondition(LogInfoModel search, String startDay, String endDay,
        String startTime, String endTime, Integer page, Integer rows) throws ParseException;

    Page<Y9LogFlowableAccessLogDO> pageOperateStatusByOperateStatus(LogInfoModel search, String operateStatus,
        String date, String hour, Integer page, Integer rows) throws ParseException;

    Page<Y9LogFlowableAccessLogDO> pageSearchByCondition(LogInfoModel search, String startTime, String endTime,
        Integer page, Integer rows);

    void save(Y9LogFlowableAccessLogDO y9LogFlowableAccessLogDO);
}
