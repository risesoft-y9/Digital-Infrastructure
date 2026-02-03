package net.risesoft.repository.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.log.constant.Y9LogSearchConsts;
import net.risesoft.log.domain.Y9LogAccessLogDO;
import net.risesoft.log.repository.Y9logAccessLogCustomRepository;
import net.risesoft.log.repository.Y9logMappingCustomRepository;
import net.risesoft.model.log.AccessLog;
import net.risesoft.model.log.AccessLogQuery;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.util.AccessLogModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9DayUtil;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.Y9LogAccessLog;
import net.risesoft.y9public.repository.Y9LogAccessLogRepository;

/**
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 */
@Component
@Slf4j
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
public class Y9logAccessLogCustomRepositoryImpl implements Y9logAccessLogCustomRepository {

    private static final FastDateFormat DATE_TIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");
    private final Y9logMappingCustomRepository y9logMappingCustomRepository;
    private final Y9LogAccessLogRepository y9logAccessLogRepository;
    private final JdbcTemplate jdbcTemplate4Public;

    public Y9logAccessLogCustomRepositoryImpl(
        Y9logMappingCustomRepository y9logMappingCustomRepository,
        Y9LogAccessLogRepository y9logAccessLogRepository,
        @Qualifier("jdbcTemplate4Public") JdbcTemplate jdbcTemplate4Public) {
        this.y9logMappingCustomRepository = y9logMappingCustomRepository;
        this.y9logAccessLogRepository = y9logAccessLogRepository;
        this.jdbcTemplate4Public = jdbcTemplate4Public;
    }

    private static PageImpl<Y9LogAccessLogDO> poPageToDoPage(Page<Y9LogAccessLog> y9LogAccessLogPage) {
        List<Y9LogAccessLogDO> list = y9LogAccessLogPage.getContent()
            .stream()
            .map(l -> Y9ModelConvertUtil.convert(l, Y9LogAccessLogDO.class))
            .collect(Collectors.toList());
        return new PageImpl<>(list, y9LogAccessLogPage.getPageable(), y9LogAccessLogPage.getTotalElements());
    }

