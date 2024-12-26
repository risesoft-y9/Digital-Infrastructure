package net.risesoft.y9public.repository.custom.impl;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.LongBounds;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.range.ParsedRange;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.range.RangeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.elasticsearch.core.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

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
import net.risesoft.y9.util.Y9Util;
import net.risesoft.y9public.entity.Y9logAccessLog;
import net.risesoft.y9public.repository.Y9logAccessLogRepository;
import net.risesoft.y9public.repository.custom.Y9logAccessLogCustomRepository;
import net.risesoft.y9public.repository.custom.Y9logMappingCustomRepository;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;

/**
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class Y9logAccessLogCustomRepositoryImpl implements Y9logAccessLogCustomRepository {

    private static final FastDateFormat DATETIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    private static final FastDateFormat DATETIME_UTC_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private final Y9logMappingCustomRepository y9logMappingService;
    private final ElasticsearchOperations elasticsearchOperations;
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

    @Override
    public Map<String, Object> getAppClickCount(String tenantId, String guidPath, String startDay, String endDay)
        throws UnknownHostException {
        Map<String, Object> returnMap = new HashMap<>();
        List<String> strList = new ArrayList<>();
        List<Long> longList = new ArrayList<>();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery(Y9LogSearchConsts.METHOD_NAME, Y9LogSearchConsts.APP_METHODNAME));
        if (StringUtils.isNotBlank(tenantId)) {
            boolQueryBuilder.must(QueryBuilders.termQuery(Y9LogSearchConsts.TENANT_ID, Y9Util.escape(tenantId)));
        }
        if (StringUtils.isNotBlank(guidPath)) {
            boolQueryBuilder.must(QueryBuilders.wildcardQuery(Y9LogSearchConsts.GUID_PATH, guidPath + "*"));
        }
        if (StringUtils.isNotBlank(startDay) && StringUtils.isNotBlank(endDay)) {
            try {
                Date sDay = Y9Day.getStartOfDay(new SimpleDateFormat("yyyy-MM-dd").parse(startDay));
                Date eDay = Y9Day.getEndOfDay(new SimpleDateFormat("yyyy-MM-dd").parse(endDay));

                boolQueryBuilder.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOG_TIME)
                    .from(String.valueOf(sDay.getTime())).to(String.valueOf(eDay.getTime())));
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        builder.withQuery(boolQueryBuilder);
        builder.withAggregations(AggregationBuilders.terms("by_modularname").field(Y9LogSearchConsts.MODULAR_NAME)
            .size(10000).order(BucketOrder.count(false)));
        builder.withTrackTotalHits(true);

        try {
            SearchHits<Y9logAccessLog> searchHits = elasticsearchOperations.search(builder.build(),
                Y9logAccessLog.class, IndexCoordinates.of(createIndexNames(startDay, endDay)));
            ElasticsearchAggregations aggregations = (ElasticsearchAggregations)searchHits.getAggregations();
            if (aggregations != null) {
                Terms terms = aggregations.aggregations().get("by_modularname");
                List<? extends Terms.Bucket> buckets = terms.getBuckets();
                buckets.forEach(bucket -> {
                    String appName = bucket.getKeyAsString();
                    long count = bucket.getDocCount();
                    strList.add(appName);
                    longList.add(count);
                });
                int length = buckets.size();
                returnMap.put("number", length);
                returnMap.put("name", strList);
                returnMap.put("value", longList);
            }
            return returnMap;
        } catch (ElasticsearchException e) {
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

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.existsQuery(Y9LogSearchConsts.USER_NAME));
        if (StringUtils.isNotBlank(tenantId)) {
            boolQueryBuilder.must(QueryBuilders.termQuery(Y9LogSearchConsts.TENANT_ID, Y9Util.escape(tenantId)));
        }
        if (StringUtils.isNotBlank(guidPath)) {
            boolQueryBuilder.must(QueryBuilders.wildcardQuery(Y9LogSearchConsts.GUID_PATH, guidPath + "*"));
        }
        if (StringUtils.isNotBlank(startDay) && StringUtils.isNotBlank(endDay)) {
            try {
                Date sDay = Y9Day.getStartOfDay(new SimpleDateFormat("yyyy-MM-dd").parse(startDay));
                Date eDay = Y9Day.getEndOfDay(new SimpleDateFormat("yyyy-MM-dd").parse(endDay));

                boolQueryBuilder.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOG_TIME)
                    .from(String.valueOf(sDay.getTime())).to(String.valueOf(eDay.getTime())));
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        NativeSearchQueryBuilder nativebuilder = new NativeSearchQueryBuilder();
        nativebuilder.withQuery(boolQueryBuilder);
        nativebuilder.withAggregations(AggregationBuilders.terms("by_modularname").field(Y9LogSearchConsts.MODULAR_NAME)
            .size(10000).order(BucketOrder.count(false)));
        nativebuilder.withTrackTotalHits(true);
        try {
            IndexCoordinates indexs = IndexCoordinates.of(createIndexNames(startDay, endDay));
            SearchHits<Y9logAccessLog> searchHits =
                elasticsearchOperations.search(nativebuilder.build(), Y9logAccessLog.class, indexs);
            ElasticsearchAggregations aggregations = (ElasticsearchAggregations)searchHits.getAggregations();
            if (aggregations != null) {
                Terms terms = aggregations.aggregations().get("by_modularname");
                List<? extends Terms.Bucket> buckets = terms.getBuckets();
                buckets.forEach(bucket -> {
                    String modularName = bucket.getKeyAsString();
                    String modularCnName = y9logMappingService.getCnModularName(modularName);
                    if (StringUtils.isNotBlank(modularCnName)) {
                        modularName = modularCnName;
                    }
                    long count = bucket.getDocCount();
                    strList.add(modularName);
                    longList.add(count);
                });
                int length = buckets.size();
                map.put("number", length);
                map.put("name", strList);
                map.put("value", longList);
            }
            return map;
        } catch (ElasticsearchException e) {
            LOGGER.error(e.getMessage(), e);
            return Collections.emptyMap();
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
        try {
            day = new SimpleDateFormat("yyyy-MM-dd").parse(selectedDate);
        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
        }
        Date startOfTime = Y9Day.getStartOfDay(day);
        Date endOfTime = Y9Day.getEndOfDay(day);

        BoolQueryBuilder sQueryBuilder = QueryBuilders.boolQuery();
        sQueryBuilder.must(QueryBuilders.existsQuery(Y9LogSearchConsts.USER_NAME));
        if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
            sQueryBuilder.must(QueryBuilders.termQuery(Y9LogSearchConsts.TENANT_ID, Y9Util.escape(tenantId)));
        }
        sQueryBuilder.must(QueryBuilders.termQuery(Y9LogSearchConsts.SUCCESS, success));
        sQueryBuilder.must(
            QueryBuilders.rangeQuery(Y9LogSearchConsts.LOG_TIME).from(DATETIME_UTC_FORMAT.format(startOfTime.getTime()))
                .to(DATETIME_UTC_FORMAT.format(endOfTime.getTime())));

        BoolQueryBuilder eQueryBuilder = QueryBuilders.boolQuery();
        eQueryBuilder.must(QueryBuilders.existsQuery(Y9LogSearchConsts.USER_NAME));
        if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
            eQueryBuilder.must(QueryBuilders.termQuery(Y9LogSearchConsts.TENANT_ID, Y9Util.escape(tenantId)));
        }
        eQueryBuilder.must(QueryBuilders.termQuery(Y9LogSearchConsts.SUCCESS, error));
        eQueryBuilder.must(
            QueryBuilders.rangeQuery(Y9LogSearchConsts.LOG_TIME).from(DATETIME_UTC_FORMAT.format(startOfTime.getTime()))
                .to(DATETIME_UTC_FORMAT.format(endOfTime.getTime())));

        DateHistogramAggregationBuilder sdateHistogramAgg =
            AggregationBuilders.dateHistogram("by_success_logtime").field(Y9LogSearchConsts.LOG_TIME)
                .calendarInterval(DateHistogramInterval.HOUR).timeZone(ZoneId.of("Asia/Shanghai")).minDocCount(0)
                .extendedBounds(new LongBounds(startOfTime.getTime(), endOfTime.getTime()));
        NativeSearchQueryBuilder sNativebuilder = new NativeSearchQueryBuilder();
        sNativebuilder.withQuery(sQueryBuilder);
        sNativebuilder.withAggregations(sdateHistogramAgg);

        DateHistogramAggregationBuilder edateHistogramAgg =
            AggregationBuilders.dateHistogram("by_error_logtime").field(Y9LogSearchConsts.LOG_TIME)
                .calendarInterval(DateHistogramInterval.HOUR).timeZone(ZoneId.of("Asia/Shanghai")).minDocCount(0)
                .extendedBounds(new LongBounds(startOfTime.getTime(), endOfTime.getTime()));
        NativeSearchQueryBuilder eNativebuilder = new NativeSearchQueryBuilder();
        eNativebuilder.withQuery(eQueryBuilder);
        eNativebuilder.withAggregations(edateHistogramAgg);

        IndexCoordinates index = IndexCoordinates
            .of(createIndexNames(DATETIME_UTC_FORMAT.format(startOfTime), DATETIME_UTC_FORMAT.format(endOfTime)));
        try {
            SearchHits<Y9logAccessLog> searchHits =
                elasticsearchOperations.search(sNativebuilder.build(), Y9logAccessLog.class, index);
            ElasticsearchAggregations aggregations = (ElasticsearchAggregations)searchHits.getAggregations();
            if (aggregations != null) {
                ParsedDateHistogram terms = aggregations.aggregations().get("by_success_logtime");
                List<? extends Histogram.Bucket> buckets = terms.getBuckets();
                buckets.forEach(bucket -> {
                    long count = bucket.getDocCount();
                    countOfSuccess.add(count);
                });
            }
        } catch (ElasticsearchException e1) {
            LOGGER.error(e1.getMessage(), e1);
        }

        try {
            SearchHits<Y9logAccessLog> searchHits =
                elasticsearchOperations.search(eNativebuilder.build(), Y9logAccessLog.class, index);
            ElasticsearchAggregations aggregations = (ElasticsearchAggregations)searchHits.getAggregations();
            if (aggregations != null) {
                ParsedDateHistogram terms = aggregations.aggregations().get("by_error_logtime");
                List<? extends Histogram.Bucket> buckets = terms.getBuckets();
                buckets.forEach(bucket -> {
                    long count = bucket.getDocCount();
                    countOfError.add(count);
                });
            }
        } catch (ElasticsearchException e1) {
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
        Criteria criteria =
            new Criteria(Y9LogSearchConsts.USER_NAME).exists().and(Y9LogSearchConsts.USER_NAME).is(loginName);
        if (tenantId != null) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.TENANT_ID).is(tenantId));
        }
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            try {
                Date startDate = DATETIME_FORMAT.parse(startTime);
                Date endDate = DATETIME_FORMAT.parse(endTime);
                String start = DATETIME_UTC_FORMAT.format(startDate);
                String end = DATETIME_UTC_FORMAT.format(endDate);
                criteria.subCriteria(new Criteria(Y9LogSearchConsts.LOG_TIME).between(start, end));
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        Query query = new CriteriaQuery(criteria);
        query.setTrackTotalHits(true);
        try {
            IndexCoordinates index = IndexCoordinates.of(getCurrentYearIndexName());

            SearchHits<Y9logAccessLog> searchHits = elasticsearchOperations.search(query, Y9logAccessLog.class, index);
            List<Y9logAccessLog> list = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());

            list.forEach(hit -> {
                String accesslogjson = Y9JsonUtil.writeValueAsString(hit);
                strList.add(accesslogjson);
            });
        } catch (ElasticsearchException e1) {
            LOGGER.warn(e1.getMessage(), e1);
        }
        return strList;
    }

    @Override
    public List<Long> listOperateTimeCount(String startDay, String endDay, Integer tenantType) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.existsQuery(Y9LogSearchConsts.USER_NAME));
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
            boolQueryBuilder.must(QueryBuilders.matchQuery(Y9LogSearchConsts.TENANT_ID, tenantId));
        }
        if (StringUtils.isNotBlank(startDay) && StringUtils.isNotBlank(endDay)) {
            String sTime = startDay + " 00:00:00";
            String eTime = endDay + " 23:59:59";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date startDate = sdf.parse(sTime);
                Date endDate = sdf.parse(eTime);
                String start = DATETIME_UTC_FORMAT.format(startDate);
                String end = DATETIME_UTC_FORMAT.format(endDate);
                LOGGER.info(start + "   " + end);
                boolQueryBuilder.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOG_TIME).from(start).to(end));
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        RangeAggregationBuilder ranges =
            AggregationBuilders.range("range-elapsedtime").field(Y9LogSearchConsts.ELAPSED_TIME).addUnboundedTo(1000000)
                .addRange(1000000, 10000000).addRange(10000000, 100000000).addRange(100000000, 500000000)
                .addRange(500000000, 1000000000).addUnboundedFrom(1000000000);
        NativeSearchQueryBuilder nativebuilder = new NativeSearchQueryBuilder();
        nativebuilder.withQuery(boolQueryBuilder);
        nativebuilder.withAggregations(ranges);
        try {
            IndexCoordinates index = IndexCoordinates.of(createIndexNames(startDay, endDay));
            SearchHits<Y9logAccessLog> searchHits =
                elasticsearchOperations.search(nativebuilder.build(), Y9logAccessLog.class, index);
            ElasticsearchAggregations aggregations = (ElasticsearchAggregations)searchHits.getAggregations();
            List<Long> list = new ArrayList<>();
            if (aggregations != null) {
                ParsedRange terms = aggregations.aggregations().get("range-elapsedtime");
                List<? extends Range.Bucket> buckets = terms.getBuckets();
                buckets.forEach(bucket -> {
                    long count = bucket.getDocCount();
                    list.add(count);
                });
            }
            return list;
        } catch (ElasticsearchException e) {
            LOGGER.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public Page<Y9logAccessLog> page(int page, int rows, String sort) {
        IndexCoordinates index = IndexCoordinates.of(getCurrentYearIndexName());
        String tenantId = Y9LoginUserHolder.getTenantId();
        Pageable pageable = null;
        Criteria criteria = new Criteria(Y9LogSearchConsts.USER_NAME).exists();

        if (tenantId != null) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.TENANT_ID).is(tenantId));
        }

        if (StringUtils.isNoneBlank(sort)) {
            pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, sort);
        } else {
            pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        }

        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);
        SearchHits<Y9logAccessLog> searchHits = elasticsearchOperations.search(query, Y9logAccessLog.class, index);
        List<Y9logAccessLog> list = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        return new PageImpl<>(list, pageable, searchHits.getTotalHits());
    }

    @Override
    public Y9Page<AccessLog> pageByCondition(LogInfoModel search, String startTime, String endTime, Integer page,
        Integer rows) {
        IndexCoordinates index = IndexCoordinates.of(createIndexNames(startTime, endTime));
        Criteria criteria = new Criteria(Y9LogSearchConsts.USER_NAME).exists();
        if (StringUtils.isNotBlank(search.getLogLevel())) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.LOG_LEVEL).is(search.getLogLevel()));
        }
        if (StringUtils.isNotBlank(search.getSuccess())) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.SUCCESS).is(search.getSuccess()));
        }
        if (StringUtils.isNotBlank(search.getOperateType())) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.OPERATE_TYPE).is(search.getOperateType()));
        }
        if (StringUtils.isNotBlank(search.getUserName())) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.USER_NAME).is(search.getUserName()));
        }
        if (StringUtils.isNotBlank(search.getUserHostIp())) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.USER_HOST_IP).is(search.getUserHostIp()));
        }
        if (StringUtils.isNotEmpty(search.getOperateName())) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.OPERATE_NAME).is(search.getOperateName()));
        }
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            startTime = startTime + " 00:00:00";
            endTime = endTime + " 23:59:59";
            long startDate = 0;
            long endDate = 0;
            try {
                startDate = DATETIME_FORMAT.parse(startTime).getTime();
                endDate = DATETIME_FORMAT.parse(endTime).getTime();
                criteria.subCriteria(new Criteria(Y9LogSearchConsts.LOG_TIME).between(startDate, endDate));
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        Pageable pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);

        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);

        SearchHits<Y9logAccessLog> searchHits = elasticsearchOperations.search(query, Y9logAccessLog.class, index);
        List<Y9logAccessLog> list = searchHits.stream().map(s -> s.getContent()).collect(Collectors.toList());
        long total = searchHits.getTotalHits();
        int totalPages = (int)total / rows;
        return Y9Page.success(page, total % rows == 0 ? totalPages : totalPages + 1, total,
            AccessLogModelConvertUtil.logEsListToModels(list));
    }

    @Override
    public Y9Page<AccessLog> pageByOperateType(String operateType, Integer page, Integer rows) {
        IndexCoordinates index = IndexCoordinates.of(getCurrentYearIndexName());
        Criteria criteria = new Criteria(Y9LogSearchConsts.OPERATE_TYPE).is(operateType);
        Pageable pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);

        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);
        SearchHits<Y9logAccessLog> searchHits = elasticsearchOperations.search(query, Y9logAccessLog.class, index);
        List<Y9logAccessLog> list = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
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
                    elasticsearchOperations.search(criteriaQuery, Y9logAccessLog.class, index);
                List<Y9logAccessLog> list = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());

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
        Criteria criteria = new Criteria(Y9LogSearchConsts.USER_NAME).exists();
        if (tenantId != null) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.TENANT_ID).is(tenantId));
        }
        if (StringUtils.isNotEmpty(userId)) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.USER_ID).is(userId));
        }
        if (StringUtils.isNotEmpty(managerLevel)) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.MANAGER_LEVEL).is(managerLevel));
        }
        if (StringUtils.isNoneBlank(sort)) {
            pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, sort);
        } else {
            pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        }

        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);

        SearchHits<Y9logAccessLog> searchHits = elasticsearchOperations.search(query, Y9logAccessLog.class, index);
        List<Y9logAccessLog> list = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        return new PageImpl<>(list, pageable, searchHits.getTotalHits());
    }

    @Override
    public Page<Y9logAccessLog> pageElapsedTimeByCondition(LogInfoModel search, String startDay, String endDay,
        String startTime, String endTime, Integer tenantType, Integer page, Integer rows) throws ParseException {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Criteria criteria = new Criteria(Y9LogSearchConsts.USER_NAME).exists();
        if (StringUtils.isNotBlank(startDay) && StringUtils.isNotBlank(endDay)) {
            Date sDay = Y9Day.getStartOfDay(new SimpleDateFormat("yyyy-MM-dd").parse(startDay));
            Date eDay = Y9Day.getEndOfDay(new SimpleDateFormat("yyyy-MM-dd").parse(endDay));
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.LOG_TIME).between(sDay.getTime(), eDay.getTime()));
        }
        if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.TENANT_ID).is(tenantId));
        }
        if (StringUtils.isNotBlank(search.getLogLevel())) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.LOG_LEVEL).is(search.getLogLevel()));
        }
        if (StringUtils.isNotBlank(search.getSuccess())) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.SUCCESS).is(search.getSuccess()));
        }
        if (StringUtils.isNotBlank(search.getOperateType())) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.OPERATE_TYPE).is(search.getOperateType()));
        }
        if (StringUtils.isNotBlank(startTime)) {
            long smallTime = Long.parseLong(startTime);
            if (StringUtils.isNotBlank(endTime)) {
                long largeTime = Long.parseLong(endTime);
                criteria.subCriteria(new Criteria(Y9LogSearchConsts.ELAPSED_TIME).between(smallTime, largeTime));
            } else {
                criteria.subCriteria(new Criteria(Y9LogSearchConsts.ELAPSED_TIME).greaterThan(smallTime));
            }
        }

        PageRequest pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);
        IndexCoordinates index = IndexCoordinates.of(createIndexNames(startDay, endDay));

        SearchHits<Y9logAccessLog> searchHits = elasticsearchOperations.search(query, Y9logAccessLog.class, index);
        List<Y9logAccessLog> list = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        return new PageImpl<>(list, pageable, searchHits.getTotalHits());
    }

    @Override
    public Page<Y9logAccessLog> pageOperateStatusByOperateStatus(LogInfoModel search, String operateStatus, String date,
        String hour, Integer tenantType, Integer page, Integer rows) throws ParseException {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Criteria criteria = new Criteria(Y9LogSearchConsts.USER_NAME).exists();

        if (StringUtils.isNotBlank(operateStatus)) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.SUCCESS).is(operateStatus));
        }
        if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.TENANT_ID).is(tenantId));
        }
        if (StringUtils.isNotBlank(search.getUserName())) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.USER_NAME).is(search.getUserName()));
        }
        if (StringUtils.isNotBlank(search.getTenantName())) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.TENANT_NAME).is(search.getTenantName()));
        }
        if (StringUtils.isNotBlank(search.getLogLevel())) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.LOG_LEVEL).is(search.getLogLevel()));
        }
        if (StringUtils.isNotBlank(search.getOperateType())) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.OPERATE_TYPE).is(search.getOperateType()));
        }
        if (StringUtils.isNotBlank(date) && StringUtils.isNotBlank(hour)) {
            int h = Integer.parseInt(hour);
            Calendar cal = Calendar.getInstance();
            Date day = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            cal.setTime(day);
            cal.add(Calendar.HOUR_OF_DAY, h);
            cal.add(Calendar.MINUTE, 0);
            cal.add(Calendar.SECOND, 0);
            cal.add(Calendar.MILLISECOND, 0);
            Date startOfTime = cal.getTime();

            cal.add(Calendar.MINUTE, 59);
            cal.add(Calendar.SECOND, 59);
            cal.add(Calendar.MILLISECOND, 999);
            Date endOfTime = cal.getTime();

            FastDateFormat DATETIME_UTC_FORMAT2 =
                FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss'Z'", TimeZone.getTimeZone("UTC"));

            String start = DATETIME_UTC_FORMAT2.format(startOfTime);
            String end = DATETIME_UTC_FORMAT2.format(endOfTime);

            LOGGER.info("start:{} end:{} fuwuqi:{}", startOfTime.getTime(), endOfTime.getTime());
            LOGGER.info("startString UTC:{} endString UTC:{}", start, end);
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.LOG_TIME).between(start, end));
        }

        PageRequest pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);

        IndexCoordinates index = IndexCoordinates.of(createIndexNames(date, null));
        SearchHits<Y9logAccessLog> searchHits = elasticsearchOperations.search(query, Y9logAccessLog.class, index);
        List<Y9logAccessLog> list = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        return new PageImpl<>(list, pageable, searchHits.getTotalHits());
    }

    @Override
    public Page<Y9logAccessLog> pageSearchByCondition(LogInfoModel search, String startTime, String endTime,
        Integer tenantType, Integer page, Integer rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Criteria criteria = new Criteria(Y9LogSearchConsts.USER_NAME).exists();

        if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.TENANT_ID).is(tenantId));
        }
        if (StringUtils.isNotBlank(search.getLogLevel())) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.LOG_LEVEL).is(search.getLogLevel()));
        }
        if (StringUtils.isNotBlank(search.getSuccess())) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.SUCCESS).is(search.getSuccess()));
        }
        if (StringUtils.isNotBlank(search.getOperateType())) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.OPERATE_TYPE).is(search.getOperateType()));
        }
        if (StringUtils.isNotBlank(search.getUserName())) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.USER_NAME).is(search.getUserName()));
        }
        if (StringUtils.isNotBlank(search.getUserHostIp())) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.USER_HOST_IP).is(search.getUserHostIp()));
        }
        if (StringUtils.isNotBlank(search.getTenantName())) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.TENANT_NAME).is(search.getTenantName()));
        }
        if (StringUtils.isNotBlank(search.getModularName())) {
            criteria.subCriteria(new Criteria().or(Y9LogSearchConsts.MODULAR_NAME).contains(search.getModularName()));
        }
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            startTime = startTime + " 00:00:00";
            endTime = endTime + " 23:59:59";
            try {
                long startDate = DATETIME_FORMAT.parse(startTime).getTime();
                long endDate = DATETIME_FORMAT.parse(endTime).getTime();
                criteria.subCriteria(new Criteria(Y9LogSearchConsts.LOG_TIME).between(startDate, endDate));
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }

        PageRequest pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);

        IndexCoordinates index = IndexCoordinates.of(createIndexNames(startTime, endTime));
        SearchHits<Y9logAccessLog> searchHits = elasticsearchOperations.search(query, Y9logAccessLog.class, index);
        List<Y9logAccessLog> list = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        return new PageImpl<>(list, pageable, searchHits.getTotalHits());
    }

    @Override
    public void save(Y9logAccessLog y9logAccessLog) {
        IndexOperations indexOps = elasticsearchOperations.indexOps(Y9logAccessLog.class);
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
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.TENANT_ID).is(tenantId));
        }
        if (StringUtils.isNotEmpty(managerLevel)) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.MANAGER_LEVEL).is(managerLevel));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getUserName())) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.USER_NAME).contains(loginInfoModel.getUserName()));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getUserHostIp())) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.USER_HOST_IP).contains(loginInfoModel.getUserHostIp()));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getModularName())) {
            criteria
                .subCriteria(new Criteria(Y9LogSearchConsts.MODULAR_NAME).contains(loginInfoModel.getModularName()));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getOperateName())) {
            criteria
                .subCriteria(new Criteria(Y9LogSearchConsts.OPERATE_NAME).contains(loginInfoModel.getOperateName()));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getOperateType())) {
            criteria
                .subCriteria(new Criteria(Y9LogSearchConsts.OPERATE_TYPE).contains(loginInfoModel.getOperateType()));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getSuccess())) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.SUCCESS).is(loginInfoModel.getSuccess()));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getLogLevel())) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.LOG_LEVEL).is(loginInfoModel.getLogLevel()));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getStartTime())
            && StringUtils.isNotBlank(loginInfoModel.getEndTime())) {
            String sTime = loginInfoModel.getStartTime() + " 00:00:00";
            String eTime = loginInfoModel.getEndTime() + " 23:59:59";
            try {
                Date startDate = DATETIME_FORMAT.parse(sTime);
                Date endDate = DATETIME_FORMAT.parse(eTime);
                String start = DATETIME_UTC_FORMAT.format(startDate);
                String end = DATETIME_UTC_FORMAT.format(endDate);
                criteria.subCriteria(new Criteria(Y9LogSearchConsts.LOG_TIME).between(start, end));
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
        SearchHits<Y9logAccessLog> searchHits = elasticsearchOperations.search(criteriaQuery, Y9logAccessLog.class);
        List<Y9logAccessLog> list = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        return new PageImpl<>(list, pageable, searchHits.getTotalHits());
    }
}
