package net.risesoft.y9public.repository.custom.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.log.constant.Y9LogSearchConsts;
import net.risesoft.model.log.FlowableAccessLog;
import net.risesoft.model.log.LogInfoModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.util.AccessLogModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Day;
import net.risesoft.y9public.entity.Y9logFlowableAccessLog;
import net.risesoft.y9public.repository.Y9logFlowableAccessLogRepository;
import net.risesoft.y9public.repository.custom.Y9logFlowableAccessLogCustomRepository;

/**
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 */
@Component
@Slf4j
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
public class Y9logFlowableAccessLogCustomRepositoryImpl implements Y9logFlowableAccessLogCustomRepository {

    private static final FastDateFormat DATE_TIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");

    private final Y9logFlowableAccessLogRepository y9logFlowableAccessLogRepository;

    public Y9logFlowableAccessLogCustomRepositoryImpl(
        Y9logFlowableAccessLogRepository y9logFlowableAccessLogRepository) {
        this.y9logFlowableAccessLogRepository = y9logFlowableAccessLogRepository;
    }

    public long getOperateTimeCount(Date startDay, Date endDay, boolean betweenAble, long elapsedTimeStart,
        Long elapsedTimeEnd) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return y9logFlowableAccessLogRepository.count(new Specification<Y9logFlowableAccessLog>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9logFlowableAccessLog> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> list = predicate.getExpressions();
                list.add(criteriaBuilder.isNotNull(root.get(Y9LogSearchConsts.USER_NAME).as(String.class)));

                if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.TENANT_ID).as(String.class),
                        Y9LoginUserHolder.getTenantId()));
                }
                list.add(
                    criteriaBuilder.between(root.get(Y9LogSearchConsts.LOG_TIME).as(Date.class), startDay, endDay));

                if (betweenAble) {
                    list.add(criteriaBuilder.between(root.get(Y9LogSearchConsts.ELAPSED_TIME).as(Long.class),
                        elapsedTimeStart, elapsedTimeEnd));
                } else {
                    list.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get(Y9LogSearchConsts.ELAPSED_TIME).as(Long.class), elapsedTimeStart));
                }
                return predicate;
            }
        });
    }

    @Override
    public List<Long> listOperateTimeCount(String startDay, String endDay) {
        Date sDay = null;
        Date eDay = null;
        List<Long> list = new ArrayList<>();
        long[] longArray = {1L, 1000000L, 10000000L, 100000000L, 1000000000L, 5000000000L, 10000000000L};

        if (StringUtils.isNotBlank(startDay)) {
            try {
                Date day = DATE_FORMAT.parse(startDay);
                sDay = Y9Day.getStartOfDay(day);
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        if (StringUtils.isNotBlank(endDay)) {
            try {
                Date day = DATE_FORMAT.parse(endDay);
                eDay = Y9Day.getEndOfDay(day);
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        for (int i = 0; i < longArray.length; i++) {

            if (i < longArray.length - 1) {
                list.add(this.getOperateTimeCount(sDay, eDay, true, longArray[i], longArray[i + 1]));
            } else {
                list.add(this.getOperateTimeCount(sDay, eDay, false, longArray[i], null));
            }
        }
        return list;
    }

    @Override
    public Page<Y9logFlowableAccessLog> page(int page, int rows, String sort) {
        Pageable pageable;
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (StringUtils.isNoneBlank(sort)) {
            pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, sort);
        } else {
            pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        }

        return y9logFlowableAccessLogRepository.findAll(new Specification<Y9logFlowableAccessLog>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9logFlowableAccessLog> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> list = predicate.getExpressions();

                if (StringUtils.isNotBlank(tenantId)) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.TENANT_ID).as(String.class), tenantId));
                }

                list.add(criteriaBuilder.isNotNull(root.get(Y9LogSearchConsts.USER_NAME).as(String.class)));
                return predicate;
            }
        }, pageable);
    }

    @Override
    public Y9Page<FlowableAccessLog> pageByCondition(LogInfoModel searchDto, String startTime, String endTime,
        Integer page, Integer rows) {
        Pageable pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.Direction.DESC, Y9LogSearchConsts.LOG_TIME);

        Page<Y9logFlowableAccessLog> pageInfo =
            y9logFlowableAccessLogRepository.findAll(new Specification<Y9logFlowableAccessLog>() {
                private static final long serialVersionUID = -2210269486911993525L;

                @Override
                public Predicate toPredicate(Root<Y9logFlowableAccessLog> root, CriteriaQuery<?> query,
                    CriteriaBuilder criteriaBuilder) {
                    Predicate predicate = criteriaBuilder.conjunction();
                    List<Expression<Boolean>> list = predicate.getExpressions();

                    if (StringUtils.isNotBlank(searchDto.getLogLevel())) {
                        list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.LOG_LEVEL).as(String.class),
                            searchDto.getLogLevel()));
                    }

                    if (StringUtils.isNotBlank(searchDto.getSuccess())) {
                        list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.SUCCESS).as(String.class),
                            searchDto.getSuccess()));
                    }
                    if (StringUtils.isNotBlank(searchDto.getOperateType())) {
                        list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.OPERATE_TYPE).as(String.class),
                            searchDto.getOperateType()));
                    }

                    if (StringUtils.isNotBlank(searchDto.getUserName())) {
                        list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.USER_NAME).as(String.class),
                            searchDto.getUserName()));
                    }
                    if (StringUtils.isNotBlank(searchDto.getUserHostIp())) {
                        list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.USER_HOST_IP).as(String.class),
                            searchDto.getUserHostIp()));
                    }
                    if (StringUtils.isNotEmpty(searchDto.getOperateName())) {
                        list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.OPERATE_NAME).as(String.class),
                            searchDto.getOperateName()));
                    }
                    if (StringUtils.isNotBlank(searchDto.getTitle())) {
                        list.add(criteriaBuilder.like(root.get(Y9LogSearchConsts.TITLE).as(String.class),
                            "%" + searchDto.getTitle() + "%"));
                    }
                    if (StringUtils.isNotBlank(searchDto.getArguments())) {
                        list.add(criteriaBuilder.like(root.get(Y9LogSearchConsts.ARGUMENTS).as(String.class),
                            "%" + searchDto.getArguments() + "%"));
                    }
                    if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
                        String sTime = startTime + " 00:00:00";
                        String eTime = endTime + " 23:59:59";
                        try {
                            Date startDate = DATE_TIME_FORMAT.parse(sTime);
                            Date endDate = DATE_TIME_FORMAT.parse(eTime);
                            list.add(criteriaBuilder.between(root.get(Y9LogSearchConsts.LOG_TIME).as(Date.class),
                                startDate, endDate));
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    return predicate;
                }
            }, pageable);

        return Y9Page.success(page, pageInfo.getTotalPages(), pageInfo.getTotalElements(),
            AccessLogModelConvertUtil.flowableLogEsListToModels(pageInfo.getContent()));
    }

    @Override
    public Page<Y9logFlowableAccessLog> pageElapsedTimeByCondition(LogInfoModel searchDto, String startDay,
        String endDay, String sTime, String lTime, Integer page, Integer rows) throws ParseException {
        String tenantId = Y9LoginUserHolder.getTenantId();

        PageRequest pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        return y9logFlowableAccessLogRepository.findAll(new Specification<Y9logFlowableAccessLog>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9logFlowableAccessLog> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> list = predicate.getExpressions();
                if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.TENANT_ID).as(String.class), tenantId));
                }
                if (StringUtils.isNotBlank(searchDto.getLogLevel())) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.LOG_LEVEL).as(String.class),
                        searchDto.getLogLevel()));
                }

                if (StringUtils.isNotBlank(searchDto.getSuccess())) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.SUCCESS).as(String.class),
                        searchDto.getSuccess()));
                }
                if (StringUtils.isNotBlank(searchDto.getOperateType())) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.OPERATE_TYPE).as(String.class),
                        searchDto.getOperateType()));
                }
                if (StringUtils.isNotBlank(searchDto.getTitle())) {
                    list.add(criteriaBuilder.like(root.get(Y9LogSearchConsts.TITLE).as(String.class),
                        "%" + searchDto.getTitle() + "%"));
                }
                if (StringUtils.isNotBlank(searchDto.getArguments())) {
                    list.add(criteriaBuilder.like(root.get(Y9LogSearchConsts.ARGUMENTS).as(String.class),
                        "%" + searchDto.getArguments() + "%"));
                }
                if (StringUtils.isNotBlank(startDay) && StringUtils.isNotBlank(endDay)) {
                    try {
                        Date sDay = Y9Day.getStartOfDay(DATE_FORMAT.parse(startDay));
                        Date eDay = Y9Day.getEndOfDay(DATE_FORMAT.parse(endDay));
                        list.add(
                            criteriaBuilder.between(root.get(Y9LogSearchConsts.LOG_TIME).as(Date.class), sDay, eDay));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
                if (StringUtils.isNotBlank(sTime)) {
                    long smallTime = Long.parseLong(sTime);
                    if (StringUtils.isNotBlank(lTime)) {
                        long largeTime = Long.parseLong(lTime);
                        list.add(criteriaBuilder.between(root.get(Y9LogSearchConsts.ELAPSED_TIME).as(Long.class),
                            smallTime, largeTime));
                    } else {

                        list.add(criteriaBuilder.greaterThan(root.get(Y9LogSearchConsts.ELAPSED_TIME).as(Long.class),
                            smallTime));
                    }
                }
                list.add(criteriaBuilder.isNotNull(root.get(Y9LogSearchConsts.USER_NAME).as(String.class)));
                return predicate;
            }
        }, pageable);
    }

    @Override
    public Page<Y9logFlowableAccessLog> pageOperateStatusByOperateStatus(LogInfoModel searchDto, String operateStatus,
        String date, String hour, Integer page, Integer rows) throws ParseException {
        String tenantId = Y9LoginUserHolder.getTenantId();

        PageRequest pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        return y9logFlowableAccessLogRepository.findAll(new Specification<Y9logFlowableAccessLog>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9logFlowableAccessLog> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> list = predicate.getExpressions();

                list.add(criteriaBuilder.isNotNull(root.get(Y9LogSearchConsts.USER_NAME).as(String.class)));

                if (StringUtils.isNotBlank(operateStatus)) {
                    list.add(
                        criteriaBuilder.equal(root.get(Y9LogSearchConsts.SUCCESS).as(String.class), operateStatus));
                }

                if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.TENANT_ID).as(String.class), tenantId));
                }
                if (StringUtils.isNotBlank(searchDto.getUserName())) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.USER_NAME).as(String.class),
                        searchDto.getUserName()));
                }
                if (StringUtils.isNotBlank(searchDto.getTenantName())) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.TENANT_NAME).as(String.class),
                        searchDto.getTenantName()));
                }

                if (StringUtils.isNotBlank(searchDto.getLogLevel())) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.LOG_LEVEL).as(String.class),
                        searchDto.getLogLevel()));
                }
                if (StringUtils.isNotBlank(searchDto.getOperateType())) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.OPERATE_TYPE).as(String.class),
                        searchDto.getOperateType()));
                }
                if (StringUtils.isNotBlank(searchDto.getTitle())) {
                    list.add(criteriaBuilder.like(root.get(Y9LogSearchConsts.TITLE).as(String.class),
                        "%" + searchDto.getTitle() + "%"));
                }
                if (StringUtils.isNotBlank(searchDto.getArguments())) {
                    list.add(criteriaBuilder.like(root.get(Y9LogSearchConsts.ARGUMENTS).as(String.class),
                        "%" + searchDto.getArguments() + "%"));
                }
                if (StringUtils.isNotBlank(date) && StringUtils.isNotBlank(hour)) {
                    int h = Integer.parseInt(hour);
                    Calendar cal = Calendar.getInstance();
                    try {
                        Date day = DATE_FORMAT.parse(date);
                        Date dat = Y9Day.getStartOfDay(day);
                        cal.setTime(dat);
                        cal.add(Calendar.HOUR_OF_DAY, h);
                        Date startOfTime = cal.getTime();
                        cal.add(Calendar.MINUTE, 59);
                        cal.add(Calendar.SECOND, 59);
                        Date endOfTime = cal.getTime();

                        list.add(criteriaBuilder.between(root.get(Y9LogSearchConsts.LOG_TIME).as(Date.class),
                            startOfTime, endOfTime));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                return predicate;
            }
        }, pageable);
    }

    @Override
    public Page<Y9logFlowableAccessLog> pageSearchByCondition(LogInfoModel loginInfoModel, String startTime,
        String endTime, Integer page, Integer rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        PageRequest pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        return y9logFlowableAccessLogRepository.findAll(new Specification<Y9logFlowableAccessLog>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9logFlowableAccessLog> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> list = predicate.getExpressions();
                if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.TENANT_ID).as(String.class), tenantId));
                }
                if (StringUtils.isNotBlank(loginInfoModel.getLogLevel())) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.LOG_LEVEL).as(String.class),
                        loginInfoModel.getLogLevel()));
                }
                if (StringUtils.isNotBlank(loginInfoModel.getSuccess())) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.SUCCESS).as(String.class),
                        loginInfoModel.getSuccess()));
                }
                if (StringUtils.isNotBlank(loginInfoModel.getOperateType())) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.OPERATE_TYPE).as(String.class),
                        loginInfoModel.getOperateType()));
                }
                if (StringUtils.isNotBlank(loginInfoModel.getOperateName())) {
                    list.add(criteriaBuilder.like(root.get(Y9LogSearchConsts.OPERATE_NAME).as(String.class),
                        "%" + loginInfoModel.getOperateName() + "%"));
                }
                if (StringUtils.isNotBlank(loginInfoModel.getUserName())) {
                    list.add(criteriaBuilder.like(root.get(Y9LogSearchConsts.USER_NAME).as(String.class),
                        "%" + loginInfoModel.getUserName() + "%"));
                }
                if (StringUtils.isNotBlank(loginInfoModel.getUserHostIp())) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.USER_HOST_IP).as(String.class),
                        loginInfoModel.getUserHostIp()));
                }
                if (StringUtils.isNotBlank(loginInfoModel.getTenantName())) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.TENANT_NAME).as(String.class),
                        loginInfoModel.getTenantName()));
                }
                if (StringUtils.isNotBlank(loginInfoModel.getModularName())) {
                    list.add(criteriaBuilder.like(root.get(Y9LogSearchConsts.MODULAR_NAME).as(String.class),
                        "%" + loginInfoModel.getModularName() + "%"));
                }
                if (StringUtils.isNotBlank(loginInfoModel.getTitle())) {
                    list.add(criteriaBuilder.like(root.get(Y9LogSearchConsts.TITLE).as(String.class),
                        "%" + loginInfoModel.getTitle() + "%"));
                }
                if (StringUtils.isNotBlank(loginInfoModel.getArguments())) {
                    list.add(criteriaBuilder.like(root.get(Y9LogSearchConsts.ARGUMENTS).as(String.class),
                        "%" + loginInfoModel.getArguments() + "%"));
                }

                if (StringUtils.isNotBlank(loginInfoModel.getStartTime())
                    && StringUtils.isNotBlank(loginInfoModel.getEndTime())) {
                    String sTime = startTime + " 00:00:00";
                    String eTime = endTime + " 23:59:59";
                    try {
                        Date startDate = DATE_TIME_FORMAT.parse(sTime);
                        Date endDate = DATE_TIME_FORMAT.parse(eTime);
                        list.add(criteriaBuilder.between(root.get(Y9LogSearchConsts.LOG_TIME).as(Date.class), startDate,
                            endDate));
                    } catch (ParseException e) {
                        LOGGER.warn(e.getMessage(), e);
                    }
                }
                list.add(criteriaBuilder.isNotNull(root.get(Y9LogSearchConsts.USER_NAME).as(String.class)));
                return predicate;
            }
        }, pageable);
    }

    @Override
    @Transactional(readOnly = false)
    public void save(Y9logFlowableAccessLog y9logAccessLog) {
        y9logFlowableAccessLogRepository.save(y9logAccessLog);
    }

}
