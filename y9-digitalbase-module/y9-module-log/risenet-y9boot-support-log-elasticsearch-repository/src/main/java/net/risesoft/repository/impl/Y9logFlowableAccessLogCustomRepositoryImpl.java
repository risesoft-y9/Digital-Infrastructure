package net.risesoft.repository.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.log.constant.Y9ESIndexConst;
import net.risesoft.log.constant.Y9LogSearchConsts;
import net.risesoft.log.domain.Y9LogFlowableAccessLogDO;
import net.risesoft.log.repository.Y9logFlowableAccessLogCustomRepository;
import net.risesoft.model.log.FlowableAccessLog;
import net.risesoft.model.log.LogInfoModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.util.AccessLogModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Day;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.Y9LogFlowableAccessLog;
import net.risesoft.y9public.repository.Y9LogFlowableAccessLogRepository;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationRange;
import co.elastic.clients.elasticsearch._types.aggregations.RangeAggregation;
import co.elastic.clients.elasticsearch._types.aggregations.RangeBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;

/**
 * @author qinman
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class Y9logFlowableAccessLogCustomRepositoryImpl implements Y9logFlowableAccessLogCustomRepository {

    private static final FastDateFormat DATETIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    private static final FastDateFormat DATETIME_UTC_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private final ElasticsearchOperations elasticsearchOperations;
    private final Y9LogFlowableAccessLogRepository y9logFlowableAccessLogRepository;

    // 目前日志查询页有两种情况：一种是有开始时间和结束时间，另一种是只选一个时间
    private String[] createIndexNames(String startDate, String endDate) {
        List<String> indexNameList = new ArrayList<>();
        if (StringUtils.isBlank(startDate) && StringUtils.isBlank(endDate)) {
            indexNameList.add(getCurrentYearIndexName());

        } else if (null != startDate && StringUtils.isBlank(endDate)) {
            String yearString = startDate.split("-")[0];
            indexNameList.add(Y9ESIndexConst.FLOWABLE_ACCESS_LOG_INDEX + "-" + yearString);

        } else {
            int minYearInt = Integer.parseInt(startDate.split("-")[0]);
            int maxYearInt = Integer.parseInt(endDate.split("-")[0]);
            while (minYearInt <= maxYearInt) {
                indexNameList.add(Y9ESIndexConst.FLOWABLE_ACCESS_LOG_INDEX + "-" + minYearInt);
                minYearInt++;
            }
        }
        return indexNameList.toArray(new String[0]);
    }

    private String getCurrentYearIndexName() {
        String yearStr = String.valueOf(LocalDate.now().getYear());
        return Y9ESIndexConst.FLOWABLE_ACCESS_LOG_INDEX + "-" + yearStr;
    }

    @Override
    public List<Long> listOperateTimeCount(String startDay, String endDay) {
        List<Long> list = new ArrayList<>();
        BoolQuery.Builder builder = new BoolQuery.Builder();

        builder.must(m -> m.exists(e -> e.field(Y9LogSearchConsts.USER_NAME)));
        if (StringUtils.isNotBlank(startDay) && StringUtils.isNotBlank(endDay)) {
            String sTime = startDay + " 00:00:00";
            String eTime = endDay + " 23:59:59";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date startDate = sdf.parse(sTime);
                Date endDate = sdf.parse(eTime);
                String start = DATETIME_UTC_FORMAT.format(startDate);
                String end = DATETIME_UTC_FORMAT.format(endDate);
                builder.must(m -> m.range(r -> r.date(
                    d -> d.field(Y9LogSearchConsts.LOG_TIME).from(start).to(end).format("yyyy-MM-dd'T'HH:mm:ss'Z'"))));
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }

        List<AggregationRange> aggregationRanges = new ArrayList<>();
        aggregationRanges.add(AggregationRange.of(ar -> ar.from(0d).to(1000000d)));
        aggregationRanges.add(AggregationRange.of(ar -> ar.from(1000000d).to(10000000d)));
        aggregationRanges.add(AggregationRange.of(ar -> ar.from(10000000d).to(100000000d)));
        aggregationRanges.add(AggregationRange.of(ar -> ar.from(100000000d).to(500000000d)));
        aggregationRanges.add(AggregationRange.of(ar -> ar.from(500000000d).to(1000000000d)));
        aggregationRanges.add(AggregationRange.of(ar -> ar.from(1000000000d)));

        NativeQueryBuilder nativebuilder = new NativeQueryBuilder();
        nativebuilder.withQuery(builder.build()._toQuery());
        nativebuilder.withAggregation("range-elapsedtime", RangeAggregation
            .of(r -> r.field(Y9LogSearchConsts.ELAPSED_TIME).ranges(aggregationRanges))._toAggregation());
        IndexCoordinates indexs = IndexCoordinates.of(createIndexNames(startDay, endDay));
        try {
            SearchHits<Y9LogFlowableAccessLog> searchHits =
                elasticsearchOperations.search(nativebuilder.build(), Y9LogFlowableAccessLog.class, indexs);
            ElasticsearchAggregations aggregations = (ElasticsearchAggregations)searchHits.getAggregations();
            List<? extends RangeBucket> buckets =
                aggregations.get("range-elapsedtime").aggregation().getAggregate().range().buckets().array();

            buckets.forEach(bucket -> {
                long count = bucket.docCount();
                list.add(count);
            });
        } catch (ElasticsearchException e1) {
            LOGGER.error(e1.getMessage(), e1);
        }
        return list;
    }

    @Override
    public Page<Y9LogFlowableAccessLogDO> page(int page, int rows, String sort) {
        IndexCoordinates index = IndexCoordinates.of(getCurrentYearIndexName());
        String tenantId = Y9LoginUserHolder.getTenantId();
        Pageable pageable;
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
        SearchHits<Y9LogFlowableAccessLog> searchHits =
            elasticsearchOperations.search(query, Y9LogFlowableAccessLog.class, index);
        List<Y9LogFlowableAccessLogDO> list = searchHits.stream()
            .map(SearchHit::getContent)
            .map(flowableAccessLog -> Y9ModelConvertUtil.convert(flowableAccessLog, Y9LogFlowableAccessLogDO.class))
            .collect(Collectors.toList());
        return new PageImpl<>(list, pageable, searchHits.getTotalHits());
    }

    @Override
    public Y9Page<FlowableAccessLog> pageByCondition(LogInfoModel search, String startTime, String endTime,
        Integer page, Integer rows) {
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

        SearchHits<Y9LogFlowableAccessLog> searchHits =
            elasticsearchOperations.search(query, Y9LogFlowableAccessLog.class, index);
        List<Y9LogFlowableAccessLog> list = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        long total = searchHits.getTotalHits();
        int totalPages = (int)total / rows;
        return Y9Page.success(page, total % rows == 0 ? totalPages : totalPages + 1, total,
            AccessLogModelConvertUtil.flowableLogEsListToModels(list));
    }

    @Override
    public Page<Y9LogFlowableAccessLogDO> pageElapsedTimeByCondition(LogInfoModel search, String startDay,
        String endDay, String startTime, String endTime, Integer page, Integer rows) throws ParseException {
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

        SearchHits<Y9LogFlowableAccessLog> searchHits =
            elasticsearchOperations.search(query, Y9LogFlowableAccessLog.class, index);
        List<Y9LogFlowableAccessLogDO> list = searchHits.stream()
            .map(SearchHit::getContent)
            .map(flowableAccessLog -> Y9ModelConvertUtil.convert(flowableAccessLog, Y9LogFlowableAccessLogDO.class))
            .collect(Collectors.toList());
        return new PageImpl<>(list, pageable, searchHits.getTotalHits());
    }

    @Override
    public Page<Y9LogFlowableAccessLogDO> pageOperateStatusByOperateStatus(LogInfoModel search, String operateStatus,
        String date, String hour, Integer page, Integer rows) throws ParseException {
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

            LOGGER.info("start:{} end:{}", startOfTime.getTime(), endOfTime.getTime());
            LOGGER.info("startString UTC:{} endString UTC:{}", start, end);
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.LOG_TIME).between(start, end));
        }

        PageRequest pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);

        IndexCoordinates index = IndexCoordinates.of(createIndexNames(date, null));
        SearchHits<Y9LogFlowableAccessLog> searchHits =
            elasticsearchOperations.search(query, Y9LogFlowableAccessLog.class, index);
        List<Y9LogFlowableAccessLogDO> list = searchHits.stream()
            .map(SearchHit::getContent)
            .map(flowableAccessLog -> Y9ModelConvertUtil.convert(flowableAccessLog, Y9LogFlowableAccessLogDO.class))
            .collect(Collectors.toList());
        return new PageImpl<>(list, pageable, searchHits.getTotalHits());
    }

    @Override
    public Page<Y9LogFlowableAccessLogDO> pageSearchByCondition(LogInfoModel search, String startTime, String endTime,
        Integer page, Integer rows) {
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
        if (StringUtils.isNotEmpty(search.getOperateName())) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.OPERATE_NAME).is(search.getOperateName()));
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
        if (StringUtils.isNotEmpty(search.getTitle())) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.TITLE).is(search.getTitle()));
        }
        if (StringUtils.isNotEmpty(search.getArguments())) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.ARGUMENTS).is(search.getArguments()));
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
        SearchHits<Y9LogFlowableAccessLog> searchHits =
            elasticsearchOperations.search(query, Y9LogFlowableAccessLog.class, index);
        List<Y9LogFlowableAccessLogDO> list = searchHits.stream()
            .map(SearchHit::getContent)
            .map(flowableAccessLog -> Y9ModelConvertUtil.convert(flowableAccessLog, Y9LogFlowableAccessLogDO.class))
            .collect(Collectors.toList());
        return new PageImpl<>(list, pageable, searchHits.getTotalHits());
    }

    @Override
    public void save(Y9LogFlowableAccessLogDO y9LogFlowableAccessLogDO) {
        IndexOperations indexOps = elasticsearchOperations.indexOps(Y9LogFlowableAccessLog.class);
        if (!indexOps.exists()) {
            synchronized (this) {
                if (!indexOps.exists()) {
                    indexOps.create();
                    indexOps.putMapping(indexOps.createMapping(Y9LogFlowableAccessLog.class));
                }
            }
        }
        Y9LogFlowableAccessLog flowableAccessLog =
            Y9ModelConvertUtil.convert(y9LogFlowableAccessLogDO, Y9LogFlowableAccessLog.class);
        y9logFlowableAccessLogRepository.save(flowableAccessLog);
    }
}
