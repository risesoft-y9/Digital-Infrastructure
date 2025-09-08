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

import net.risesoft.log.repository.Y9logUserLoginInfoCustomRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.CriteriaQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.log.constant.Y9ESIndexConst;
import net.risesoft.log.constant.Y9LogSearchConsts;
import net.risesoft.log.domain.Y9LogUserLoginInfoDO;
import net.risesoft.model.log.LogInfoModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9.util.Y9Util;
import net.risesoft.y9public.entity.Y9LogUserLoginInfo;
import net.risesoft.y9public.repository.Y9LogUserLoginInfoRepository;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery.Builder;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;

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
        Query query = new CriteriaQueryBuilder(criteria).build();
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
        List<Map<String, Object>> list = new ArrayList<>();
        Builder build = new Builder();
        build.must(m -> m.queryString(qs -> qs.fields(Y9LogSearchConsts.USER_HOST_IP).query(cip + "*")));
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
            build.must(m -> m.queryString(qs -> qs.fields(Y9LogSearchConsts.TENANT_ID).query(tenantId)));
        }

        Aggregation userHostIpAggs = Aggregation.of(a -> a.terms(t -> t.field(Y9LogSearchConsts.USER_HOST_IP)));

        NativeQueryBuilder querybuilder = new NativeQueryBuilder();
        querybuilder.withQuery(build.build()._toQuery());
        querybuilder.withAggregation("by_UserHostIP", userHostIpAggs);
        querybuilder.withTrackTotalHits(true);
        try {
            SearchHits<Y9LogUserLoginInfo> searchHits =
                elasticsearchOperations.search(querybuilder.build(), Y9LogUserLoginInfo.class, INDEX);
            ElasticsearchAggregations aggregations = (ElasticsearchAggregations)searchHits.getAggregations();
            List<StringTermsBucket> ipList =
                aggregations.get("by_UserHostIP").aggregation().getAggregate().sterms().buckets().array();

            ipList.forEach(bucket -> {
                Map<String, Object> map = new HashMap<>();
                String serverIp = bucket.key().stringValue();
                String text = serverIp + "<span style='color:red'>(" + bucket.docCount() + ")</span>";
                map.put("serverIp", serverIp);
                map.put("text", text);
                list.add(map);
            });
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
                criteria.subCriteria(new Criteria(Y9LogSearchConsts.LOGIN_TIME)
                    .between(simpleDateFormat.parse(startTime).getTime(), simpleDateFormat.parse(endTime).getTime()));
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
        Builder build = new Builder();
        build.must(m -> m.term(t -> t.field(Y9LogSearchConsts.SUCCESS).value(success)));
        build.must(m -> m.term(t -> t.field(Y9LogSearchConsts.USER_HOST_IP).value(userHostIp)));
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
            build.must(m -> m.queryString(qs -> qs.fields(Y9LogSearchConsts.TENANT_ID).query(tenantId)));
        }

        Aggregation usernameAggs = Aggregation.of(a -> a.terms(t -> t.field(Y9LogSearchConsts.USER_NAME))
            .aggregations("topHits", aggs -> aggs.topHits(h -> h.size(1).explain(true))));

        NativeQueryBuilder querybuilder = new NativeQueryBuilder();
        querybuilder.withQuery(build.build()._toQuery());
        querybuilder.withAggregation("username-aggs", usernameAggs);
        querybuilder.withTrackTotalHits(true);
        try {
            SearchHits<Y9LogUserLoginInfo> searchHits =
                elasticsearchOperations.search(querybuilder.build(), Y9LogUserLoginInfo.class, INDEX);
            ElasticsearchAggregations aggregations = (ElasticsearchAggregations)searchHits.getAggregations();
            List<StringTermsBucket> stbList =
                aggregations.get("username-aggs").aggregation().getAggregate().sterms().buckets().array();
            int totalCount = stbList.size();
            endIndex = endIndex < totalCount ? endIndex : totalCount;
            for (int i = startIndex; i < endIndex; i++) {
                Map<String, Object> map = new HashMap<>();
                StringTermsBucket bucket = stbList.get(i);
                long count = bucket.docCount();
                Y9LogUserLoginInfo y9logUserLoginInfo = bucket.aggregations().get("topHits").topHits().hits().hits()
                    .get(0).source().to(Y9LogUserLoginInfo.class);
                map.put(Y9LogSearchConsts.USER_ID, y9logUserLoginInfo.getUserId());
                map.put(Y9LogSearchConsts.USER_NAME, y9logUserLoginInfo.getUserName());
                map.put("serverCount", String.valueOf(count));
                strList.add(map);
            }
            return Y9Page.success(page, (int)Math.ceil((float)totalCount / (float)page), totalCount, strList);
        } catch (ElasticsearchException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return Y9Page.failure(page, page, startIndex, null, tenantId, endIndex);
    }

    @Override
    public Y9Page<Map<String, Object>> pageByUserHostIpAndSuccessAndUserNameLike(String userHostIp, String success,
        String userName, int page, int rows) {
        int endIndex = page * rows;
        List<Map<String, Object>> strList = new ArrayList<>();
        Builder build = new Builder();
        build.must(m -> m.term(t -> t.field(Y9LogSearchConsts.SUCCESS).value(success)));
        build.must(m -> m.term(t -> t.field(Y9LogSearchConsts.USER_HOST_IP).value(userHostIp)));
        build.must(m -> m.term(t -> t.field(Y9LogSearchConsts.USER_NAME).value("*" + userName + "*")));
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
            build.must(m -> m.queryString(qs -> qs.fields(Y9LogSearchConsts.TENANT_ID).query(tenantId)));
        }

        Aggregation appAggs = Aggregation.of(a -> a.terms(t -> t.field(Y9LogSearchConsts.USER_NAME))
            .aggregations("topHits", aggs -> aggs.topHits(h -> h.size(1).explain(true))));

        NativeQueryBuilder querybuilder = new NativeQueryBuilder();
        querybuilder.withQuery(build.build()._toQuery());
        querybuilder.withAggregation("userNameAggs", appAggs);
        querybuilder.withTrackTotalHits(true);

        try {
            SearchHits<Y9LogUserLoginInfo> searchHits =
                elasticsearchOperations.search(querybuilder.build(), Y9LogUserLoginInfo.class, INDEX);

            ElasticsearchAggregations aggregations = (ElasticsearchAggregations)searchHits.getAggregations();
            // 指定聚合的名称
            ElasticsearchAggregation userNameAggs = aggregations.get("userNameAggs");
            // 获得聚合
            Aggregate aggregate = userNameAggs.aggregation().getAggregate();

            List<StringTermsBucket> list = aggregate.sterms().buckets().array();
            int totalCount = list.size();
            endIndex = endIndex < totalCount ? endIndex : totalCount;
            for (StringTermsBucket bucket : list) {
                long count = bucket.docCount();
                List<Hit<JsonData>> topHitList = bucket.aggregations().get("topHits").topHits().hits().hits();
                for (Hit<JsonData> h : topHitList) {
                    Map<String, Object> map = new HashMap<>();
                    Y9LogUserLoginInfoDO y9LogUserLoginInfoDO = h.source().to(Y9LogUserLoginInfoDO.class);
                    map.put(Y9LogSearchConsts.USER_ID, y9LogUserLoginInfoDO.getUserId());
                    map.put(Y9LogSearchConsts.USER_NAME, y9LogUserLoginInfoDO.getUserName());
                    map.put("serverCount", String.valueOf(count));
                    strList.add(map);
                }
            }
            return Y9Page.success(page, (int)Math.ceil((float)totalCount / (float)page), totalCount, strList);
        } catch (ElasticsearchException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
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
    public Y9Page<Y9LogUserLoginInfoDO> searchQuery(String tenantId, String managerLevel, LogInfoModel loginInfoModel,
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

    private static PageImpl<Y9LogUserLoginInfoDO> poPageToDoPage(Page<Y9LogUserLoginInfo> userLoginInfoPage) {
        List<Y9LogUserLoginInfoDO> list = userLoginInfoPage.getContent()
            .stream()
            .map(l -> Y9ModelConvertUtil.convert(l, Y9LogUserLoginInfoDO.class))
            .collect(Collectors.toList());
        return new PageImpl<>(list, userLoginInfoPage.getPageable(), userLoginInfoPage.getTotalElements());
    }
}