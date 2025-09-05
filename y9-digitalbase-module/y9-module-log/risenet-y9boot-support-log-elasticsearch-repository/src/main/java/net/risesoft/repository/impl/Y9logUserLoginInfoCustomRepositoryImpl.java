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
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedTopHits;
import org.elasticsearch.search.aggregations.metrics.TopHitsAggregationBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.CriteriaQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.log.constant.Y9ESIndexConst;
import net.risesoft.log.constant.Y9LogSearchConsts;
import net.risesoft.log.domain.Y9LogUserLoginInfoDO;
import net.risesoft.log.repository.Y9logUserLoginInfoCustomRepository;
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
        String tenantId = Y9LoginUserHolder.getTenantId();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.wildcardQuery(Y9LogSearchConsts.USER_HOST_IP, cip + "*"));
        if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
            boolQueryBuilder.must(QueryBuilders.matchQuery(Y9LogSearchConsts.TENANT_ID, tenantId));
        }
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        builder.withQuery(boolQueryBuilder);
        builder.withAggregations(
            AggregationBuilders.terms("by_UserHostIP").field(Y9LogSearchConsts.USER_HOST_IP).size(Integer.MAX_VALUE));

        try {
            SearchHits<Y9LogUserLoginInfo> searchHits =
                elasticsearchOperations.search(builder.build(), Y9LogUserLoginInfo.class, INDEX);
            ElasticsearchAggregations aggregations = (ElasticsearchAggregations)searchHits.getAggregations();
            if (aggregations != null) {
                Terms terms = aggregations.aggregations().get("by_UserHostIP");
                List<? extends Terms.Bucket> buckets = terms.getBuckets();
                List<Map<String, Object>> list = new ArrayList<>();
                buckets.forEach(bucket -> {
                    Map<String, Object> map = new HashMap<>();
                    String serverIp = bucket.getKeyAsString();
                    String text = serverIp + "<span style='color:red'>(" + bucket.getDocCount() + ")</span>";
                    map.put("serverIp", serverIp);
                    map.put("text", text);
                    list.add(map);
                });
                return list;
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
        String tenantId = Y9LoginUserHolder.getTenantId();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery(Y9LogSearchConsts.SUCCESS, success));
        boolQueryBuilder.must(QueryBuilders.termQuery(Y9LogSearchConsts.USER_HOST_IP, userHostIp));
        if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
            boolQueryBuilder.must(QueryBuilders.matchQuery(Y9LogSearchConsts.TENANT_ID, tenantId));
        }
        TermsAggregationBuilder termsAggregation =
            AggregationBuilders.terms("username-aggs").field(Y9LogSearchConsts.USER_NAME).executionHint("map");
        TopHitsAggregationBuilder topHits = AggregationBuilders.topHits("topHits").size(1).explain(true);
        termsAggregation.subAggregation(topHits);

        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        builder.withQuery(boolQueryBuilder);
        builder.withAggregations(termsAggregation);

        SearchHits<Y9LogUserLoginInfo> searchHits =
            elasticsearchOperations.search(builder.build(), Y9LogUserLoginInfo.class, INDEX);
        ElasticsearchAggregations aggregations = (ElasticsearchAggregations)searchHits.getAggregations();
        if (aggregations != null) {
            Terms terms = aggregations.aggregations().get("username-aggs");
            List<? extends Terms.Bucket> stbList = terms.getBuckets();
            int totalCount = stbList.size();
            endIndex = Math.min(endIndex, totalCount);
            for (int i = startIndex; i < endIndex; i++) {
                Map<String, Object> map = new HashMap<>();
                Terms.Bucket bucket = stbList.get(i);
                long count = bucket.getDocCount();

                Aggregations topAggregations = bucket.getAggregations();
                ParsedTopHits topScoreResult = topAggregations.get("topHits");
                if (topScoreResult.getHits().getHits().length > 0) {
                    String loginInfoJson = topScoreResult.getHits().getAt(0).getSourceAsString();
                    try {
                        Y9LogUserLoginInfoDO y9LogUserLoginInfoDO =
                            Y9JsonUtil.objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"))
                                .readValue(loginInfoJson, Y9LogUserLoginInfoDO.class);
                        map.put(Y9LogSearchConsts.USER_ID, y9LogUserLoginInfoDO.getUserId());
                        map.put(Y9LogSearchConsts.USER_NAME, y9LogUserLoginInfoDO.getUserName());
                    } catch (JsonMappingException e) {
                        e.printStackTrace();
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                }
                map.put("serverCount", String.valueOf(count));
                strList.add(map);
            }
            return Y9Page.success(page, (int)Math.ceil((float)totalCount / (float)page), totalCount, strList);
        }
        return Y9Page.failure(page, page, startIndex, null, tenantId, endIndex);
    }

    @Override
    public Y9Page<Map<String, Object>> pageByUserHostIpAndSuccessAndUserNameLike(String userHostIp, String success,
        String userName, int page, int rows) {
        int startIndex = (page - 1) * rows;
        int endIndex = page * rows;
        List<Map<String, Object>> strList = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery(Y9LogSearchConsts.SUCCESS, success));
        boolQueryBuilder.must(QueryBuilders.termQuery(Y9LogSearchConsts.USER_HOST_IP, userHostIp));
        boolQueryBuilder.must(QueryBuilders.matchQuery(Y9LogSearchConsts.USER_NAME, "*" + userName + "*"));
        if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
            boolQueryBuilder.must(QueryBuilders.matchQuery(Y9LogSearchConsts.TENANT_ID, tenantId));
        }

        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        builder.withQuery(boolQueryBuilder);
        builder.withAggregations(AggregationBuilders.terms("username-aggs").field(Y9LogSearchConsts.USER_NAME),
            AggregationBuilders.topHits("topHits").size(1).explain(true));

        try {
            SearchHits<Y9LogUserLoginInfo> searchHits =
                elasticsearchOperations.search(builder.build(), Y9LogUserLoginInfo.class, INDEX);
            ElasticsearchAggregations aggregations = (ElasticsearchAggregations)searchHits.getAggregations();
            if (aggregations != null) {
                Terms terms = aggregations.aggregations().get("username-aggs");
                List<? extends Terms.Bucket> stbList = terms.getBuckets();
                int totalCount = stbList.size();
                endIndex = Math.min(endIndex, totalCount);
                for (int i = startIndex; i < endIndex; i++) {
                    Map<String, Object> map = new HashMap<>();
                    Terms.Bucket bucket = stbList.get(i);
                    long count = bucket.getDocCount();
                    Y9LogUserLoginInfoDO y9LogUserLoginInfoDO =
                        ((Aggregate)bucket.getAggregations().get("topHits")).topHits()
                            .hits()
                            .hits()
                            .get(0)
                            .source()
                            .to(Y9LogUserLoginInfoDO.class);
                    map.put(Y9LogSearchConsts.USER_ID, y9LogUserLoginInfoDO.getUserId());
                    map.put(Y9LogSearchConsts.USER_NAME, y9LogUserLoginInfoDO.getUserName());
                    map.put("serverCount", String.valueOf(count));
                    strList.add(map);
                }
                return Y9Page.success(page, (int)Math.ceil((float)totalCount / (float)page), totalCount, strList);
            }
        } catch (ElasticsearchException e) {
            LOGGER.warn(e.getMessage(), e);
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
