package net.risesoft.y9public.repository.custom.impl;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.log.constant.Y9LogSearchConsts;
import net.risesoft.model.log.AccessLog;
import net.risesoft.model.log.LogInfoModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.util.AccessLogModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9Day;
import net.risesoft.y9public.entity.Y9logAccessLog;
import net.risesoft.y9public.repository.Y9logAccessLogRepository;
import net.risesoft.y9public.repository.custom.Y9logAccessLogCustomRepository;
import net.risesoft.y9public.repository.custom.Y9logMappingCustomRepository;

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
    private final Y9logAccessLogRepository y9logAccessLogRepository;

    private final JdbcTemplate jdbcTemplate4Public;

    public Y9logAccessLogCustomRepositoryImpl(Y9logMappingCustomRepository y9logMappingCustomRepository,
        Y9logAccessLogRepository y9logAccessLogRepository,
        @Qualifier("jdbcTemplate4Public") JdbcTemplate jdbcTemplate4Public) {
        this.y9logMappingCustomRepository = y9logMappingCustomRepository;
        this.y9logAccessLogRepository = y9logAccessLogRepository;
        this.jdbcTemplate4Public = jdbcTemplate4Public;
    }

    @Override
    public Map<String, Object> getAppClickCount(String tenantId, String guidPath, String startDay, String endDay)
        throws UnknownHostException {
        Map<String, Object> returnMap = new HashMap<>();
        List<String> strList = new ArrayList<>();
        List<String> longList = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append(
            "SELECT DISTINCT(l.METHOD_NAME) as appName,count(l.METHOD_NAME) as count FROM  Y9_LOG_ACCESS_LOG l WHERE ");
        sql.append("l.MODULAR_NAME ='" + Y9LogSearchConsts.APP_MODULARNAME + "' ");

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
                sDay = Y9Day.getStartOfDay(DATE_FORMAT.parse(startDay));
                eDay = Y9Day.getEndOfDay(DATE_FORMAT.parse(endDay));
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
            sql.append("AND l.LOG_TIME >= '" + sDay.getTime() + "' ");
            sql.append("AND l.LOG_TIME <= '" + eDay.getTime() + "' ");
        }
        sql.append("GROUP BY l.METHOD_NAME");

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
                sDay = Y9Day.getStartOfDay(DATE_FORMAT.parse(startDay));
                eDay = Y9Day.getEndOfDay(DATE_FORMAT.parse(endDay));
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
    public Map<String, Object> getOperateStatusCount(String selectedDate, Integer tenantType) {
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
            Date startOfTime = Y9Day.getStartOfDay(day);
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
                if (!tenantType.equals(1)) {
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

    public long getOperateTimeCount(Date startDay, Date endDay, Integer tenantType, boolean betweenAble,
        long elapsedTimeStart, Long elapsedTimeEnd) {
        return y9logAccessLogRepository.count(new Specification<Y9logAccessLog>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9logAccessLog> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> list = predicate.getExpressions();
                list.add(criteriaBuilder.isNotNull(root.get(Y9LogSearchConsts.USER_NAME).as(String.class)));

                if (!tenantType.equals(1)) {
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
    public List<String> listAccessLog(String tenantId, String loginName, String startTime, String endTime) {
        List<String> strList = new ArrayList<>();
        List<Y9logAccessLog> searchList = y9logAccessLogRepository.findAll(new Specification<Y9logAccessLog>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9logAccessLog> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> list = predicate.getExpressions();
                list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.USER_NAME).as(String.class), loginName));
                if (StringUtils.isNotBlank(tenantId)) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.TENANT_ID).as(String.class), tenantId));
                }
                Date startDate = null;
                Date endDate = null;
                try {
                    startDate = DATE_TIME_FORMAT.parse(startTime);
                    endDate = DATE_TIME_FORMAT.parse(endTime);
                    list.add(criteriaBuilder.between(root.get(Y9LogSearchConsts.LOG_TIME).as(Date.class), startDate,
                        endDate));
                } catch (ParseException e1) {
                    LOGGER.warn(e1.getMessage(), e1);
                }

                return predicate;
            }
        });

        searchList.stream().forEach(team -> {
            strList.add(Y9JsonUtil.writeValueAsString(team));
        });
        return strList;
    }

    @Override
    public List<Long> listOperateTimeCount(String startDay, String endDay, Integer tenantType) {
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
                list.add(this.getOperateTimeCount(sDay, eDay, tenantType, true, longArray[i], longArray[i + 1]));
            } else {
                list.add(this.getOperateTimeCount(sDay, eDay, tenantType, false, longArray[i], null));
            }
        }
        return list;
    }

    @Override
    public Page<Y9logAccessLog> page(int page, int rows, String sort) {
        Pageable pageable;
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (StringUtils.isNoneBlank(sort)) {
            pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, sort);
        } else {
            pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        }

        return y9logAccessLogRepository.findAll(new Specification<Y9logAccessLog>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9logAccessLog> root, CriteriaQuery<?> query,
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
    public Y9Page<AccessLog> pageByCondition(LogInfoModel searchDto, String startTime, String endTime, Integer page,
        Integer rows) {

        Pageable pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.Direction.DESC, Y9LogSearchConsts.LOG_TIME);

        Page<Y9logAccessLog> pageInfo = y9logAccessLogRepository.findAll(new Specification<Y9logAccessLog>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9logAccessLog> root, CriteriaQuery<?> query,
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
                if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
                    String sTime = startTime + " 00:00:00";
                    String eTime = endTime + " 23:59:59";
                    try {
                        Date startDate = DATE_TIME_FORMAT.parse(sTime);
                        Date endDate = DATE_TIME_FORMAT.parse(eTime);
                        list.add(criteriaBuilder.between(root.get(Y9LogSearchConsts.LOG_TIME).as(Date.class), startDate,
                            endDate));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }

                return predicate;
            }
        }, pageable);

        return Y9Page.success(page, pageInfo.getTotalPages(), pageInfo.getTotalElements(),
            AccessLogModelConvertUtil.logEsListToModels(pageInfo.getContent()));
    }

    @Override
    public Y9Page<AccessLog> pageByOperateType(String operateType, Integer page, Integer rows) {
        Pageable pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.Direction.DESC, Y9LogSearchConsts.LOG_TIME);

        Page<Y9logAccessLog> pageInfo = y9logAccessLogRepository.findByOperateType(operateType, pageable);

        return Y9Page.success(page, pageInfo.getTotalPages(), pageInfo.getTotalElements(),
            AccessLogModelConvertUtil.logEsListToModels(pageInfo.getContent()));
    }

    @Override
    public Y9Page<AccessLog> pageByOrgType(String tenantId, List<String> personIds, String operateType, Integer page,
        Integer rows) {

        PageRequest pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        Page<Y9logAccessLog> pageInfo = y9logAccessLogRepository.findAll(new Specification<Y9logAccessLog>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9logAccessLog> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> list = predicate.getExpressions();
                if (StringUtils.isNotBlank(operateType)) {
                    list.add(
                        criteriaBuilder.equal(root.get(Y9LogSearchConsts.OPERATE_TYPE).as(String.class), operateType));
                }
                if (CollectionUtils.isNotEmpty(personIds)) {
                    // in条件拼接
                    In<String> in = criteriaBuilder.in(root.get(Y9LogSearchConsts.USER_ID).as(String.class));
                    for (String id : personIds) {
                        in.value(id);
                    }
                    list.add(in);
                }

                return predicate;
            }
        }, pageable);

        return Y9Page.success(page, pageInfo.getTotalPages(), pageInfo.getTotalElements(),
            AccessLogModelConvertUtil.logEsListToModels(pageInfo.getContent()));

    }

    @Override
    public Page<Y9logAccessLog> pageByTenantIdAndManagerLevelAndUserId(String tenantId, String managerLevel,
        String userId, Integer page, Integer rows, String sort) {
        Pageable pageable;

        if (StringUtils.isNoneBlank(sort)) {
            pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, sort);
        } else {
            pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        }

        return y9logAccessLogRepository.findAll(new Specification<Y9logAccessLog>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9logAccessLog> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> list = predicate.getExpressions();
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
                return predicate;
            }
        }, pageable);
    }

    @Override
    public Page<Y9logAccessLog> pageElapsedTimeByCondition(LogInfoModel searchDto, String startDay, String endDay,
        String sTime, String lTime, Integer tenantType, Integer page, Integer rows) throws ParseException {
        String tenantId = Y9LoginUserHolder.getTenantId();

        PageRequest pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        return y9logAccessLogRepository.findAll(new Specification<Y9logAccessLog>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9logAccessLog> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> list = predicate.getExpressions();
                if (!tenantType.equals(1)) {
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
    public Page<Y9logAccessLog> pageOperateStatusByOperateStatus(LogInfoModel searchDto, String operateStatus,
        String date, String hour, Integer tenantType, Integer page, Integer rows) throws ParseException {
        String tenantId = Y9LoginUserHolder.getTenantId();

        PageRequest pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        return y9logAccessLogRepository.findAll(new Specification<Y9logAccessLog>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9logAccessLog> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> list = predicate.getExpressions();

                list.add(criteriaBuilder.isNotNull(root.get(Y9LogSearchConsts.USER_NAME).as(String.class)));

                if (StringUtils.isNotBlank(operateStatus)) {
                    list.add(
                        criteriaBuilder.equal(root.get(Y9LogSearchConsts.SUCCESS).as(String.class), operateStatus));
                }

                if (!tenantType.equals(1)) {
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
    public Page<Y9logAccessLog> pageSearchByCondition(LogInfoModel loginInfoModel, String startTime, String endTime,
        Integer tenantType, Integer page, Integer rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        PageRequest pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        return y9logAccessLogRepository.findAll(new Specification<Y9logAccessLog>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9logAccessLog> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> list = predicate.getExpressions();
                if (!tenantType.equals(1)) {
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
                if (StringUtils.isNotBlank(loginInfoModel.getTenantName())) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.TENANT_NAME).as(String.class),
                        loginInfoModel.getTenantName()));
                }
                if (StringUtils.isNotBlank(loginInfoModel.getModularName())) {
                    list.add(criteriaBuilder.like(root.get(Y9LogSearchConsts.MODULAR_NAME).as(String.class),
                        "%" + loginInfoModel.getModularName() + "%"));
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
    public void save(Y9logAccessLog y9logAccessLog) {
        y9logAccessLogRepository.save(y9logAccessLog);
    }

    @Override
    public Page<Y9logAccessLog> searchQuery(String tenantId, String managerLevel, LogInfoModel loginInfoModel,
        Integer page, Integer rows) {
        Pageable pageable = null;
        if (StringUtils.isNotBlank(loginInfoModel.getSortName())) {
            pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, loginInfoModel.getSortName());
        } else {
            pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        }

        return y9logAccessLogRepository.findAll(new Specification<Y9logAccessLog>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9logAccessLog> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> list = predicate.getExpressions();

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

                return predicate;
            }
        }, pageable);

    }
}
