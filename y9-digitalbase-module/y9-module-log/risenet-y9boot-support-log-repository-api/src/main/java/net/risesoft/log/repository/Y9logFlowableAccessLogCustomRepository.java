package net.risesoft.log.repository;

import java.text.ParseException;
import java.util.List;

import org.springframework.data.domain.Page;

import net.risesoft.log.domain.Y9LogFlowableAccessLogDO;
import net.risesoft.model.log.FlowableAccessLog;
import net.risesoft.model.log.FlowableAccessLogQuery;
import net.risesoft.pojo.Y9Page;

/**
 *
 * @author qinman
 *
 */
public interface Y9logFlowableAccessLogCustomRepository {

    List<Long> listOperateTimeCount(String startDay, String endDay);

    Page<Y9LogFlowableAccessLogDO> page(int page, int rows, String sort);

    Y9Page<FlowableAccessLog> pageByCondition(FlowableAccessLogQuery search, Integer page, Integer rows);

    Page<Y9LogFlowableAccessLogDO> pageElapsedTimeByCondition(FlowableAccessLogQuery search, String startDay,
        String endDay, String startTime, String endTime, Integer page, Integer rows) throws ParseException;

    Page<Y9LogFlowableAccessLogDO> pageOperateStatusByOperateStatus(FlowableAccessLogQuery search, String operateStatus,
        String date, String hour, Integer page, Integer rows) throws ParseException;

    Page<Y9LogFlowableAccessLogDO> pageSearchByCondition(FlowableAccessLogQuery search, String startTime,
        String endTime, Integer page, Integer rows);

    void save(Y9LogFlowableAccessLogDO y9LogFlowableAccessLogDO);
}
