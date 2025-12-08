package net.risesoft.y9public.service.impl;

import java.text.ParseException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.log.domain.Y9LogFlowableAccessLogDO;
import net.risesoft.log.repository.Y9logFlowableAccessLogCustomRepository;
import net.risesoft.model.log.FlowableAccessLog;
import net.risesoft.model.log.FlowableAccessLogQuery;
import net.risesoft.pojo.Y9Page;
import net.risesoft.y9public.service.Y9logFlowableAccessLogService;

import y9.client.rest.platform.tenant.TenantApiClient;

/**
 * @author qinman
 */
@Service
@RequiredArgsConstructor
public class Y9logFlowableAccessLogServiceImpl implements Y9logFlowableAccessLogService {

    private final TenantApiClient tenantManager;

    private final Y9logFlowableAccessLogCustomRepository y9logFlowableAccessLogCustomRepository;

    @Override
    public List<Long> listOperateTimeCount(String startDay, String endDay) {
        return y9logFlowableAccessLogCustomRepository.listOperateTimeCount(startDay, endDay);
    }

    @Override
    public Page<Y9LogFlowableAccessLogDO> page(int page, int rows, String sort) {
        return y9logFlowableAccessLogCustomRepository.page(page, rows, sort);
    }

    @Override
    public Y9Page<FlowableAccessLog> pageByCondition(FlowableAccessLogQuery searchDto, Integer page, Integer rows) {
        return y9logFlowableAccessLogCustomRepository.pageByCondition(searchDto, page, rows);
    }

    @Override
    public Page<Y9LogFlowableAccessLogDO> pageElapsedTimeByCondition(FlowableAccessLogQuery searchDto, String startDay,
        String endDay, String sTime, String lTime, Integer page, Integer rows) throws ParseException {
        return y9logFlowableAccessLogCustomRepository.pageElapsedTimeByCondition(searchDto, startDay, endDay, sTime,
            lTime, page, rows);
    }

    @Override
    public Page<Y9LogFlowableAccessLogDO> pageOperateStatusByOperateStatus(FlowableAccessLogQuery searchDto,
        String operateStatus, String date, String hour, Integer page, Integer rows) throws ParseException {
        return y9logFlowableAccessLogCustomRepository.pageOperateStatusByOperateStatus(searchDto, operateStatus, date,
            hour, page, rows);
    }

    @Override
    public Page<Y9LogFlowableAccessLogDO> pageSearchByCondition(FlowableAccessLogQuery searchDto, String startTime,
        String endTime, Integer page, Integer rows) {
        return y9logFlowableAccessLogCustomRepository.pageSearchByCondition(searchDto, startTime, endTime, page, rows);
    }

    @Override
    public void save(Y9LogFlowableAccessLogDO y9LogFlowableAccessLogDO) {
        y9logFlowableAccessLogCustomRepository.save(y9LogFlowableAccessLogDO);
    }
}
