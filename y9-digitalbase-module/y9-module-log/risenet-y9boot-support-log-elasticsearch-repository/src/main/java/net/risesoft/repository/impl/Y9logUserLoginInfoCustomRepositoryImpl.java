package net.risesoft.repository.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
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
import net.risesoft.log.domain.Y9LogUserLoginInfoDO;
import net.risesoft.log.repository.Y9logUserLoginInfoCustomRepository;
import net.risesoft.model.log.LoginLogQuery;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9.util.Y9Util;
import net.risesoft.y9public.entity.Y9LogUserLoginInfo;
import net.risesoft.y9public.repository.Y9LogUserLoginInfoRepository;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;

/**
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class Y9logUserLoginInfoCustomRepositoryImpl implements Y9logUserLoginInfoCustomRepository {

    private static final IndexCoordinates INDEX = IndexCoordinates.of(Y9ESIndexConst.LOGIN_INFO_INDEX);

    private final Y9LogUserLoginInfoRepository y9logUserLoginInfoRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    private static PageImpl<Y9LogUserLoginInfoDO> poPageToDoPage(Page<Y9LogUserLoginInfo> userLoginInfoPage) {
        List<Y9LogUserLoginInfoDO> list = userLoginInfoPage.getContent()
            .stream()
            .map(l -> Y9ModelConvertUtil.convert(l, Y9LogUserLoginInfoDO.class))
            .collect(Collectors.toList());
        return new PageImpl<>(list, userLoginInfoPage.getPageable(), userLoginInfoPage.getTotalElements());
    }

    @Override
    public long countByLoginTimeBetweenAndSuccess(Date startTime, Date endTime, String success) {
        Criteria criteria = new Criteria(Y9LogSearchConsts.LOGIN_TIME).between(startTime.getTime(), endTime.getTime());
        if (StringUtils.isNotBlank(success)) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.SUCCESS).is(success));
        }
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.TENANT_ID).is(tenantId));
        }
        Query query = new CriteriaQuery(criteria);
        return elasticsearchOperations.count(query, INDEX);
    }

    @Override
    public long countBySuccessAndUserHostIpAndUserId(String success, String userHostIp, String userId) {
        Criteria criteria = new Criteria();
        if (StringUtils.isNotBlank(success)) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.SUCCESS).is(success));
        }
        if (StringUtils.isNotBlank(userId)) {
            String parserUserId = Y9Util.escape(userId);
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.USER_ID).is(parserUserId));
        }
        if (StringUtils.isNotBlank(userHostIp)) {
            String parserUserHostIp = Y9Util.escape(userHostIp);
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.USER_HOST_IP).is(parserUserHostIp));
        }
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.TENANT_ID).is(tenantId));
        }
        Query query = new CriteriaQuery(criteria);
        return elasticsearchOperations.count(query, INDEX);
    }

    @Override
    public long countByUserHostIpLikeAndLoginTimeBetweenAndSuccess(String userHostIp, Date startTime, Date endTime,
        String success) {
        Criteria criteria = new Criteria(Y9LogSearchConsts.LOGIN_TIME).between(startTime.getTime(), endTime.getTime());
        if (StringUtils.isNotBlank(success)) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.SUCCESS).is(success));
        }
        if (StringUtils.isNotBlank(userHostIp)) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.USER_HOST_IP).startsWith(userHostIp));
        }
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.TENANT_ID).is(tenantId));
        }
        Query query = new CriteriaQuery(criteria);
        return elasticsearchOperations.count(query, INDEX);
    }

    @Override
    public List<Map<String, Object>> listUserHostIpByCip(String cip) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        BoolQuery.Builder builder = new BoolQuery.Builder();
        builder.must(m -> m.wildcard(w -> w.field(Y9LogSearchConsts.USER_HOST_IP).value(cip + "*")));
        if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
            builder.must(m -> m.term(t -> t.field(Y9LogSearchConsts.TENANT_ID).value(tenantId)));
        }

        Aggregation userHostIpAggs =
            Aggregation.of(a -> a.terms(t -> t.field(Y9LogSearchConsts.USER_HOST_IP).size(Integer.MAX_VALUE)));

        NativeQueryBuilder nativeBuilder = new NativeQueryBuilder();
        nativeBuilder.withQuery(builder.build()._toQuery());
        nativeBuilder.withAggregation("by_UserHostIP", userHostIpAggs);

        try {
            SearchHits<Y9LogUserLoginInfo> searchHits =
                elasticsearchOperations.search(nativeBuilder.build(), Y9LogUserLoginInfo.class, INDEX);
            if (searchHits.hasAggregations()) {
                ElasticsearchAggregations aggregations = (ElasticsearchAggregations)searchHits.getAggregations();
                var aggregation = aggregations.get("by_UserHostIP");
                if (aggregation != null) {
                    Aggregate aggregate = aggregation.aggregation().getAggregate();
                    if (aggregate != null) {
                        return aggregate.sterms().buckets().array().stream().map(bucket -> {
                            Map<String, Object> map = new HashMap<>();
                            String serverIp = bucket.key().stringValue();
                            map.put("serverIp", serverIp);
                            map.put("text", serverIp + "<span style='color:red'>(" + bucket.docCount() + ")</span>");
                            return map;
                        }).collect(Collectors.toList());
                    }
                }
            }
        } catch (ElasticsearchException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    @Override
    public Y9Page<Y9LogUserLoginInfoDO> page(String tenantId, String userHostIp, String userId, String success,
        String startTime, String endTime, Y9PageQuery pageQuery) {
        Criteria criteria = new Criteria();
        if (StringUtils.isNotBlank(userHostIp)) {
            String parserUserHostIp = Y9Util.escape(userHostIp);
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.USER_HOST_IP).is(parserUserHostIp));
        }
        if (StringUtils.isNotBlank(userId)) {
            String parseUserId = Y9Util.escape(userId);
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.USER_ID).is(parseUserId));
        }
        if (StringUtils.isNotBlank(tenantId) && !tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.TENANT_ID).is(tenantId));
        }
        if (StringUtils.isNotBlank(success)) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.SUCCESS).is(success));
        }
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                long startTimestamp = simpleDateFormat.parse(startTime).getTime();
                long endTimestamp = simpleDateFormat.parse(endTime).getTime();
                criteria.subCriteria(new Criteria(Y9LogSearchConsts.LOGIN_TIME).between(startTimestamp, endTimestamp));
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }

        int page = pageQuery.getPage4Db();
        int size = pageQuery.getSize();
        Query query = new CriteriaQuery(criteria).setPageable(PageRequest.of(page, size))
            .addSort(Sort.by(Direction.DESC, Y9LogSearchConsts.LOGIN_TIME));

        SearchHits<Y9LogUserLoginInfo> searchHits =
            elasticsearchOperations.search(query, Y9LogUserLoginInfo.class, INDEX);
        List<Y9LogUserLoginInfoDO> list = searchHits.stream()
            .map(SearchHit::getContent)
            .map(l -> Y9ModelConvertUtil.convert(l, Y9LogUserLoginInfoDO.class))
            .collect(Collectors.toList());
        int totalPages = (int)searchHits.getTotalHits() / size;
        return Y9Page.success(page, searchHits.getTotalHits() % size == 0 ? totalPages : totalPages + 1,
            searchHits.getTotalHits(), list);
    }

    @Override
    public Y9Page<Y9LogUserLoginInfoDO> pageByLoginTimeBetweenAndSuccess(Date startTime, Date endTime, String success,
        int page, int rows) {
        return pageByUserHostIpLikeAndLoginTimeBetweenAndSuccess(null, startTime, endTime, success, page, rows);
    }

    @Override
    public Y9Page<Map<String, Object>> pageByUserHostIpAndSuccess(String userHostIp, String success, int page,
        int rows) {
        int startIndex = (page - 1) * rows;
        int endIndex = page * rows;
        List<Map<String, Object>> strList = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();

        // 构建查询条件
        BoolQuery.Builder builder = new BoolQuery.Builder();
        builder.must(m -> m.term(t -> t.field(Y9LogSearchConsts.SUCCESS).value(success)));
        builder.must(m -> m.term(t -> t.field(Y9LogSearchConsts.USER_HOST_IP).value(userHostIp)));
        if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
            builder.must(m -> m.term(t -> t.field(Y9LogSearchConsts.TENANT_ID).value(tenantId)));
        }

        // 构建topHits子聚合
        Aggregation topHitsAgg = Aggregation.of(a -> a.topHits(t -> t.size(1).explain(true)));

        // 构建terms聚合并添加topHits作为子聚合 - 使用链式调用
        Aggregation usernameAggs =
            Aggregation.of(a -> a.terms(TermsAggregation.of(t -> t.field(Y9LogSearchConsts.USER_NAME)))
                .aggregations("topHits", topHitsAgg));

        NativeQueryBuilder nativeBuilder = new NativeQueryBuilder();
        nativeBuilder.withQuery(builder.build()._toQuery());
        nativeBuilder.withAggregation("username-aggs", usernameAggs);

        SearchHits<Y9LogUserLoginInfo> searchHits =
            elasticsearchOperations.search(nativeBuilder.build(), Y9LogUserLoginInfo.class, INDEX);
        if (searchHits.hasAggregations()) {
            ElasticsearchAggregations aggregations = (ElasticsearchAggregations)searchHits.getAggregations();
            var termsAggregation = aggregations.get("username-aggs");
            if (termsAggregation != null) {
                Aggregate aggregate = termsAggregation.aggregation().getAggregate();
                if (aggregate != null) {
                    List<? extends StringTermsBucket> stbList = aggregate.sterms().buckets().array();
                    int totalCount = stbList.size();
                    endIndex = Math.min(endIndex, totalCount);
                    for (int i = startIndex; i < endIndex; i++) {
                        Map<String, Object> map = new HashMap<>();
                        StringTermsBucket bucket = stbList.get(i);
                        long count = bucket.docCount();

                        // 从子聚合中获取topHits结果
                        var bucketAggs = bucket.aggregations();
                        if (bucketAggs != null && !bucketAggs.isEmpty()) {
                            var topHitsAggResult = bucketAggs.get("topHits");
                            if (topHitsAggResult != null) {
                                Aggregate topHitsAggregate = topHitsAggResult;
                                if (topHitsAggregate != null) {
                                    var topHit = topHitsAggregate.topHits().hits().hits().get(0);
                                    Y9LogUserLoginInfoDO y9LogUserLoginInfoDO =
                                        topHit.source().to(Y9LogUserLoginInfoDO.class);
                                    map.put(Y9LogSearchConsts.USER_ID, y9LogUserLoginInfoDO.getUserId());
                                    map.put(Y9LogSearchConsts.USER_NAME, y9LogUserLoginInfoDO.getUserName());
                                }
                            }
                        }
                        map.put("serverCount", String.valueOf(count));
                        strList.add(map);
                    }
                    return Y9Page.success(page, (int)Math.ceil((float)totalCount / (float)rows), totalCount, strList);
                }
            }
        }
        int totalCount = 0;
        return Y9Page.failure(page, (int)Math.ceil((float)totalCount / (float)rows), totalCount,
            Collections.emptyList(), "查询失败", 0);
    }

    @Override
    public Y9Page<Map<String, Object>> pageByUserHostIpAndSuccessAndUserNameLike(String userHostIp, String success,
        String userName, int page, int rows) {
        int startIndex = (page - 1) * rows;
        int endIndex = page * rows;
        List<Map<String, Object>> strList = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        // 构建查询条件
        BoolQuery.Builder builder = new BoolQuery.Builder();
        builder.must(m -> m.term(t -> t.field(Y9LogSearchConsts.SUCCESS).value(success)));
        builder.must(m -> m.term(t -> t.field(Y9LogSearchConsts.USER_HOST_IP).value(userHostIp)));
        builder.must(m -> m.wildcard(w -> w.field(Y9LogSearchConsts.USER_NAME).value("*" + userName + "*")));
        if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
            builder.must(m -> m.term(t -> t.field(Y9LogSearchConsts.TENANT_ID).value(tenantId)));
        }

        // 构建topHits子聚合
        Aggregation topHitsAgg = Aggregation.of(a -> a.topHits(t -> t.size(1).explain(true)));

        // 构建terms聚合并添加topHits作为子聚合 - 使用链式调用
        Aggregation termsAgg =
            Aggregation.of(a -> a.terms(TermsAggregation.of(t -> t.field(Y9LogSearchConsts.USER_NAME)))
                .aggregations("topHits", topHitsAgg));

        NativeQueryBuilder nativeBuilder = new NativeQueryBuilder();
        nativeBuilder.withQuery(builder.build()._toQuery());
        nativeBuilder.withAggregation("username-aggs", termsAgg);

        try {
            SearchHits<Y9LogUserLoginInfo> searchHits =
                elasticsearchOperations.search(nativeBuilder.build(), Y9LogUserLoginInfo.class, INDEX);
            if (searchHits.hasAggregations()) {
                ElasticsearchAggregations aggregations = (ElasticsearchAggregations)searchHits.getAggregations();
                var termsAggregation = aggregations.get("username-aggs");
                if (termsAggregation != null) {
                    Aggregate aggregate = termsAggregation.aggregation().getAggregate();
                    if (aggregate != null) {
                        List<? extends StringTermsBucket> stbList = aggregate.sterms().buckets().array();
                        int totalCount = stbList.size();
                        endIndex = Math.min(endIndex, totalCount);
                        for (int i = startIndex; i < endIndex; i++) {
                            Map<String, Object> map = new HashMap<>();
                            StringTermsBucket bucket = stbList.get(i);
                            long count = bucket.docCount();

                            // 从子聚合中获取topHits结果
                            var bucketAggs = bucket.aggregations();
                            if (bucketAggs != null && !bucketAggs.isEmpty()) {
                                var topHitsAggResult = bucketAggs.get("topHits");
                                if (topHitsAggResult != null) {
                                    Aggregate topHitsAggregate = topHitsAggResult;
                                    if (topHitsAggregate != null) {
                                        var topHit = topHitsAggregate.topHits().hits().hits().get(0);
                                        Y9LogUserLoginInfoDO y9LogUserLoginInfoDO =
                                            topHit.source().to(Y9LogUserLoginInfoDO.class);
                                        map.put(Y9LogSearchConsts.USER_ID, y9LogUserLoginInfoDO.getUserId());
                                        map.put(Y9LogSearchConsts.USER_NAME, y9LogUserLoginInfoDO.getUserName());
                                    }
                                }
                            }
                            map.put("serverCount", String.valueOf(count));
                            strList.add(map);
                        }
                        return Y9Page.success(page, (int)Math.ceil((float)totalCount / (float)rows), totalCount,
                            strList);
                    }
                }
            }
        } catch (ElasticsearchException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        int totalCount = 0;
        return Y9Page.failure(page, (int)Math.ceil((float)totalCount / (float)rows), totalCount,
            Collections.emptyList(), "查询失败", 0);
    }

    @Override
    public Y9Page<Y9LogUserLoginInfoDO> pageByUserHostIpLikeAndLoginTimeBetweenAndSuccess(String userHostIp,
        Date startTime, Date endTime, String success, int page, int rows) {
        Criteria criteria = new Criteria();
        if (StringUtils.isNotBlank(userHostIp)) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.USER_HOST_IP).startsWith(userHostIp));
        }
        if (StringUtils.isNotBlank(success)) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.SUCCESS).is(success));
        }
        if (startTime != null && endTime != null) {
            criteria.subCriteria(
                new Criteria(Y9LogSearchConsts.LOGIN_TIME).between(startTime.getTime(), endTime.getTime()));
        }
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.TENANT_ID).is(tenantId));
        }
        Query query = new CriteriaQuery(criteria).setPageable(PageRequest.of((page < 1) ? 0 : page - 1, rows))
            .addSort(Sort.by(Direction.DESC, Y9LogSearchConsts.LOGIN_TIME));
        SearchHits<Y9LogUserLoginInfo> searchHits =
            elasticsearchOperations.search(query, Y9LogUserLoginInfo.class, INDEX);

        List<Y9LogUserLoginInfoDO> list = searchHits.stream()
            .map(SearchHit::getContent)
            .map(l -> Y9ModelConvertUtil.convert(l, Y9LogUserLoginInfoDO.class))
            .collect(Collectors.toList());
        int totalPages = (int)searchHits.getTotalHits() / rows;
        return Y9Page.success(page, searchHits.getTotalHits() % rows == 0 ? totalPages : totalPages + 1,
            searchHits.getTotalHits(), list);
    }

    @Override
    public Y9Page<Y9LogUserLoginInfoDO> searchQuery(String tenantId, String managerLevel, LoginLogQuery loginInfoModel,
        int page, int rows) {
        Pageable pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.by(Direction.DESC, Y9LogSearchConsts.LOGIN_TIME));
        Criteria criteria = new Criteria();
        if (StringUtils.isNotBlank(tenantId) && !tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.TENANT_ID).is(tenantId));
        }
        if (StringUtils.isNotBlank(managerLevel)) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.MANAGER_LEVEL).is(managerLevel));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getUserName())) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.USER_NAME).contains(loginInfoModel.getUserName()));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getUserHostIp())) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.USER_HOST_IP).contains(loginInfoModel.getUserHostIp()));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getOsName())) {
            criteria.subCriteria(new Criteria("osName").contains(loginInfoModel.getOsName()));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getScreenResolution())) {
            criteria.subCriteria(new Criteria("screenResolution").contains(loginInfoModel.getScreenResolution()));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getSuccess())) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.SUCCESS).is(loginInfoModel.getSuccess()));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getBrowserName())) {
            criteria.subCriteria(new Criteria("browserName").contains(loginInfoModel.getSuccess()));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getBrowserVersion())) {
            criteria.subCriteria(new Criteria("browserVersion").contains(loginInfoModel.getBrowserVersion()));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getStartTime())
            && StringUtils.isNotBlank(loginInfoModel.getEndTime())) {
            try {
                Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(loginInfoModel.getStartTime());
                Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(loginInfoModel.getEndTime());
                criteria.subCriteria(new Criteria(Y9LogSearchConsts.LOGIN_TIME).between(startDate, endDate));
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        SearchHits<Y9LogUserLoginInfo> searchHits =
            elasticsearchOperations.search(query, Y9LogUserLoginInfo.class, INDEX);

        List<Y9LogUserLoginInfoDO> list = searchHits.stream()
            .map(SearchHit::getContent)
            .map(l -> Y9ModelConvertUtil.convert(l, Y9LogUserLoginInfoDO.class))
            .collect(Collectors.toList());
        int totalPages = (int)searchHits.getTotalHits() / rows;
        return Y9Page.success(page, searchHits.getTotalHits() % rows == 0 ? totalPages : totalPages + 1,
            searchHits.getTotalHits(), list);
    }

    @Override
    public Integer countByUserId(String personId) {
        return y9logUserLoginInfoRepository.countByUserId(personId);
    }

    @Override
    public Y9LogUserLoginInfoDO findTopByTenantIdAndUserIdOrderByLoginTimeDesc(String tenantId, String userId) {
        Y9LogUserLoginInfo y9LogUserLoginInfo =
            y9logUserLoginInfoRepository.findTopByTenantIdAndUserIdOrderByLoginTimeDesc(tenantId, userId);
        return Y9ModelConvertUtil.convert(y9LogUserLoginInfo, Y9LogUserLoginInfoDO.class);
    }

    @Override
    public List<Y9LogUserLoginInfoDO> findAll() {
        return StreamSupport.stream(y9logUserLoginInfoRepository.findAll().spliterator(), false)
            .map(l -> Y9ModelConvertUtil.convert(l, Y9LogUserLoginInfoDO.class))
            .collect(Collectors.toList());
    }

    @Override
    public Set<Y9LogUserLoginInfoDO> findByUserIdAndSuccess(String userId, String success) {
        return y9logUserLoginInfoRepository.findByUserIdAndSuccess(userId, success)
            .stream()
            .map(l -> Y9ModelConvertUtil.convert(l, Y9LogUserLoginInfoDO.class))
            .collect(Collectors.toSet());
    }

    @Override
    public Page<Y9LogUserLoginInfoDO> findByTenantIdAndSuccessAndUserHostIpAndUserId(String tenantId, String success,
        String userHostIp, String userId, Pageable pageable) {
        Page<Y9LogUserLoginInfo> page = y9logUserLoginInfoRepository
            .findByTenantIdAndSuccessAndUserHostIpAndUserId(tenantId, success, userHostIp, userId, pageable);
        return poPageToDoPage(page);
    }

    @Override
    public Page<Y9LogUserLoginInfoDO> findBySuccessAndUserHostIpAndUserId(String success, String userHostIp,
        String userId, Pageable pageable) {
        Page<Y9LogUserLoginInfo> page =
            y9logUserLoginInfoRepository.findBySuccessAndUserHostIpAndUserId(success, userHostIp, userId, pageable);
        return poPageToDoPage(page);
    }

    @Override
    public Page<Y9LogUserLoginInfoDO> findByTenantIdAndManagerLevel(String tenantId, String managerLevel,
        Pageable pageable) {
        Page<Y9LogUserLoginInfo> page =
            y9logUserLoginInfoRepository.findByTenantIdAndManagerLevel(tenantId, managerLevel, pageable);
        return poPageToDoPage(page);
    }

    @Override
    public void save(Y9LogUserLoginInfoDO y9LogUserLoginInfoDO) {
        Y9LogUserLoginInfo y9LogUserLoginInfo =
            Y9ModelConvertUtil.convert(y9LogUserLoginInfoDO, Y9LogUserLoginInfo.class);
        y9logUserLoginInfoRepository.save(y9LogUserLoginInfo);
    }

}
