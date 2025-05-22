package net.risesoft.y9public.service.impl;

import java.text.ParseException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.model.log.FlowableAccessLog;
import net.risesoft.model.log.LogInfoModel;
import net.risesoft.model.platform.Tenant;
import net.risesoft.pojo.Y9Page;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.Y9logFlowableAccessLog;
import net.risesoft.y9public.repository.custom.Y9logFlowableAccessLogCustomRepository;
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

    private Integer getTenantType(String tenantId) {
        Integer num = 0;
        Tenant tenant = tenantManager.getById(tenantId).getData();
        if (null != tenant) {
            num = tenant.getTenantType().getValue();
        }
        return num;
    }

    @Override
    public List<Long> listOperateTimeCount(String startDay, String endDay) {
        return y9logFlowableAccessLogCustomRepository.listOperateTimeCount(startDay, endDay,
            getTenantType(Y9LoginUserHolder.getTenantId()));
    }

    @Override
    public Page<Y9logFlowableAccessLog> page(int page, int rows, String sort) {
        return y9logFlowableAccessLogCustomRepository.page(page, rows, sort);
    }

    @Override
    public Y9Page<FlowableAccessLog> pageByCondition(LogInfoModel searchDto, String startTime, String endTime,
        Integer page, Integer rows) {
        return y9logFlowableAccessLogCustomRepository.pageByCondition(searchDto, startTime, endTime, page, rows);
    }

    @Override
    public Page<Y9logFlowableAccessLog> pageElapsedTimeByCondition(LogInfoModel searchDto, String startDay,
        String endDay, String sTime, String lTime, Integer page, Integer rows) throws ParseException {
        return y9logFlowableAccessLogCustomRepository.pageElapsedTimeByCondition(searchDto, startDay, endDay, sTime,
            lTime, getTenantType(Y9LoginUserHolder.getTenantId()), page, rows);
    }

    @Override
    public Page<Y9logFlowableAccessLog> pageOperateStatusByOperateStatus(LogInfoModel searchDto, String operateStatus,
        String date, String hour, Integer page, Integer rows) throws ParseException {
        return y9logFlowableAccessLogCustomRepository.pageOperateStatusByOperateStatus(searchDto, operateStatus, date,
            hour, getTenantType(Y9LoginUserHolder.getTenantId()), page, rows);
    }

    @Override
    public Page<Y9logFlowableAccessLog> pageSearchByCondition(LogInfoModel searchDto, String startTime, String endTime,
        Integer page, Integer rows) {
        return y9logFlowableAccessLogCustomRepository.pageSearchByCondition(searchDto, startTime, endTime,
            getTenantType(Y9LoginUserHolder.getTenantId()), page, rows);
    }

    @Override
    public void save(Y9logFlowableAccessLog y9logFlowableAccessLog) {
        y9logFlowableAccessLogCustomRepository.save(y9logFlowableAccessLog);
    }
}