    @Override
    public Map<String, Object> getAppClickCount(String tenantId, String guidPath, String startDay, String endDay) {
        Map<String, Object> returnMap = new HashMap<>();
        List<String> strList = new ArrayList<>();
        List<String> longList = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append(
            "SELECT DISTINCT(l.MODULAR_NAME) as appName,count(l.MODULAR_NAME) as count FROM  Y9_LOG_ACCESS_LOG l WHERE ");
        sql.append("l.METHOD_NAME ='" + Y9LogSearchConsts.APP_METHODNAME + "' ");

        if (StringUtils.isNotBlank(tenantId)) {
            sql.append("AND l.TENANT_ID ='" + tenantId + "' ");
        }
        if (StringUtils.isNotBlank(guidPath)) {
            sql.append("AND l.GUID_PATH LIKE '" + guidPath + "%' ");
        }

        if (StringUtils.isNotBlank(startDay) && StringUtils.isNotBlank(endDay)) {
            Date sDay = new Date();
            Date eDay = new Date();
            try {
                sDay = Y9DayUtil.getStartOfDay(DATE_FORMAT.parse(startDay));
                eDay = Y9DayUtil.getEndOfDay(DATE_FORMAT.parse(endDay));
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
            sql.append("AND l.LOG_TIME >= '" + sDay.getTime() + "' ");
            sql.append("AND l.LOG_TIME <= '" + eDay.getTime() + "' ");
        }
        sql.append("GROUP BY l.MODULAR_NAME");

        List<Map<String, Object>> countAppNames = jdbcTemplate4Public.queryForList(sql.toString());

        int length = countAppNames.size();
        for (int i = length - 1; i >= 0; i--) {
            String appName = countAppNames.get(i).get("appName").toString();
            strList.add(appName);
            longList.add(countAppNames.get(i).get("count").toString());
        }
        returnMap.put("number", length);
        returnMap.put("name", strList);
        returnMap.put("value", longList);
        return returnMap;
    }

    @Override
    public Map<String, Object> getModuleNameCount(String tenantId, String guidPath, String startDay, String endDay) {
        Map<String, Object> map = new HashMap<>();
        List<String> strList = new ArrayList<>();
        List<String> longList = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append(
            "SELECT DISTINCT(log.MODULAR_NAME) as modularname,count(log.MODULAR_NAME) as count FROM  Y9_LOG_ACCESS_LOG log WHERE ");
        sql.append("log.USER_NAME IS NOT NULL ");

        if (StringUtils.isNotBlank(tenantId)) {
            sql.append("AND log.TENANT_ID ='" + tenantId + "' ");
        }
        if (StringUtils.isNotBlank(guidPath)) {
            sql.append("AND log.GUID_PATH LIKE '" + guidPath + "%' ");
        }

        if (StringUtils.isNotBlank(startDay) && StringUtils.isNotBlank(endDay)) {
            Date sDay = new Date();
            Date eDay = new Date();
            try {
                sDay = Y9DayUtil.getStartOfDay(DATE_FORMAT.parse(startDay));
                eDay = Y9DayUtil.getEndOfDay(DATE_FORMAT.parse(endDay));
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
            sql.append("AND log.LOG_TIME > '" + sDay.getTime() + "' ");
            sql.append("AND log.LOG_TIME < '" + eDay.getTime() + "' ");
        }
        sql.append("GROUP BY log.MODULAR_NAME");
        List<Map<String, Object>> countModularNames = jdbcTemplate4Public.queryForList(sql.toString());

        int length = countModularNames.size();
        for (int i = length - 1; i >= 0; i--) {
            String modularName = countModularNames.get(i).get("modularName").toString();
            String modularCnName = y9logMappingCustomRepository.getCnModularName(modularName);
            if (StringUtils.isNotBlank(modularCnName)) {
                modularName = modularCnName;
            }
            strList.add(modularName);
            longList.add(countModularNames.get(i).get("count").toString());
        }
        int num = strList.size();
        map.put("name", strList);
        map.put("value", longList);
        map.put("number", num);
        return map;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Map<String, Object> getOperateStatusCount(String selectedDate) {
        List<Integer> time = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        List<Long> countOfSuccess = new ArrayList<>();
        List<Long> countOfError = new ArrayList<>();
        String success = "成功";
        String error = "出错";
        Date day = new Date();
        if (StringUtils.isNotBlank(selectedDate)) {
            try {
                day = DATE_FORMAT.parse(selectedDate);
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
            Date startOfTime = Y9DayUtil.getStartOfDay(day);
            Date endOfTime = null;
            for (int i = 0; i < 24; i++) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(day);
                cal.set(Calendar.HOUR_OF_DAY, i);
                startOfTime = cal.getTime();
                cal.add(Calendar.MINUTE, 59);
                cal.add(Calendar.SECOND, 59);
                endOfTime = cal.getTime();
                String tenantId = Y9LoginUserHolder.getTenantId();
                long countSuccess = 0L;
                long countError = 0L;
                if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
                    countSuccess =
                        y9logAccessLogRepository.countByTenantIdAndSuccessAndLogTimeBetweenAndUserNameNotNull(tenantId,
                            success, startOfTime, endOfTime);
                    countError = y9logAccessLogRepository.countByTenantIdAndSuccessAndLogTimeBetweenAndUserNameNotNull(
                        tenantId, error, startOfTime, endOfTime);
                } else {
                    countSuccess = y9logAccessLogRepository.countBySuccessAndLogTimeBetweenAndUserNameNotNull(success,
                        startOfTime, endOfTime);
                    countError = y9logAccessLogRepository.countBySuccessAndLogTimeBetweenAndUserNameNotNull(error,
                        startOfTime, endOfTime);
                }

                countOfSuccess.add(countSuccess);
                countOfError.add(countError);
                time.add(startOfTime.getHours());
            }
        }
        map.put("time", time);
        map.put("totalOfSuccess", countOfSuccess);
        map.put("totalOfError", countOfError);
        return map;
    }

    public long getOperateTimeCount(Date startDay, Date endDay, boolean betweenAble, long elapsedTimeStart,
        Long elapsedTimeEnd) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return y9logAccessLogRepository.count(new Specification<>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9LogAccessLog> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
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
                // 如果没有条件，返回空查询
                if (list.isEmpty()) {
                    return criteriaBuilder.conjunction(); // 相当于 WHERE 1=1
                }
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
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
                sDay = Y9DayUtil.getStartOfDay(day);
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        if (StringUtils.isNotBlank(endDay)) {
            try {
                Date day = DATE_FORMAT.parse(endDay);
                eDay = Y9DayUtil.getEndOfDay(day);
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
    public Y9Page<AccessLog> pageByOperateType(String operateType, Integer page, Integer rows) {
        Pageable pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.Direction.DESC, Y9LogSearchConsts.LOG_TIME);

        Page<Y9LogAccessLog> pageInfo = y9logAccessLogRepository.findByOperateType(operateType, pageable);

        return Y9Page.success(page, pageInfo.getTotalPages(), pageInfo.getTotalElements(),
            AccessLogModelConvertUtil.logEsListToModels(pageInfo.getContent()));
    }

    @Override
    public Y9Page<AccessLog> pageByOrgType(List<String> personIds, String operateType, Integer page, Integer rows) {
        PageRequest pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        Page<Y9LogAccessLog> pageInfo = y9logAccessLogRepository.findAll(new Specification<>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9LogAccessLog> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
                if (StringUtils.isNotBlank(operateType)) {
                    list.add(
                        criteriaBuilder.equal(root.get(Y9LogSearchConsts.OPERATE_TYPE).as(String.class), operateType));
                }
                if (personIds!=null && personIds.size()>0) {
                    // in条件拼接
                    In<String> in = criteriaBuilder.in(root.get(Y9LogSearchConsts.USER_ID).as(String.class));
                    for (String id : personIds) {
                        in.value(id);
                    }
                    list.add(in);
                }

                // 如果没有条件，返回空查询
                if (list.isEmpty()) {
                    return criteriaBuilder.conjunction(); // 相当于 WHERE 1=1
                }
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        }, pageable);

        return Y9Page.success(page, pageInfo.getTotalPages(), pageInfo.getTotalElements(),
            AccessLogModelConvertUtil.logEsListToModels(pageInfo.getContent()));

    }

    @Override
    public Page<Y9LogAccessLogDO> pageByTenantIdAndManagerLevelAndUserId(String tenantId, String managerLevel,
        String userId, Integer page, Integer rows, String sort) {
        Pageable pageable;

        if (StringUtils.isNoneBlank(sort)) {
            pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, sort);
        } else {
            pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        }

        Page<Y9LogAccessLog> y9LogAccessLogPage = y9logAccessLogRepository.findAll(new Specification<>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9LogAccessLog> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
                if (tenantId != null) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.TENANT_ID).as(String.class), tenantId));
                }
                if (StringUtils.isNotBlank(userId)) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.USER_ID).as(String.class), userId));
                }

                if (StringUtils.isNotBlank(managerLevel)) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.MANAGER_LEVEL).as(String.class),
                        managerLevel));
                }
                list.add(criteriaBuilder.isNotNull(root.get(Y9LogSearchConsts.USER_NAME).as(String.class)));
                // 如果没有条件，返回空查询
                if (list.isEmpty()) {
                    return criteriaBuilder.conjunction(); // 相当于 WHERE 1=1
                }
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        }, pageable);
        return poPageToDoPage(y9LogAccessLogPage);
    }

    @Override
    public Page<Y9LogAccessLogDO> pageElapsedTimeByCondition(AccessLogQuery searchDto, String startDay, String endDay,
        String sTime, String lTime, Integer page, Integer rows) throws ParseException {
        String tenantId = Y9LoginUserHolder.getTenantId();

        PageRequest pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        Page<Y9LogAccessLog> y9LogAccessLogPage = y9logAccessLogRepository.findAll(new Specification<>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9LogAccessLog> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
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
                if (StringUtils.isNotBlank(startDay) && StringUtils.isNotBlank(endDay)) {
                    try {
                        Date sDay = Y9DayUtil.getStartOfDay(DATE_FORMAT.parse(startDay));
                        Date eDay = Y9DayUtil.getEndOfDay(DATE_FORMAT.parse(endDay));
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
                // 如果没有条件，返回空查询
                if (list.isEmpty()) {
                    return criteriaBuilder.conjunction(); // 相当于 WHERE 1=1
                }
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        }, pageable);
        return poPageToDoPage(y9LogAccessLogPage);
    }

    @Override
    public Page<Y9LogAccessLogDO> pageOperateStatusByOperateStatus(AccessLogQuery searchDto, String operateStatus,
        String date, String hour, Integer page, Integer rows) throws ParseException {
        String tenantId = Y9LoginUserHolder.getTenantId();

        PageRequest pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        Page<Y9LogAccessLog> y9LogAccessLogPage = y9logAccessLogRepository.findAll(new Specification<>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9LogAccessLog> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();

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

                if (StringUtils.isNotBlank(searchDto.getLogLevel())) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.LOG_LEVEL).as(String.class),
                        searchDto.getLogLevel()));
                }

                if (StringUtils.isNotBlank(searchDto.getOperateType())) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.OPERATE_TYPE).as(String.class),
                        searchDto.getOperateType()));
                }

                if (StringUtils.isNotBlank(date) && StringUtils.isNotBlank(hour)) {
                    int h = Integer.parseInt(hour);
                    Calendar cal = Calendar.getInstance();
                    try {
                        Date day = DATE_FORMAT.parse(date);
                        Date dat = Y9DayUtil.getStartOfDay(day);
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
                // 如果没有条件，返回空查询
                if (list.isEmpty()) {
                    return criteriaBuilder.conjunction(); // 相当于 WHERE 1=1
                }
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        }, pageable);
        return poPageToDoPage(y9LogAccessLogPage);
    }

    @Override
    public Y9Page<AccessLog> pageSearchByCondition(AccessLogQuery loginInfoModel, Y9PageQuery pageQuery) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        PageRequest pageable =
            PageRequest.of(pageQuery.getPage4Db(), pageQuery.getSize(), Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        Page<Y9LogAccessLog> y9LogAccessLogPage = y9logAccessLogRepository.findAll(new Specification<>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9LogAccessLog> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
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
                if (StringUtils.isNotBlank(loginInfoModel.getUserName())) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.USER_NAME).as(String.class),
                        loginInfoModel.getUserName()));
                }
                if (StringUtils.isNotBlank(loginInfoModel.getUserHostIp())) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.USER_HOST_IP).as(String.class),
                        loginInfoModel.getUserHostIp()));
                }
                if (StringUtils.isNotBlank(loginInfoModel.getModularName())) {
                    list.add(criteriaBuilder.like(root.get(Y9LogSearchConsts.MODULAR_NAME).as(String.class),
                        "%" + loginInfoModel.getModularName() + "%"));
                }

                if (StringUtils.isNotBlank(loginInfoModel.getStartTime())
                    && StringUtils.isNotBlank(loginInfoModel.getEndTime())) {
                    String sTime = loginInfoModel.getStartTime() + " 00:00:00";
                    String eTime = loginInfoModel.getEndTime() + " 23:59:59";
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
                // 如果没有条件，返回空查询
                if (list.isEmpty()) {
                    return criteriaBuilder.conjunction(); // 相当于 WHERE 1=1
                }
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        }, pageable);

        return Y9Page.success(pageQuery.getPage(), y9LogAccessLogPage.getTotalPages(),
            y9LogAccessLogPage.getTotalElements(),
            AccessLogModelConvertUtil.logEsListToModels(y9LogAccessLogPage.getContent()));
    }

    @Override
    @Transactional(readOnly = false)
    public void save(Y9LogAccessLogDO y9LogAccessLogDO) {
        Y9LogAccessLog y9LogAccessLog = Y9ModelConvertUtil.convert(y9LogAccessLogDO, Y9LogAccessLog.class);
        y9logAccessLogRepository.save(y9LogAccessLog);
    }

    @Override
    public Page<Y9LogAccessLogDO> searchQuery(String tenantId, String managerLevel, AccessLogQuery loginInfoModel,
        Integer page, Integer rows) {
        Pageable pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);

        Page<Y9LogAccessLog> y9LogAccessLogPage = y9logAccessLogRepository.findAll(new Specification<>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9LogAccessLog> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();

                if (StringUtils.isNotBlank(tenantId)) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.TENANT_ID).as(String.class), tenantId));
                }
                if (StringUtils.isNotEmpty(managerLevel)) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.MANAGER_LEVEL).as(String.class),
                        managerLevel));
                }
                if (StringUtils.isNotBlank(loginInfoModel.getUserName())) {
                    list.add(criteriaBuilder.like(root.get(Y9LogSearchConsts.USER_NAME).as(String.class),
                        "%" + loginInfoModel.getUserName() + "%"));
                }
                if (StringUtils.isNotBlank(loginInfoModel.getUserHostIp())) {
                    list.add(criteriaBuilder.like(root.get(Y9LogSearchConsts.USER_HOST_IP).as(String.class),
                        "%" + loginInfoModel.getUserHostIp() + "%"));
                }
                if (StringUtils.isNotBlank(loginInfoModel.getSystemName())) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.SYSTEM_NAME).as(String.class),
                        loginInfoModel.getSystemName()));
                }
                if (StringUtils.isNotBlank(loginInfoModel.getModularName())) {
                    list.add(criteriaBuilder.like(root.get(Y9LogSearchConsts.MODULAR_NAME).as(String.class),
                        "%" + loginInfoModel.getModularName() + "%"));
                }
                if (StringUtils.isNotBlank(loginInfoModel.getOperateName())) {

                    list.add(criteriaBuilder.like(root.get(Y9LogSearchConsts.OPERATE_NAME).as(String.class),
                        "%" + loginInfoModel.getOperateName() + "%"));
                }
                if (StringUtils.isNotBlank(loginInfoModel.getOperateType())) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.OPERATE_TYPE).as(String.class),
                        loginInfoModel.getOperateType()));
                }
                if (StringUtils.isNotBlank(loginInfoModel.getSuccess())) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.SUCCESS).as(String.class),
                        loginInfoModel.getSuccess()));
                }
                if (StringUtils.isNotBlank(loginInfoModel.getLogLevel())) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.LOG_LEVEL).as(String.class),
                        loginInfoModel.getLogLevel()));
                }
                if (StringUtils.isNotBlank(loginInfoModel.getStartTime())
                    && StringUtils.isNotBlank(loginInfoModel.getEndTime())) {
                    String sTime = loginInfoModel.getStartTime() + " 00:00:00";
                    String eTime = loginInfoModel.getEndTime() + " 23:59:59";
                    SimpleDateFormat sdfUtc = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    try {
                        Date startDate = DATE_TIME_FORMAT.parse(sTime);
                        Date endDate = DATE_TIME_FORMAT.parse(eTime);
                        Date start = sdfUtc.parse(sdfUtc.format(startDate));
                        Date end = sdfUtc.parse(sdfUtc.format(endDate));
                        list.add(
                            criteriaBuilder.between(root.get(Y9LogSearchConsts.LOG_TIME).as(Date.class), start, end));
                    } catch (ParseException e) {
                        LOGGER.warn(e.getMessage(), e);
                    }
                }

                // 如果没有条件，返回空查询
                if (list.isEmpty()) {
                    return criteriaBuilder.conjunction(); // 相当于 WHERE 1=1
                }
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        }, pageable);
        return poPageToDoPage(y9LogAccessLogPage);
    }
}
