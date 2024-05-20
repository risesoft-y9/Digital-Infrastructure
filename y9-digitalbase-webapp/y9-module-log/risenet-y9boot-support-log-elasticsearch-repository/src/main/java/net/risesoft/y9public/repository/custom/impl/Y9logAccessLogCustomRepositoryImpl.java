package net.risesoft.y9public.repository.custom.impl;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.log.constant.Y9ESIndexConst;
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

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationRange;
import co.elastic.clients.elasticsearch._types.aggregations.CalendarInterval;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery.Builder;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.util.NamedValue;

/**
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class Y9logAccessLogCustomRepositoryImpl implements Y9logAccessLogCustomRepository {

    private static final FastDateFormat DATETIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    private final Y9logMappingCustomRepository y9logMappingService;
    private final ElasticsearchClient elasticsearchClient;
    private final ElasticsearchTemplate elasticsearchTemplate;
    private final Y9logAccessLogRepository y9logAccessLogRepository;

    // 目前日志查询页有两种情况：一种是有开始时间和结束时间，另一种是只选一个时间
    private String[] createIndexNames(String startDate, String endDate) {
        List<String> indexNameList = new ArrayList<>();
        if (StringUtils.isBlank(startDate) && StringUtils.isBlank(endDate)) {
            indexNameList.add(getCurrentYearIndexName());

        } else if (null != startDate && StringUtils.isBlank(endDate)) {
            String yearString = startDate.split("-")[0];
            indexNameList.add(Y9ESIndexConst.ACCESS_LOG_INDEX + "-" + yearString);

        } else {
            int minYearInt = Integer.parseInt(startDate.split("-")[0]);
            int maxYearInt = Integer.parseInt(endDate.split("-")[0]);
            while (minYearInt <= maxYearInt) {
                indexNameList.add(Y9ESIndexConst.ACCESS_LOG_INDEX + "-" + minYearInt);
                minYearInt++;
            }
        }
        return indexNameList.toArray(new String[0]);
    }

    private String[] getAllLogindexName() {
        List<String> indexNameList = new ArrayList<>();
        try {
            elasticsearchClient.cat().indices().valueBody().forEach(indexRecord -> {
                String indexName = indexRecord.index();
                if (indexName.contains(Y9ESIndexConst.ACCESS_LOG_INDEX)) {
                    indexNameList.add(indexName);
                }
            });
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return indexNameList.toArray(new String[0]);
    }

    @Override
    public Map<String, Object> getAppClickCount(String tenantId, String guidPath, String startDay, String endDay)
        throws UnknownHostException {
        Builder build = new BoolQuery.Builder();
        Map<String, Object> returnMap = new HashMap<>();
        List<String> strList = new ArrayList<>();
        List<Long> longList = new ArrayList<>();

        build.must(
            m -> m.queryString(qs -> qs.fields(Y9LogSearchConsts.METHOD_NAME).query(Y9LogSearchConsts.APP_METHODNAME)));

        if (StringUtils.isNotBlank(tenantId)) {
            build.must(m -> m.queryString(qs -> qs.fields(Y9LogSearchConsts.TENANT_ID).query(tenantId)));
        }

        if (StringUtils.isNotBlank(guidPath)) {
            build.must(m -> m.queryString(qs -> qs.fields(Y9LogSearchConsts.GUID_PATH).query(guidPath + "*")));
        }

        if (StringUtils.isNotBlank(startDay) && StringUtils.isNotBlank(endDay)) {
            try {
                Date sDay = Y9Day.getStartOfDay(new SimpleDateFormat("yyyy-MM-dd").parse(startDay));
                Date eDay = Y9Day.getEndOfDay(new SimpleDateFormat("yyyy-MM-dd").parse(endDay));
                build.must(m -> m.range(r -> r.field(Y9LogSearchConsts.LOG_TIME).from(String.valueOf(sDay.getTime()))
                    .to(String.valueOf(eDay.getTime()))));
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }

        @SuppressWarnings("unchecked")
        SearchRequest request = SearchRequest.of(s -> s.index(Arrays.asList(createIndexNames(startDay, endDay)))
            .query(build.build()._toQuery()).aggregations("by_appName", a -> a.terms(t -> t
                .field(Y9LogSearchConsts.MODULAR_NAME).size(10000).order(new NamedValue<>("_count", SortOrder.Desc)))));

        try {
            List<StringTermsBucket> buckets = elasticsearchClient.search(request, Y9logAccessLog.class).aggregations()
                .get("by_appName").sterms().buckets().array();
            int length = buckets.size();
            buckets.forEach(bucket -> {
                String appName = bucket.key().toString();
                long count = bucket.docCount();
                strList.add(appName);
                longList.add(count);
            });
            returnMap.put("number", length);
            returnMap.put("name", strList);
            returnMap.put("value", longList);
            return returnMap;
        } catch (ElasticsearchException | IOException e) {
            LOGGER.error(e.getMessage(), e);
            return Collections.emptyMap();
        }
    }

    private String getCurrentYearIndexName() {
        String yearStr = String.valueOf(LocalDate.now().getYear());
        return Y9ESIndexConst.ACCESS_LOG_INDEX + "-" + yearStr;
    }

    @Override
    public Map<String, Object> getModuleNameCount(String tenantId, String guidPath, String startDay, String endDay) {
        Map<String, Object> map = new HashMap<>();
        List<String> strList = new ArrayList<>();
        List<Long> longList = new ArrayList<>();

        Builder builder = new BoolQuery.Builder();
        builder.must(m -> m.exists(e -> e.field(Y9LogSearchConsts.USER_NAME)));

        if (StringUtils.isNotBlank(guidPath)) {
            builder.must(m -> m.queryString(qs -> qs.fields(Y9LogSearchConsts.GUID_PATH).query(guidPath + "*")));
        }

        if (StringUtils.isNotBlank(tenantId)) {
            builder.must(m -> m.queryString(qs -> qs.fields(Y9LogSearchConsts.TENANT_ID).query(tenantId)));
        }

        if (StringUtils.isNotBlank(startDay) && StringUtils.isNotBlank(endDay)) {
            try {
                Date sDay = Y9Day.getStartOfDay(new SimpleDateFormat("yyyy-MM-dd").parse(startDay));
                Date eDay = Y9Day.getEndOfDay(new SimpleDateFormat("yyyy-MM-dd").parse(endDay));
                builder.must(m -> m.range(r -> r.field(Y9LogSearchConsts.LOGIN_TIME)
                    .from(String.valueOf(sDay.getTime())).to(String.valueOf(eDay.getTime()))));
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }

        @SuppressWarnings("unchecked")
        SearchRequest request = SearchRequest.of(s -> s.index(Arrays.asList(createIndexNames(startDay, endDay)))
            .query(builder.build()._toQuery()).aggregations("by_modularname", a -> a.terms(t -> t
                .field(Y9LogSearchConsts.MODULAR_NAME).size(10000).order(new NamedValue<>("_count", SortOrder.Desc)))));

        try {
            elasticsearchClient.search(request, Y9logAccessLog.class).aggregations().get("by_modularname").sterms()
                .buckets().array().forEach(bucket -> {
                    String modularName = bucket.key().stringValue();
                    String modularCnName = y9logMappingService.getCnModularName(modularName);
                    if (StringUtils.isNotBlank(modularCnName)) {
                        modularName = modularCnName;
                    }
                    long count = bucket.docCount();
                    strList.add(modularName);
                    longList.add(count);
                });
            map.put("name", strList);
            map.put("value", longList);
            map.put("number", strList.size());
            return map;
        } catch (ElasticsearchException | IOException e1) {
            LOGGER.error(e1.getMessage(), e1);
            return null;
        }
    }

    @Override
    public Map<String, Object> getOperateStatusCount(String selectedDate, Integer tenantType) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String success = "成功";
        String error = "出错";
        List<Integer> time = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        List<Long> countOfSuccess = new ArrayList<>();
        List<Long> countOfError = new ArrayList<>();
        Date day = new Date();

        Builder sbuilder = new BoolQuery.Builder();
        Builder ebuilder = new BoolQuery.Builder();
        try {
            day = new SimpleDateFormat("yyyy-MM-dd").parse(selectedDate);
        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
        }
        Date startOfTime = Y9Day.getStartOfDay(day);
        Date endOfTime = Y9Day.getEndOfDay(day);
        sbuilder.must(m -> m.exists(e -> e.field(Y9LogSearchConsts.USER_NAME)));
        sbuilder.must(m -> m.queryString(qs -> qs.fields(Y9LogSearchConsts.SUCCESS).query(success)));
        sbuilder.must(m -> m.range(r -> r.field(Y9LogSearchConsts.LOG_TIME).from(String.valueOf(startOfTime.getTime()))
            .to(String.valueOf(endOfTime.getTime()))));

        ebuilder.must(m -> m.exists(e -> e.field(Y9LogSearchConsts.USER_NAME)));
        ebuilder.must(m -> m.queryString(qs -> qs.fields(Y9LogSearchConsts.SUCCESS).query(error)));
        ebuilder.must(m -> m.range(r -> r.field(Y9LogSearchConsts.LOG_TIME).from(String.valueOf(startOfTime.getTime()))
            .to(String.valueOf(endOfTime.getTime()))));

        if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
            sbuilder.must(m -> m.queryString(qs -> qs.fields(Y9LogSearchConsts.TENANT_ID).query(tenantId)));
            ebuilder.must(m -> m.queryString(qs -> qs.fields(Y9LogSearchConsts.TENANT_ID).query(tenantId)));
        }

        SearchRequest sRequest = SearchRequest.of(s -> s.index(Arrays.asList(createIndexNames(selectedDate, null)))
            .query(sbuilder.build()._toQuery()).aggregations("by_success_logtime", a -> a.dateHistogram(
                d -> d.field(Y9LogSearchConsts.LOG_TIME).calendarInterval(CalendarInterval.Hour).minDocCount(0))));
        SearchRequest eRequest = SearchRequest.of(s -> s.index(Arrays.asList(createIndexNames(selectedDate, null)))
            .query(sbuilder.build()._toQuery()).aggregations("by_error_logtime", a -> a.dateHistogram(
                d -> d.field(Y9LogSearchConsts.LOG_TIME).calendarInterval(CalendarInterval.Hour).minDocCount(0))));

        try {
            elasticsearchClient.search(sRequest, Void.class).aggregations().get("by_success_logtime").dateHistogram()
                .buckets().array().forEach(bucket -> {
                    long count = bucket.docCount();
                    countOfSuccess.add(count);
                });

        } catch (ElasticsearchException | IOException e1) {
            LOGGER.error(e1.getMessage(), e1);
        }

        try {
            elasticsearchClient.search(eRequest, Void.class).aggregations().get("by_error_logtime").dateHistogram()
                .buckets().array().forEach(bucket -> {
                    long count = bucket.docCount();
                    countOfError.add(count);
                });

        } catch (ElasticsearchException | IOException e1) {
            LOGGER.error(e1.getMessage(), e1);
        }
        for (int i = 0; i < 24; i++) {
            time.add(i);
        }
        map.put("time", time);
        map.put("totalOfSuccess", countOfSuccess);
        map.put("totalOfError", countOfError);
        return map;
    }

    @Override
    public List<String> listAccessLog(String startTime, String endTime, String loginName, String tenantId) {
        List<String> strList = new ArrayList<>();
        try {
            Date startDate = DATETIME_FORMAT.parse(startTime);
            Date endDate = DATETIME_FORMAT.parse(endTime);

            SearchRequest request =
                SearchRequest
                    .of(s -> s.index(Y9ESIndexConst.ACCESS_LOG_INDEX)
                        .query(q -> q.bool(b -> b
                            .must(m -> m.queryString(qs -> qs.fields(Y9LogSearchConsts.USER_NAME).query(loginName)))
                            .must(m -> m.queryString(qs -> qs.fields(Y9LogSearchConsts.TENANT_ID).query(tenantId)))
                            .must(m -> m.range(r -> r.field(Y9LogSearchConsts.LOGIN_TIME)
                                .from(String.valueOf(startDate.getTime())).to(String.valueOf(endDate.getTime())))))));

            try {
                List<Hit<Y9logAccessLog>> hits =
                    elasticsearchClient.search(request, Y9logAccessLog.class).hits().hits();
                hits.forEach(hit -> {
                    String accesslogjson = Y9JsonUtil.writeValueAsString(hit.source());
                    strList.add(accesslogjson);
                });
            } catch (ElasticsearchException | IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        } catch (ParseException e1) {
            LOGGER.warn(e1.getMessage(), e1);
        }
        return strList;
    }

    @Override
    public List<Long> listOperateTimeCount(String startDay, String endDay, Integer tenantType) {
        List<Long> list = new ArrayList<>();
        Builder builder = new BoolQuery.Builder();

        builder.must(m -> m.exists(e -> e.field(Y9LogSearchConsts.USER_NAME)));
        if (StringUtils.isNotBlank(startDay) && StringUtils.isNotBlank(endDay)) {
            builder.must(
                m -> m.range(r -> r.field(Y9LogSearchConsts.LOG_TIME).from(startDay).to(endDay).format("yyyy-MM-dd")));
        }

        List<AggregationRange> aggregationRanges = new ArrayList<>();
        aggregationRanges.add(AggregationRange.of(ar -> ar.from("0").to("1000000")));
        aggregationRanges.add(AggregationRange.of(ar -> ar.from("1000000").to("10000000")));
        aggregationRanges.add(AggregationRange.of(ar -> ar.from("10000000").to("100000000")));
        aggregationRanges.add(AggregationRange.of(ar -> ar.from("100000000").to("500000000")));
        aggregationRanges.add(AggregationRange.of(ar -> ar.from("500000000").to("1000000000")));
        aggregationRanges.add(AggregationRange.of(ar -> ar.from("1000000000")));

        SearchRequest request = SearchRequest.of(s -> s.index(Arrays.asList(createIndexNames(startDay, endDay)))
            .query(q -> q.bool(builder.build())).aggregations("range-elapsedtime",
                a -> a.range(r -> r.field(Y9LogSearchConsts.ELAPSED_TIME).ranges(aggregationRanges))));

        try {
            elasticsearchClient.search(request, Y9logAccessLog.class).aggregations().get("range-elapsedtime").range()
                .buckets().array().forEach(bucket -> {
                    long count = bucket.docCount();
                    list.add(count);
                });
            return list;
        } catch (ElasticsearchException | IOException e1) {
            LOGGER.error(e1.getMessage(), e1);
            return null;
        }
    }

    @Override
    public Page<Y9logAccessLog> page(int page, int rows, String sort) {
        IndexCoordinates index = IndexCoordinates.of(getCurrentYearIndexName());
        String tenantId = Y9LoginUserHolder.getTenantId();
        Pageable pageable = null;
        Criteria criteria = new Criteria();

        criteria.and(Y9LogSearchConsts.USER_NAME).exists();
        if (tenantId != null) {
            criteria.and(Y9LogSearchConsts.TENANT_ID).is(tenantId);
        }

        if (StringUtils.isNoneBlank(sort)) {
            pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, sort);
        } else {
            pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        }

        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);
        SearchHits<Y9logAccessLog> searchHits = elasticsearchTemplate.search(query, Y9logAccessLog.class, index);
        List<Y9logAccessLog> list = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        Page<Y9logAccessLog> pageResult = new PageImpl<>(list, pageable, searchHits.getTotalHits());
        return pageResult;
    }

    @Override
    public Y9Page<AccessLog> pageByCondition(LogInfoModel search, String startTime, String endTime, Integer page,
        Integer rows) {
        IndexCoordinates index = IndexCoordinates.of(createIndexNames(startTime, endTime));
        Criteria criteria = new Criteria();

        criteria.and(Y9LogSearchConsts.USER_NAME).exists();
        if (StringUtils.isNotBlank(search.getLogLevel())) {
            criteria.and(Y9LogSearchConsts.LOG_LEVEL).is(search.getLogLevel());
        }
        if (StringUtils.isNotBlank(search.getSuccess())) {
            criteria.and(Y9LogSearchConsts.SUCCESS).is(search.getSuccess());
        }
        if (StringUtils.isNotBlank(search.getOperateType())) {
            criteria.and(Y9LogSearchConsts.OPERATE_TYPE).is(search.getOperateType());
        }
        if (StringUtils.isNotBlank(search.getUserName())) {
            criteria.and(Y9LogSearchConsts.USER_NAME).is(search.getUserName());
        }
        if (StringUtils.isNotBlank(search.getUserHostIp())) {
            criteria.and(Y9LogSearchConsts.USER_HOST_IP).is(search.getUserHostIp());
        }
        if (StringUtils.isNotEmpty(search.getOperateName())) {
            criteria.and(Y9LogSearchConsts.OPERATE_NAME).is(search.getOperateName());
        }
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            startTime = startTime + " 00:00:00";
            endTime = endTime + " 23:59:59";
            long startDate = 0;
            try {
                startDate = DATETIME_FORMAT.parse(startTime).getTime();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            long endDate = 0;
            try {
                endDate = DATETIME_FORMAT.parse(endTime).getTime();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            criteria.and(Y9LogSearchConsts.LOG_TIME).between(startDate, endDate);
        }
        Pageable pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.Direction.DESC, Y9LogSearchConsts.LOG_TIME);

        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);

        SearchHits<Y9logAccessLog> searchHits = elasticsearchTemplate.search(query, Y9logAccessLog.class, index);
        List<Y9logAccessLog> list = searchHits.stream().map(s -> s.getContent()).collect(Collectors.toList());
        long total = searchHits.getTotalHits();
        int totalPages = (int)total / rows;
        return Y9Page.success(page, total % rows == 0 ? totalPages : totalPages + 1, total,
            AccessLogModelConvertUtil.logEsListToModels(list));
    }

    @Override
    public Y9Page<AccessLog> pageByOperateType(String operateType, Integer page, Integer rows) {
        IndexCoordinates index = IndexCoordinates.of(getCurrentYearIndexName());
        Criteria criteria = new Criteria();

        criteria.and(Y9LogSearchConsts.OPERATE_TYPE).is(operateType);
        Pageable pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.Direction.DESC, Y9LogSearchConsts.LOG_TIME);

        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);
        SearchHits<Y9logAccessLog> searchHits = elasticsearchTemplate.search(query, Y9logAccessLog.class, index);
        List<Y9logAccessLog> list = searchHits.stream()
            .map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        long total = searchHits.getTotalHits();
        int totalPages = (int)total / rows;
        return Y9Page.success(page, total % rows == 0 ? totalPages : totalPages + 1, total,
            AccessLogModelConvertUtil.logEsListToModels(list));
    }

    @Override
    public Y9Page<AccessLog> pageByOrgType(String tenantId, List<String> personIds, String operateType, Integer page,
        Integer rows) {
        IndexCoordinates index = IndexCoordinates.of(getCurrentYearIndexName());
        try {
            if (CollectionUtils.isNotEmpty(personIds)) {
                CriteriaQuery criteriaQuery =
                    new CriteriaQuery(new Criteria().and(new Criteria(Y9LogSearchConsts.OPERATE_TYPE).is(operateType))
                        .and(new Criteria(Y9LogSearchConsts.USER_ID).in(personIds)))
                            .setPageable(PageRequest.of((page < 1) ? 0 : page - 1, rows))
                            .addSort(Sort.by(Order.desc(Y9LogSearchConsts.LOG_TIME)));

                SearchHits<Y9logAccessLog> searchHits =
                    elasticsearchTemplate.search(criteriaQuery, Y9logAccessLog.class, index);
                List<Y9logAccessLog> list =
                    searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent)
                        .collect(Collectors.toList());

                long total = searchHits.getTotalHits();
                int totalPages = (int)total / rows;
                return Y9Page.success(page, total % rows == 0 ? totalPages : totalPages + 1, total,
                    AccessLogModelConvertUtil.logEsListToModels(list));
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Page<Y9logAccessLog> pageByTenantIdAndManagerLevelAndUserId(String tenantId, String managerLevel,
        String userId, Integer page, Integer rows, String sort) {
        IndexCoordinates index = IndexCoordinates.of(getCurrentYearIndexName());
        Pageable pageable = null;
        Criteria criteria = new Criteria();

        criteria.and(Y9LogSearchConsts.USER_NAME).exists();
        if (tenantId != null) {
            criteria.and(Y9LogSearchConsts.TENANT_ID).is(tenantId);
        }
        if (StringUtils.isNotEmpty(userId)) {
            criteria.and(Y9LogSearchConsts.USER_ID).is(userId);
        }
        if (StringUtils.isNotEmpty(managerLevel)) {
            criteria.and(Y9LogSearchConsts.MANAGER_LEVEL).is(managerLevel);
        }
        if (StringUtils.isNoneBlank(sort)) {
            pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, sort);
        } else {
            pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        }

        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);

        SearchHits<Y9logAccessLog> searchHits = elasticsearchTemplate.search(query, Y9logAccessLog.class, index);
        List<Y9logAccessLog> list = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        PageImpl<Y9logAccessLog> pageImpl = new PageImpl<>(list, pageable, searchHits.getTotalHits());
        return pageImpl;
    }

    @Override
    public Page<Y9logAccessLog> pageElapsedTimeByCondition(LogInfoModel search, String startDay, String endDay,
        String startTime, String endTime, Integer tenantType, Integer page, Integer rows) throws ParseException {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Criteria criteria = new Criteria();

        criteria.and(Y9LogSearchConsts.USER_NAME).exists();
        if (StringUtils.isNotBlank(startDay) && StringUtils.isNotBlank(endDay)) {
            Date sDay = Y9Day.getStartOfDay(new SimpleDateFormat("yyyy-MM-dd").parse(startDay));
            Date eDay = Y9Day.getEndOfDay(new SimpleDateFormat("yyyy-MM-dd").parse(endDay));
            criteria.and(Y9LogSearchConsts.LOG_TIME).between(sDay.getTime(), eDay.getTime());
        }
        if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
            criteria.and(Y9LogSearchConsts.TENANT_ID).is(tenantId);
        }
        if (StringUtils.isNotBlank(search.getLogLevel())) {
            criteria.and(Y9LogSearchConsts.LOG_LEVEL).is(search.getLogLevel());
        }
        if (StringUtils.isNotBlank(search.getSuccess())) {
            criteria.and(Y9LogSearchConsts.SUCCESS).is(search.getSuccess());
        }
        if (StringUtils.isNotBlank(search.getOperateType())) {
            criteria.and(Y9LogSearchConsts.OPERATE_TYPE).is(search.getOperateType());
        }
        if (StringUtils.isNotBlank(startTime)) {
            long smallTime = Long.parseLong(startTime);
            if (StringUtils.isNotBlank(endTime)) {
                long largeTime = Long.parseLong(endTime);
                criteria.and(Y9LogSearchConsts.ELAPSED_TIME).between(smallTime, largeTime);
            } else {
                criteria.and(Y9LogSearchConsts.ELAPSED_TIME).greaterThan(smallTime);
            }
        }

        PageRequest pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);
        IndexCoordinates index = IndexCoordinates.of(createIndexNames(startDay, endDay));

        SearchHits<Y9logAccessLog> searchHits = elasticsearchTemplate.search(query, Y9logAccessLog.class, index);
        List<Y9logAccessLog> list = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        PageImpl<Y9logAccessLog> pageImpl = new PageImpl<>(list, pageable, searchHits.getTotalHits());
        return pageImpl;
    }

    @Override
    public Page<Y9logAccessLog> pageOperateStatusByOperateStatus(LogInfoModel search, String operateStatus, String date,
        String hour, Integer tenantType, Integer page, Integer rows) throws ParseException {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Criteria criteria = new Criteria();
        criteria.and(Y9LogSearchConsts.USER_NAME).exists();

        if (StringUtils.isNotBlank(operateStatus)) {
            criteria.and(Y9LogSearchConsts.SUCCESS).is(operateStatus);
        }
        if (StringUtils.isNotBlank(date) && StringUtils.isNotBlank(hour)) {
            int h = Integer.parseInt(hour);
            Calendar cal = Calendar.getInstance();
            Date day = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            Date dat = Y9Day.getStartOfDay(day);
            cal.setTime(dat);
            cal.add(Calendar.HOUR_OF_DAY, h);
            Date startOfTime = cal.getTime();
            cal.add(Calendar.MINUTE, 59);
            cal.add(Calendar.SECOND, 59);
            Date endOfTime = cal.getTime();
            criteria.and(Y9LogSearchConsts.LOG_TIME).between(startOfTime.getTime(), endOfTime.getTime());
        }
        if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
            criteria.and(Y9LogSearchConsts.TENANT_ID).is(tenantId);
        }
        if (StringUtils.isNotBlank(search.getUserName())) {
            criteria.and(Y9LogSearchConsts.USER_NAME).is(search.getUserName());
        }
        if (StringUtils.isNotBlank(search.getTenantName())) {
            criteria.and(Y9LogSearchConsts.TENANT_NAME).is(search.getTenantName());
        }
        if (StringUtils.isNotBlank(search.getLogLevel())) {
            criteria.and(Y9LogSearchConsts.LOG_LEVEL).is(search.getLogLevel());
        }
        if (StringUtils.isNotBlank(search.getOperateType())) {
            criteria.and(Y9LogSearchConsts.OPERATE_TYPE).is(search.getOperateType());
        }

        PageRequest pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);

        IndexCoordinates index = IndexCoordinates.of(createIndexNames(date, null));
        SearchHits<Y9logAccessLog> searchHits = elasticsearchTemplate.search(query, Y9logAccessLog.class, index);
        List<Y9logAccessLog> list = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        PageImpl<Y9logAccessLog> pageImpl = new PageImpl<>(list, pageable, searchHits.getTotalHits());
        return pageImpl;
    }

    @Override
    public Page<Y9logAccessLog> pageSearchByCondition(LogInfoModel search, String startTime, String endTime,
        Integer tenantType, Integer page, Integer rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Criteria criteria = new Criteria();

        if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
            criteria.and(Y9LogSearchConsts.TENANT_ID).is(tenantId);
        }
        if (StringUtils.isNotBlank(search.getLogLevel())) {
            criteria.and(Y9LogSearchConsts.LOG_LEVEL).is(search.getLogLevel());
        }
        if (StringUtils.isNotBlank(search.getSuccess())) {
            criteria.and(Y9LogSearchConsts.SUCCESS).is(search.getSuccess());
        }
        if (StringUtils.isNotBlank(search.getOperateType())) {
            criteria.and(Y9LogSearchConsts.OPERATE_TYPE).is(search.getOperateType());
        }
        if (StringUtils.isNotBlank(search.getUserName())) {
            criteria.and(Y9LogSearchConsts.USER_NAME).is(search.getUserName());
        }
        if (StringUtils.isNotBlank(search.getUserHostIp())) {
            criteria.and(Y9LogSearchConsts.USER_HOST_IP).is(search.getUserHostIp());
        }
        if (StringUtils.isNotBlank(search.getTenantName())) {
            criteria.and(Y9LogSearchConsts.TENANT_NAME).is(search.getTenantName());
        }
        if (StringUtils.isNotBlank(search.getModularName())) {
            criteria.or(Y9LogSearchConsts.MODULAR_NAME).contains(search.getModularName());
        }
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            startTime = startTime + " 00:00:00";
            endTime = endTime + " 23:59:59";
            try {
                long startDate = DATETIME_FORMAT.parse(startTime).getTime();
                long endDate = DATETIME_FORMAT.parse(endTime).getTime();
                criteria.and(Y9LogSearchConsts.LOG_TIME).between(startDate, endDate);
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        criteria.and(Y9LogSearchConsts.USER_NAME).exists();

        PageRequest pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);

        IndexCoordinates index = IndexCoordinates.of(createIndexNames(startTime, endTime));
        SearchHits<Y9logAccessLog> searchHits = elasticsearchTemplate.search(query, Y9logAccessLog.class, index);
        List<Y9logAccessLog> list = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        PageImpl<Y9logAccessLog> pageResult = new PageImpl<>(list, pageable, searchHits.getTotalHits());
        return pageResult;
    }

    @Override
    public void save(Y9logAccessLog y9logAccessLog) {
        IndexOperations indexOps = elasticsearchTemplate.indexOps(Y9logAccessLog.class);
        if (!indexOps.exists()) {
            synchronized (this) {
                if (!indexOps.exists()) {
                    indexOps.create();
                    indexOps.putMapping(indexOps.createMapping(Y9logAccessLog.class));
                }
            }
        }
        y9logAccessLogRepository.save(y9logAccessLog);
    }

    @Override
    public Page<Y9logAccessLog> searchQuery(String tenantId, String managerLevel, LogInfoModel loginInfoModel,
        Integer page, Integer rows) {
        Criteria criteria = new Criteria();
        Pageable pageable;

        if (StringUtils.isNotBlank(tenantId)) {
            criteria.and(Y9LogSearchConsts.TENANT_ID).is(tenantId);
        }
        if (StringUtils.isNotEmpty(managerLevel)) {
            criteria.and(Y9LogSearchConsts.MANAGER_LEVEL).is(managerLevel);
        }
        if (StringUtils.isNotBlank(loginInfoModel.getUserName())) {
            criteria.and(Y9LogSearchConsts.USER_NAME).contains(loginInfoModel.getUserName());
        }
        if (StringUtils.isNotBlank(loginInfoModel.getUserHostIp())) {
            criteria.and(Y9LogSearchConsts.USER_HOST_IP).contains(loginInfoModel.getUserHostIp());
        }
        if (StringUtils.isNotBlank(loginInfoModel.getModularName())) {
            criteria.and(Y9LogSearchConsts.MODULAR_NAME).contains(loginInfoModel.getModularName());
        }
        if (StringUtils.isNotBlank(loginInfoModel.getOperateName())) {
            criteria.and(Y9LogSearchConsts.OPERATE_NAME).contains(loginInfoModel.getOperateName());
        }
        if (StringUtils.isNotBlank(loginInfoModel.getOperateType())) {
            criteria.and(Y9LogSearchConsts.OPERATE_TYPE).contains(loginInfoModel.getOperateType());
        }
        if (StringUtils.isNotBlank(loginInfoModel.getSuccess())) {
            criteria.and(Y9LogSearchConsts.SUCCESS).is(loginInfoModel.getSuccess());
        }
        if (StringUtils.isNotBlank(loginInfoModel.getLogLevel())) {
            criteria.and(Y9LogSearchConsts.LOG_LEVEL).is(loginInfoModel.getLogLevel());
        }
        if (StringUtils.isNotBlank(loginInfoModel.getStartTime())
            && StringUtils.isNotBlank(loginInfoModel.getEndTime())) {
            String sTime = loginInfoModel.getStartTime() + " 00:00:00";
            String eTime = loginInfoModel.getEndTime() + " 23:59:59";
            SimpleDateFormat sdfUtc = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            try {
                Date startDate = DATETIME_FORMAT.parse(sTime);
                Date endDate = DATETIME_FORMAT.parse(eTime);
                Date start = sdfUtc.parse(sdfUtc.format(startDate));
                Date end = sdfUtc.parse(sdfUtc.format(endDate));
                String s = sdfUtc.format(start);
                String e = sdfUtc.format(end);

                criteria.and(Y9LogSearchConsts.LOG_TIME).between(s, e);
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }

        if (StringUtils.isNotBlank(loginInfoModel.getSortName())) {
            pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, loginInfoModel.getSortName());
        } else {
            pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        }

        Query criteriaQuery = new CriteriaQuery(criteria).setPageable(pageable);
        criteriaQuery.setTrackTotalHits(true);
        SearchHits<Y9logAccessLog> searchHits = elasticsearchTemplate.search(criteriaQuery, Y9logAccessLog.class);
        List<Y9logAccessLog> list = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        return new PageImpl<>(list, pageable, searchHits.getTotalHits());
    }

    /*
    // 目前日志查询页有两种情况：一种是有开始时间和结束时间，另一种是只选一个时间
    private String[] createIndexNames(String startDate, String endDate) {
        List<String> indexNameList = new ArrayList<>();
        if (StringUtils.isBlank(startDate) && StringUtils.isBlank(endDate)) {
            indexNameList.add(getCurrentYearIndexName());
    
        } else if (null != startDate && StringUtils.isBlank(endDate)) {
            String yearString = startDate.split("-")[0];
            indexNameList.add(Y9ESIndexConst.ACCESS_LOG_INDEX + "-" + yearString);
    
        } else {
            int minYearInt = Integer.parseInt(startDate.split("-")[0]);
            int maxYearInt = Integer.parseInt(endDate.split("-")[0]);
            while (minYearInt <= maxYearInt) {
                indexNameList.add(Y9ESIndexConst.ACCESS_LOG_INDEX + "-" + String.valueOf(minYearInt));
                minYearInt++;
            }
        }
        return indexNameList.toArray(new String[0]);
    }
    
    private String[] getAllLogindexName() {
        List<String> indexNameList = new ArrayList<>();
        try {
            GetAliasesRequest request = new GetAliasesRequest();
            GetAliasesResponse aliasesResponse =
                elasticsearchClient.indices().getAlias(request, RequestOptions.DEFAULT);
            Map<String, Set<AliasMetadata>> map = aliasesResponse.getAliases();
            for (String key : map.keySet()) {
                if (key.contains(Y9ESIndexConst.ACCESS_LOG_INDEX)) {
                    indexNameList.add(key);
                }
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return indexNameList.toArray(new String[0]);
    }
    
    @Override
    public Map<String, Object> getAppClickCount(String orgId, String orgType, String tenantId, String startDay,
        String endDay) throws UnknownHostException {
        String guidPath = getGuidPath(orgId, orgType, tenantId);
        Map<String, Object> returnMap = new HashMap<>();
        List<String> strList = new ArrayList<>();
        List<Long> longList = new ArrayList<>();
    
        SearchResponse response = null;
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.queryStringQuery("net.risesoft.controller.admin.WebsiteController.saveAppCheckCount")
            .field(Y9LogSearchConsts.MODULAR_NAME));
        if (StringUtils.isNotBlank(tenantId)) {
            query.must(QueryBuilders.queryStringQuery(tenantId).field(Y9LogSearchConsts.TENANT_ID));
        }
        if (StringUtils.isNotBlank(guidPath)) {
            query.must(QueryBuilders.queryStringQuery(guidPath + "*").field(Y9LogSearchConsts.GUID_PATH));
        }
    
        if (StringUtils.isNotBlank(startDay) && StringUtils.isNotBlank(endDay)) {
            Date sDay = new Date();
            Date eDay = new Date();
            try {
                sDay = Y9Day.getStartOfDay(new SimpleDateFormat("yyyy-MM-dd").parse(startDay));
                eDay = Y9Day.getEndOfDay(new SimpleDateFormat("yyyy-MM-dd").parse(endDay));
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
            query.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOG_TIME).from(sDay.getTime()).to(eDay.getTime()));
        }
    
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(query)
            .aggregation(AggregationBuilders.terms("by_appName").size(10000).field(Y9LogSearchConsts.METHOD_NAME));
        SearchRequest searchRequest = new SearchRequest(createIndexNames(startDay, endDay));
        searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
        searchRequest.source(searchSourceBuilder);
        try {
            response = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    
        Terms terms = response.getAggregations().get("by_appName");
        int length = terms.getBuckets().size();
        for (int i = length - 1; i >= 0; i--) {
            String appName = terms.getBuckets().get(i).getKey().toString();
            strList.add(appName);
            longList.add(terms.getBuckets().get(i).getDocCount());
        }
        returnMap.put("number", length);
        returnMap.put("name", strList);
        returnMap.put("value", longList);
        return returnMap;
    }
    
    public long getCountByQueryCondition(BoolQueryBuilder query, String startDay, String endDay) {
        String tenantId = Y9LoginUserHolder.getTenantId();
    
        if (!getTenantType(tenantId).equals(1)) {
            query.must(QueryBuilders.queryStringQuery(tenantId).field(Y9LogSearchConsts.TENANT_ID));
        }
    
        SearchRequest searchRequest = new SearchRequest(createIndexNames(startDay, endDay));
        searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(query);
        searchRequest.source(searchSourceBuilder);
        long count = 0L;
        try {
            count = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT).getHits().getHits().length;
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    
        return count;
    }
    
    public String getCurrentYearIndexName() {
        String yearStr = String.valueOf(new DateTime().getYear());
        return Y9ESIndexConst.ACCESS_LOG_INDEX + "-" + yearStr;
    }
    
    public String getGuidPath(String orgId, String orgType, String tenantId) {
        String guidPath = null;
        if (StringUtils.isNotBlank(orgId) && StringUtils.isNotBlank(orgType)) {
            if (orgType.equals(OrgTypeEnum.ORGANIZATION.getEnName())) {
                guidPath = organizationManager.get(tenantId, orgId).getData().getGuidPath();
            } else if (orgType.equals(OrgTypeEnum.DEPARTMENT.getEnName())) {
                // orgDepartmentService.getDNById(orgId);
                guidPath = departmentManager.get(tenantId, orgId).getData().getGuidPath();
            } else if (orgType.equals(OrgTypeEnum.GROUP.getEnName())) {
                // orgGroupService.getDNById(orgId);
                guidPath = groupManager.get(tenantId, orgId).getData().getGuidPath();
            } else if (orgType.equals(OrgTypeEnum.POSITION.getEnName())) {
                // orgPositionService.getDNById(orgId);
                guidPath = positionManager.get(tenantId, orgId).getData().getGuidPath();
            } else if (orgType.equals(OrgTypeEnum.PERSON.getEnName())) {
                // orgPersonService.getDNById(orgId);
                guidPath = personManager.get(tenantId, orgId).getData().getGuidPath();
            }
        }
        return guidPath;
    }
    
    @Override
    public Map<String, Object> getModuleNameCount(String orgId, String orgType, String tenantId, String startDay,
        String endDay) {
        String guidPath = getGuidPath(orgId, orgType, tenantId);
        Map<String, Object> map = new HashMap<>();
        List<String> strList = new ArrayList<>();
        List<Long> longList = new ArrayList<>();
        SearchResponse response = null;
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.existsQuery(Y9LogSearchConsts.USER_NAME));
    
        if (StringUtils.isNotBlank(guidPath)) {
            // guidPath = Y9Util.escape(guidPath);
            query.must(QueryBuilders.queryStringQuery(guidPath + "*").field(Y9LogSearchConsts.GUID_PATH));
        }
        if (StringUtils.isNotBlank(tenantId)) {
            query.must(QueryBuilders.queryStringQuery(tenantId).field(Y9LogSearchConsts.TENANT_ID));
        }
    
        if (StringUtils.isNotBlank(startDay) && StringUtils.isNotBlank(endDay)) {
            Date sDay = new Date();
            Date eDay = new Date();
            try {
                sDay = Y9Day.getStartOfDay(new SimpleDateFormat("yyyy-MM-dd").parse(startDay));
                Y9Day.getEndOfDay(new SimpleDateFormat("yyyy-MM-dd").parse(endDay));
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
            query.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOG_TIME).from(sDay.getTime()).to(eDay.getTime()));
        }
    
        SearchRequest request = new SearchRequest(createIndexNames(startDay, endDay));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(query)
            .aggregation(AggregationBuilders.terms("by_modularname").size(1000).field(Y9LogSearchConsts.MODULAR_NAME));
        request.source(searchSourceBuilder);
        try {
            response = elasticsearchClient.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        Terms terms = response.getAggregations().get("by_modularname");
        for (int i = terms.getBuckets().size() - 1; i >= 0; i--) {
            String modularName = terms.getBuckets().get(i).getKey().toString();
            String modularCnName = y9logMappingService.getCnModularName(modularName);
            if (StringUtils.isNotBlank(modularCnName)) {
                modularName = modularCnName;
            }
            strList.add(modularName);
            longList.add(terms.getBuckets().get(i).getDocCount());
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
                day = new SimpleDateFormat("yyyy-MM-dd").parse(selectedDate);
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
    
                BoolQueryBuilder squery = QueryBuilders.boolQuery();
                BoolQueryBuilder equery = QueryBuilders.boolQuery();
    
                squery.must(QueryBuilders.queryStringQuery(success).field(Y9LogSearchConsts.SUCCESS));
                squery.must(QueryBuilders.existsQuery(Y9LogSearchConsts.USER_NAME));
                squery.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOG_TIME).from(startOfTime.getTime())
                    .to(endOfTime.getTime()));
                countOfSuccess.add(getCountByQueryCondition(squery, selectedDate, null));
    
                equery.must(QueryBuilders.queryStringQuery(error).field(Y9LogSearchConsts.SUCCESS));
                equery.must(QueryBuilders.existsQuery(Y9LogSearchConsts.USER_NAME));
                equery.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOG_TIME).from(startOfTime.getTime())
                    .to(endOfTime.getTime()));
                countOfError.add(getCountByQueryCondition(equery, selectedDate, null));
    
                time.add(startOfTime.getHours());
            }
        }
        map.put("time", time);
        map.put("totalOfSuccess", countOfSuccess);
        map.put("totalOfError", countOfError);
        return map;
    }
    
    private Page<Y9logAccessLog> getSearchList(QueryBuilder query, Integer page, Integer rows, String startDay,
        String endDay) {
        IndexCoordinates index = IndexCoordinates.of(createIndexNames(startDay, endDay));
    
        PageRequest pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(query).withPageable(pageable).build();
        searchQuery.setTrackTotalHits(true);
        SearchHits<Y9logAccessLog> searchHits =
            elasticsearchOperations.search(searchQuery, Y9logAccessLog.class, index);
    
        List<Y9logAccessLog> list = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        Page<Y9logAccessLog> pageResult = new PageImpl<>(list, pageable, searchHits.getTotalHits());
        return pageResult;
    }
    
    private List<String> getTenantIds() {
        List<Tenant> tenantList = tenantManager.listAllTenants().getData();
        return tenantList.stream().map(Tenant::getId).collect(Collectors.toList());
    }
    
    private Integer getTenantType(String tenantId) {
        Integer num = 0;
        Tenant tenant = tenantManager.getById(tenantId).getData();
        if (null != tenant) {
            num = tenant.getTenantType().getValue();
        }
        return num;
    }
    
    @Override
    public List<String> listAccessLog(String startTime, String endTime, String loginName, String tenantId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<String> strList = new ArrayList<>();
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = sdf.parse(startTime);
            endDate = sdf.parse(endTime);
        } catch (ParseException e1) {
            LOGGER.warn(e1.getMessage(), e1);
        }
    
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.queryStringQuery(loginName).field(Y9LogSearchConsts.USER_NAME));
        query
            .must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOG_TIME).from(startDate.getTime()).to(endDate.getTime()));
        if (StringUtils.isNotBlank(tenantId)) {
            query.must(QueryBuilders.queryStringQuery(tenantId).field(Y9LogSearchConsts.TENANT_ID));
        }
    
        SearchRequest searchRequest =
            new SearchRequest(createIndexNames(startTime, endTime)).searchType(SearchType.DFS_QUERY_THEN_FETCH);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(query);
        searchRequest.source(searchSourceBuilder);
        org.elasticsearch.search.SearchHit[] searchhits = null;
        try {
            searchhits = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT).getHits().getHits();
            for (int i = 0; i < searchhits.length; i++) {
                String accesslogjson = searchhits[i].getSourceAsString();
                strList.add(accesslogjson);
            }
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return strList;
    }
    
    @Override
    public List<Long> listOperateTimeCount(String startDay, String endDay) {
        Date sDay = null;
        Date eDay = null;
        List<Long> list = new ArrayList<>();
        long[] longArray = {1L, 1000000L, 10000000L, 100000000L, 1000000000L, 5000000000L, 10000000000L};
    
        if (StringUtils.isNotBlank(startDay)) {
            try {
                Date day = new SimpleDateFormat("yyyy-MM-dd").parse(startDay);
                sDay = Y9Day.getStartOfDay(day);
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        if (StringUtils.isNotBlank(endDay)) {
            try {
                Date day = new SimpleDateFormat("yyyy-MM-dd").parse(endDay);
                eDay = Y9Day.getEndOfDay(day);
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        for (int i = 0; i < longArray.length; i++) {
            BoolQueryBuilder query = QueryBuilders.boolQuery();
            query.must(QueryBuilders.existsQuery(Y9LogSearchConsts.USER_NAME));
            query.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOG_TIME).from(sDay.getTime()).to(eDay.getTime()));
            if (i < longArray.length - 1) {
                query.must(
                    QueryBuilders.rangeQuery(Y9LogSearchConsts.ELAPSED_TIME).from(longArray[i]).to(longArray[i + 1]));
            } else {
                query.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.ELAPSED_TIME).gte(longArray[i]));
            }
            list.add(this.getCountByQueryCondition(query, startDay, endDay));
        }
        return list;
    }
    
    @Override
    public Page<Y9logAccessLog> page(int page, int rows, String sort) {
        IndexCoordinates index = IndexCoordinates.of(getCurrentYearIndexName());
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        Pageable pageable;
    
        String tenantId = Y9LoginUserHolder.getTenantId();
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.existsQuery(Y9LogSearchConsts.USER_NAME));
        if (tenantId != null) {
            query.must(QueryBuilders.queryStringQuery(tenantId).field(Y9LogSearchConsts.TENANT_ID));
        }
    
        if (StringUtils.isNoneBlank(sort)) {
            pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, sort);
        } else {
            pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        }
    
        NativeSearchQuery searchQuery = searchQueryBuilder.withQuery(query).withPageable(pageable).build();
        // https://www.elastic.co/guide/en/elasticsearch/reference/7.0/search-request-track-total-hits.html
        searchQuery.setTrackTotalHits(true);
        SearchHits<Y9logAccessLog> searchHits =
            elasticsearchOperations.search(searchQuery, Y9logAccessLog.class, index);
        List<Y9logAccessLog> list = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        Page<Y9logAccessLog> pageResult = new PageImpl<>(list, pageable, searchHits.getTotalHits());
        return pageResult;
    }
    
    @Override
    public Y9Page<AccessLog> pageByCondition(LogInfoModel searchDto, String startTime, String endTime, Integer page,
        Integer rows) {
        IndexCoordinates index = IndexCoordinates.of(createIndexNames(startTime, endTime));
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(searchDto.getLogLevel())) {
            query.must(QueryBuilders.queryStringQuery(searchDto.getLogLevel()).field(Y9LogSearchConsts.LOG_LEVEL));
        }
        if (StringUtils.isNotBlank(searchDto.getSuccess())) {
            query.must(QueryBuilders.queryStringQuery(searchDto.getSuccess()).field(Y9LogSearchConsts.SUCCESS));
        }
        if (StringUtils.isNotBlank(searchDto.getOperateType())) {
            query
                .must(QueryBuilders.queryStringQuery(searchDto.getOperateType()).field(Y9LogSearchConsts.OPERATE_TYPE));
        }
        if (StringUtils.isNotBlank(searchDto.getUserName())) {
            query.must(QueryBuilders.queryStringQuery(searchDto.getUserName()).field(Y9LogSearchConsts.USER_NAME));
        }
        if (StringUtils.isNotBlank(searchDto.getUserHostIp())) {
            query.must(QueryBuilders.queryStringQuery(searchDto.getUserHostIp()).field(Y9LogSearchConsts.USER_HOST_IP));
        }
        if (StringUtils.isNotEmpty(searchDto.getOperateName())) {
            query
                .must(QueryBuilders.queryStringQuery(searchDto.getOperateName()).field(Y9LogSearchConsts.OPERATE_NAME));
        }
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            startTime = startTime + " 00:00:00";
            endTime = endTime + " 23:59:59";
            SimpleDateFormat logDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long startDate = 0;
            try {
                startDate = logDate.parse(startTime).getTime();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            long endDate = 0;
            try {
                endDate = logDate.parse(endTime).getTime();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            query.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOG_TIME).from(startDate).to(endDate));
        }
        query.must(QueryBuilders.queryStringQuery("*").field(Y9LogSearchConsts.USER_NAME));
        Pageable pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.Direction.DESC, Y9LogSearchConsts.LOG_TIME);
    
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(query).withPageable(pageable).build();
        searchQuery.setTrackTotalHits(true);
        SearchHits<Y9logAccessLog> searchHits =
            elasticsearchOperations.search(searchQuery, Y9logAccessLog.class, index);
        List<Y9logAccessLog> list = searchHits.stream()
            .map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        long total = searchHits.getTotalHits();
        int totalPages = (int)total / rows;
        return Y9Page.success(page, total % rows == 0 ? totalPages : totalPages + 1, total,
            AccessLogModelConvertUtil.logEsListToModels(list));
    }
    
    @Override
    public Y9Page<AccessLog> pageByOperateType(String operateType, Integer page, Integer rows) {
        IndexCoordinates index = IndexCoordinates.of(getCurrentYearIndexName());
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.queryStringQuery(operateType).field(Y9LogSearchConsts.OPERATE_TYPE));
        Pageable pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.Direction.DESC, Y9LogSearchConsts.LOG_TIME);
    
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(query).withPageable(pageable).build();
        searchQuery.setTrackTotalHits(true);
        SearchHits<Y9logAccessLog> searchHits =
            elasticsearchOperations.search(searchQuery, Y9logAccessLog.class, index);
        List<Y9logAccessLog> list = searchHits.stream()
            .map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        long total = searchHits.getTotalHits();
        int totalPages = (int)total / rows;
        return Y9Page.success(page, total % rows == 0 ? totalPages : totalPages + 1, total,
            AccessLogModelConvertUtil.logEsListToModels(list));
    }
    
    @Override
    public Y9Page<AccessLog> pageByOrgType(String tenantId, String orgId, String orgType, String operateType,
        Integer page, Integer rows) {
        IndexCoordinates index = IndexCoordinates.of(getCurrentYearIndexName());
        List<String> ids = new ArrayList<>();
        List<Person> allPersons = new ArrayList<>();
        if (orgType.equals(OrgTypeEnum.DEPARTMENT.getEnName())) {
            allPersons = personManager.listRecursivelyByParentId(tenantId, orgId).getData();
        } else if (orgType.equals(OrgTypeEnum.GROUP.getEnName())) {
            allPersons = groupManager.listPersonsByGroupId(tenantId, orgId).getData();
        } else if (orgType.equals(OrgTypeEnum.POSITION.getEnName())) {
            allPersons = positionManager.listPersonsByPositionId(tenantId, orgId).getData();
        } else if (orgType.equals(OrgTypeEnum.PERSON.getEnName())) {
            allPersons.add(personManager.get(tenantId, orgId).getData());
        }
        for (Person p : allPersons) {
            ids.add(p.getId());
        }
        try {
            if (CollectionUtils.isNotEmpty(ids)) {
                CriteriaQuery criteriaQuery =
                    new CriteriaQuery(new Criteria().and(new Criteria(Y9LogSearchConsts.OPERATE_TYPE).is(operateType))
                        .and(new Criteria(Y9LogSearchConsts.USER_ID).in(ids)))
                        .setPageable(PageRequest.of((page < 1) ? 0 : page - 1, rows))
                        .addSort(Sort.by(Order.desc(Y9LogSearchConsts.LOG_TIME)));
    
                SearchHits<Y9logAccessLog> searchHits =
                    elasticsearchOperations.search(criteriaQuery, Y9logAccessLog.class, index);
                List<Y9logAccessLog> list =
                    searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent)
                        .collect(Collectors.toList());
    
                long total = searchHits.getTotalHits();
                int totalPages = (int)total / rows;
                return Y9Page.success(page, total % rows == 0 ? totalPages : totalPages + 1, total,
                    AccessLogModelConvertUtil.logEsListToModels(list));
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return null;
    }
    
    @Override
    public Page<Y9logAccessLog> pageByTenantIdAndManagerLevelAndUserId(String tenantId, String managerLevel,
        String userId, int page, int rows, String sort) {
        IndexCoordinates index = IndexCoordinates.of(getCurrentYearIndexName());
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        Pageable pageable;
    
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.existsQuery(Y9LogSearchConsts.USER_NAME));
        if (tenantId != null) {
            queryBuilder.must(QueryBuilders.queryStringQuery(tenantId).field(Y9LogSearchConsts.TENANT_ID));
        }
        if (StringUtils.isNotEmpty(userId)) {
            queryBuilder.must(QueryBuilders.queryStringQuery(userId).field(Y9LogSearchConsts.USER_ID));
        }
        if (StringUtils.isNotEmpty(managerLevel)) {
            queryBuilder.must(QueryBuilders.queryStringQuery(managerLevel).field(Y9LogSearchConsts.MANAGER_LEVEL));
        }
        if (StringUtils.isNoneBlank(sort)) {
            pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, sort);
        } else {
            pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        }
    
        NativeSearchQuery searchQuery = searchQueryBuilder.withQuery(queryBuilder).withPageable(pageable).build();
        // https://www.elastic.co/guide/en/elasticsearch/reference/7.0/search-request-track-total-hits.html
        searchQuery.setTrackTotalHits(true);
        SearchHits<Y9logAccessLog> searchHits =
            elasticsearchOperations.search(searchQuery, Y9logAccessLog.class, index);
        List<Y9logAccessLog> list = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        Page<Y9logAccessLog> pageResult = new PageImpl<>(list, pageable, searchHits.getTotalHits());
        return pageResult;
    }
    
    @Override
    public Page<Y9logAccessLog> pageElapsedTimeByCondition(LogInfoModel searchDto, String startDay, String endDay,
        String sTime, String lTime, int rows, int page) throws ParseException {
        String tenantId = Y9LoginUserHolder.getTenantId();
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.existsQuery(Y9LogSearchConsts.USER_NAME));
        if (StringUtils.isNotBlank(startDay) && StringUtils.isNotBlank(endDay)) {
            Date sDay = Y9Day.getStartOfDay(new SimpleDateFormat("yyyy-MM-dd").parse(startDay));
            Date eDay = Y9Day.getEndOfDay(new SimpleDateFormat("yyyy-MM-dd").parse(endDay));
            query.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOG_TIME).from(sDay.getTime()).to(eDay.getTime()));
        }
        if (!getTenantType(tenantId).equals(1)) {
            query.must(QueryBuilders.queryStringQuery(tenantId).field(Y9LogSearchConsts.TENANT_ID));
        }
        if (StringUtils.isNotBlank(searchDto.getLogLevel())) {
            query.must(QueryBuilders.queryStringQuery(searchDto.getLogLevel()).field(Y9LogSearchConsts.LOG_LEVEL));
        }
        if (StringUtils.isNotBlank(searchDto.getSuccess())) {
            query.must(QueryBuilders.queryStringQuery(searchDto.getSuccess()).field(Y9LogSearchConsts.SUCCESS));
        }
        if (StringUtils.isNotBlank(searchDto.getOperateType())) {
            query
                .must(QueryBuilders.queryStringQuery(searchDto.getOperateType()).field(Y9LogSearchConsts.OPERATE_TYPE));
        }
        if (StringUtils.isNotBlank(sTime)) {
            long smallTime = Long.parseLong(sTime);
            if (StringUtils.isNotBlank(lTime)) {
                long largeTime = Long.parseLong(lTime);
                query.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.ELAPSED_TIME).from(smallTime).to(largeTime));
            } else {
                query.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.ELAPSED_TIME).gt(smallTime));
            }
        }
        Page<Y9logAccessLog> esPage = getSearchList(query, page, rows, startDay, endDay);
        return esPage;
    }
    
    @Override
    public Page<Y9logAccessLog> pageOperateStatusByOperateStatus(LogInfoModel searchDto, String operateStatus,
        String date, String hour, Integer page, Integer rows) throws ParseException {
        String tenantId = Y9LoginUserHolder.getTenantId();
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.existsQuery(Y9LogSearchConsts.USER_NAME));
        if (StringUtils.isNotBlank(operateStatus)) {
            query.must(QueryBuilders.queryStringQuery(operateStatus).field(Y9LogSearchConsts.SUCCESS));
        }
        if (StringUtils.isNotBlank(date) && StringUtils.isNotBlank(hour)) {
            int h = Integer.parseInt(hour);
            Calendar cal = Calendar.getInstance();
            Date day = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            Date dat = Y9Day.getStartOfDay(day);
            cal.setTime(dat);
            cal.add(Calendar.HOUR_OF_DAY, h);
            Date startOfTime = cal.getTime();
            cal.add(Calendar.MINUTE, 59);
            cal.add(Calendar.SECOND, 59);
            Date endOfTime = cal.getTime();
            query.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOG_TIME).from(startOfTime.getTime())
                .to(endOfTime.getTime()));
        }
        if (!getTenantType(tenantId).equals(1)) {
            query.must(QueryBuilders.queryStringQuery(tenantId).field(Y9LogSearchConsts.TENANT_ID));
        }
        if (StringUtils.isNotBlank(searchDto.getUserName())) {
            query.must(QueryBuilders.queryStringQuery(searchDto.getUserName()).field(Y9LogSearchConsts.USER_NAME));
        }
        if (StringUtils.isNotBlank(searchDto.getTenantName())) {
            query.must(QueryBuilders.queryStringQuery(searchDto.getTenantName()).field(Y9LogSearchConsts.TENANT_NAME));
        }
        if (StringUtils.isNotBlank(searchDto.getLogLevel())) {
            query.must(QueryBuilders.queryStringQuery(searchDto.getLogLevel()).field(Y9LogSearchConsts.LOG_LEVEL));
        }
        if (StringUtils.isNotBlank(searchDto.getOperateType())) {
            query
                .must(QueryBuilders.queryStringQuery(searchDto.getOperateType()).field(Y9LogSearchConsts.OPERATE_TYPE));
        }
        return getSearchList(query, page, rows, date, null);
    }
    
    @Override
    public Page<Y9logAccessLog> pageSearchByCondition(LogInfoModel searchDto, String startTime, String endTime,
        Integer page, Integer rows) {
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (!getTenantType(tenantId).equals(1)) {
            query.must(QueryBuilders.queryStringQuery(tenantId).field(Y9LogSearchConsts.TENANT_ID));
        }
        if (StringUtils.isNotBlank(searchDto.getLogLevel())) {
            query.must(QueryBuilders.queryStringQuery(searchDto.getLogLevel()).field(Y9LogSearchConsts.LOG_LEVEL));
        }
        if (StringUtils.isNotBlank(searchDto.getSuccess())) {
            query.must(QueryBuilders.queryStringQuery(searchDto.getSuccess()).field(Y9LogSearchConsts.SUCCESS));
        }
        if (StringUtils.isNotBlank(searchDto.getOperateType())) {
            query
                .must(QueryBuilders.queryStringQuery(searchDto.getOperateType()).field(Y9LogSearchConsts.OPERATE_TYPE));
        }
        if (StringUtils.isNotBlank(searchDto.getUserName())) {
            query.must(QueryBuilders.queryStringQuery(searchDto.getUserName()).field(Y9LogSearchConsts.USER_NAME));
        }
        if (StringUtils.isNotBlank(searchDto.getUserHostIp())) {
            query.must(QueryBuilders.queryStringQuery(searchDto.getUserHostIp()).field(Y9LogSearchConsts.USER_HOST_IP));
        }
        if (StringUtils.isNotBlank(searchDto.getTenantName())) {
            query.must(QueryBuilders.queryStringQuery(searchDto.getTenantName()).field(Y9LogSearchConsts.TENANT_NAME));
        }
        if (StringUtils.isNotBlank(searchDto.getModularName())) {
            query.must(QueryBuilders.boolQuery().should(
                QueryBuilders.wildcardQuery(Y9LogSearchConsts.MODULAR_NAME, "*" + searchDto.getModularName() + "*")));
        }
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            startTime = startTime + " 00:00:00";
            endTime = endTime + " 23:59:59";
            SimpleDateFormat logDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long startDate = 0L;
            long endDate = 0L;
            try {
                startDate = logDate.parse(startTime).getTime();
                endDate = logDate.parse(endTime).getTime();
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
            query.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOG_TIME).from(startDate).to(endDate));
        }
        query.must(QueryBuilders.existsQuery(Y9LogSearchConsts.USER_NAME));
        Page<Y9logAccessLog> esPage = getSearchList(query, page, rows, startTime, endTime);
        return esPage;
    }
    
    @Override
    public void save(Y9logAccessLog y9logAccessLog) {
        y9logAccessLogRepository.save(y9logAccessLog);
    }
    
    @Override
    public Page<Y9logAccessLog> searchQuery(String tenantId, String managerLevel, LogInfoModel loginInfoModel,
        int page,
        int rows) {
    
    
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(tenantId)) {
            queryBuilder.must(QueryBuilders.queryStringQuery(tenantId).field(Y9LogSearchConsts.TENANT_ID));
        }
        if (StringUtils.isNotEmpty(managerLevel)) {
            queryBuilder.must(QueryBuilders.queryStringQuery(managerLevel).field(Y9LogSearchConsts.MANAGER_LEVEL));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getUserName())) {
            queryBuilder.must(QueryBuilders.boolQuery().should(
                QueryBuilders.wildcardQuery(Y9LogSearchConsts.USER_NAME, "*" + loginInfoModel.getUserName() + "*")));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getUserHostIp())) {
            queryBuilder.must(QueryBuilders.boolQuery().should(QueryBuilders
                .wildcardQuery(Y9LogSearchConsts.USER_HOST_IP, "*" + loginInfoModel.getUserHostIp() + "*")));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getModularName())) {
            queryBuilder.must(QueryBuilders.boolQuery().should(QueryBuilders
                .wildcardQuery(Y9LogSearchConsts.MODULAR_NAME, "*" + loginInfoModel.getModularName() + "*")));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getOperateName())) {
            queryBuilder.must(QueryBuilders.boolQuery().should(QueryBuilders
                .wildcardQuery(Y9LogSearchConsts.OPERATE_NAME, "*" + loginInfoModel.getOperateName() + "*")));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getOperateType())) {
            // queryBuilder.must(
            // QueryBuilders.queryStringQuery(loginInfoModel.getOperateType()).field(Y9LogSearchConsts.OPERATE_TYPE));
            queryBuilder.must(QueryBuilders.termQuery(Y9LogSearchConsts.OPERATE_TYPE, loginInfoModel.getOperateType()));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getSuccess())) {
            queryBuilder
                .must(QueryBuilders.queryStringQuery(loginInfoModel.getSuccess()).field(Y9LogSearchConsts.SUCCESS));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getLogLevel())) {
            queryBuilder
                .must(QueryBuilders.queryStringQuery(loginInfoModel.getLogLevel()).field(Y9LogSearchConsts.LOG_LEVEL));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getStartTime())
            && StringUtils.isNotBlank(loginInfoModel.getEndTime())) {
            String sTime = loginInfoModel.getStartTime() + " 00:00:00";
            String eTime = loginInfoModel.getEndTime() + " 23:59:59";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdfUtc = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            try {
                Date startDate = sdf.parse(sTime);
                Date endDate = sdf.parse(eTime);
                Date start = sdfUtc.parse(sdfUtc.format(startDate));
                Date end = sdfUtc.parse(sdfUtc.format(endDate));
                String s = sdfUtc.format(start);
                String e = sdfUtc.format(end);
                queryBuilder.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOG_TIME).from(s).to(e)
                    .format("yyyy-MM-dd'T'HH:mm:ss'Z'"));
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
    Pageable pageable = null;
        if (StringUtils.isNotBlank(loginInfoModel.getSortName())) {
            pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, loginInfoModel.getSortName());
        } else {
            pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        }
    IndexCoordinates index = IndexCoordinates.of(getCurrentYearIndexName());
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        NativeSearchQuery searchQuery = searchQueryBuilder.withQuery(queryBuilder).withPageable(pageable).build();
        // https://www.elastic.co/guide/en/elasticsearch/reference/7.0/search-request-track-total-hits.html
        searchQuery.setTrackTotalHits(true);
        SearchHits<Y9logAccessLog> searchHits =
            elasticsearchOperations.search(searchQuery, Y9logAccessLog.class, index);
        List<Y9logAccessLog> list = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        return new PageImpl<>(list, pageable, searchHits.getTotalHits());
    }
    */
}
