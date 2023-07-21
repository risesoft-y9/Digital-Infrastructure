package net.risesoft.log.service.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.TopHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.log.constant.Y9ESIndexConst;
import net.risesoft.log.constant.Y9LogSearchConsts;
import net.risesoft.log.entity.Y9logUserLoginInfo;
import net.risesoft.log.repository.Y9logUserLoginInfoRepository;
import net.risesoft.log.service.Y9logUserLoginInfoService;
import net.risesoft.model.log.LogInfoModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.y9.util.Y9Util;

/**
 *
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class Y9logUserLoginInfoServiceImpl implements Y9logUserLoginInfoService {

    private final RestHighLevelClient elasticsearchRestHighLevelClient;
    private final Y9logUserLoginInfoRepository y9logUserLoginInfoRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public long countByLoginTimeBetweenAndSuccess(Date startTime, Date endTime, String success) {
        IndexCoordinates index = IndexCoordinates.of(Y9ESIndexConst.LOGIN_INFO_INDEX);
        BoolQueryBuilder builder = QueryBuilders.boolQuery();

        if (StringUtils.isNotBlank(success)) {
            builder.must(QueryBuilders.queryStringQuery(success).field(Y9LogSearchConsts.SUCCESS));
        }
        builder.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOGIN_TIME).from(startTime.getTime()).to(endTime.getTime()));
        NativeSearchQuery query = new NativeSearchQueryBuilder().withQuery(builder).build();
        return elasticsearchOperations.count(query, index);
    }

    @Override
    public Integer countByPersonId(String personId) {
        String parserPersonId = Y9Util.escape(personId);
        List<Y9logUserLoginInfo> list = y9logUserLoginInfoRepository.findByUserId(parserPersonId);
        return list.size();
    }

    @Override
    public long countBySuccessAndUserHostIpAndUserId(String success, String userHostIp, String userId) {
        IndexCoordinates index = IndexCoordinates.of(Y9ESIndexConst.LOGIN_INFO_INDEX);
        BoolQueryBuilder builder = QueryBuilders.boolQuery();

        if (StringUtils.isNotBlank(success)) {
            builder.must(QueryBuilders.queryStringQuery(success).field(Y9LogSearchConsts.SUCCESS));
        }
        if (StringUtils.isNotBlank(userHostIp)) {
            String parserUserHostIp = Y9Util.escape(userHostIp);
            builder.must(QueryBuilders.queryStringQuery(parserUserHostIp).field(Y9LogSearchConsts.USER_HOST_IP));
        }
        if (StringUtils.isNotBlank(userId)) {
            String parserUserId = Y9Util.escape(userId);
            builder.must(QueryBuilders.queryStringQuery(parserUserId).field(Y9LogSearchConsts.USER_ID));
        }
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(builder).build();
        return elasticsearchOperations.count(nativeSearchQuery, index);
    }

    @Override
    public long countByUserHostIpAndSuccess(String userHostIp, String success) {
        long count = 0L;
        SearchRequest searchRequest = new SearchRequest(Y9ESIndexConst.LOGIN_INFO_INDEX);
        searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder builder = QueryBuilders.boolQuery().must(QueryBuilders.termQuery(Y9LogSearchConsts.USER_HOST_IP, userHostIp)).must(QueryBuilders.termQuery(Y9LogSearchConsts.SUCCESS, success));
        searchSourceBuilder.from(0).size(1000000).aggregation(AggregationBuilders.terms("aggs").field(Y9LogSearchConsts.USER_NAME).subAggregation(AggregationBuilders.topHits("top").size(1)).size(100)).query(builder).sort(Y9LogSearchConsts.LOGIN_TIME, SortOrder.DESC).explain(true);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = elasticsearchRestHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Terms aggs = searchResponse.getAggregations().get("aggs");
            count = aggs.getBuckets().size();
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return count;
    }

    @Override
    public long countByUserHostIpAndSuccessAndUserName(String userHostIp, String success, String userName) {
        return y9logUserLoginInfoRepository.countByUserHostIpAndSuccessAndUserName(userHostIp, success, userName);
    }

    @Override
    public long countByUserHostIpLikeAndLoginTimeBetweenAndSuccess(String userHostIp, Date startTime, Date endTime, String success) {
        IndexCoordinates index = IndexCoordinates.of(Y9ESIndexConst.LOGIN_INFO_INDEX);
        BoolQueryBuilder builder = QueryBuilders.boolQuery();

        if (StringUtils.isNotBlank(userHostIp)) {
            builder.must(QueryBuilders.wildcardQuery(Y9LogSearchConsts.USER_HOST_IP, userHostIp + "*"));
        }
        if (StringUtils.isNotBlank(success)) {
            builder.must(QueryBuilders.queryStringQuery(success).field(Y9LogSearchConsts.SUCCESS));
        }
        builder.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOGIN_TIME).from(startTime.getTime()).to(endTime.getTime()));
        NativeSearchQuery query = new NativeSearchQueryBuilder().withQuery(builder).build();

        return elasticsearchOperations.count(query, index);
    }

    @Override
    public Iterable<Y9logUserLoginInfo> listAll() {
        return y9logUserLoginInfoRepository.findAll();
    }

    @Override
    public List<Object[]> listDistinctUserHostIpByUserIdAndLoginTime(String userId, Date startTime, Date endTime) {
        List<Object[]> strList = new ArrayList<>();
        try {
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            SearchRequest searchRequest = new SearchRequest(Y9ESIndexConst.LOGIN_INFO_INDEX);
            searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
            AggregationBuilder aggregation = AggregationBuilders.terms("aggs").field(Y9LogSearchConsts.USER_HOST_IP).subAggregation(AggregationBuilders.topHits("top").size(1)).size(1000000);
            BoolQueryBuilder builder = QueryBuilders.boolQuery().must(QueryBuilders.termQuery(Y9LogSearchConsts.USER_ID, userId)).must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOGIN_TIME).from(startTime.getTime()).to(endTime.getTime()));
            searchSourceBuilder.query(builder).aggregation(aggregation).sort(Y9LogSearchConsts.LOGIN_TIME, SortOrder.DESC).explain(true);
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = elasticsearchRestHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Terms aggs = searchResponse.getAggregations().get("aggs");
            if (aggs.getBuckets().size() > 0) {
                for (int i = 0; i < aggs.getBuckets().size(); i++) {
                    Terms.Bucket entry = aggs.getBuckets().get(i);
                    long count = entry.getDocCount();
                    TopHits topHits = entry.getAggregations().get("top");
                    for (SearchHit hit : topHits.getHits().getHits()) {
                        Map<String, Object> sourceMap = hit.getSourceAsMap();
                        String userHostIp = (String)sourceMap.get(Y9LogSearchConsts.USER_HOST_IP);
                        String[] userLoginInfo = {userHostIp, String.valueOf(count)};
                        strList.add(userLoginInfo);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return strList;
    }

    @Override
    public List<Map<String, Object>> listUserHostIpByCip(String cip) {
        List<Map<String, Object>> list = new ArrayList<>();
        SearchResponse searchResponse = null;
        SearchRequest searchRequest = new SearchRequest().indices(Y9ESIndexConst.LOGIN_INFO_INDEX).searchType(SearchType.DFS_QUERY_THEN_FETCH);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery(Y9LogSearchConsts.USER_HOST_IP, cip + "*");
        boolQueryBuilder.must(wildcardQueryBuilder);
        sourceBuilder.query(boolQueryBuilder);
        // 聚合
        TermsAggregationBuilder aggregation = AggregationBuilders.terms("by_UserHostIP").field(Y9LogSearchConsts.USER_HOST_IP).subAggregation(AggregationBuilders.topHits("top").size(1));
        sourceBuilder.aggregation(aggregation);

        searchRequest.source(sourceBuilder);
        try {
            searchResponse = elasticsearchRestHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        if (searchResponse != null) {
            Terms userHostIpTerms = searchResponse.getAggregations().get("by_UserHostIP");
            userHostIpTerms.getBuckets().forEach(bucket -> {
                Map<String, Object> map = new HashMap<>();
                String serverIp = bucket.getKeyAsString();
                String text = serverIp + "<span style='color:red'>(" + bucket.getDocCount() + ")</span>";
                map.put("serverIp", serverIp);
                map.put("text", text);
                list.add(map);
            });
        }
        return list;
    }

    @Override
    public List<String> listUserHostIpByUserId(String userId, String success) {
        String parserUserId = Y9Util.escape(userId);
        Set<Y9logUserLoginInfo> list = y9logUserLoginInfoRepository.findByUserIdAndSuccess(parserUserId, success);
        Set<String> userHostIpSet = list.stream().map(Y9logUserLoginInfo::getUserHostIp).collect(Collectors.toSet());
        List<String> userHostIpList = new ArrayList<>();
        userHostIpList.addAll(userHostIpSet);
        return userHostIpList;
    }

    @Override
    public Y9Page<Y9logUserLoginInfo> page(String userHostIp, String userId, String success, String startTime, String endTime, int page, int rows) {
        IndexCoordinates index = IndexCoordinates.of(Y9ESIndexConst.LOGIN_INFO_INDEX);

        String parserUserId = Y9Util.escape(userId);
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(userHostIp)) {
            String parserUserHostIp = Y9Util.escape(userHostIp);
            builder.must(QueryBuilders.queryStringQuery(parserUserHostIp).field(Y9LogSearchConsts.USER_HOST_IP));
        }
        if (StringUtils.isNotBlank(parserUserId)) {
            builder.must(QueryBuilders.queryStringQuery(parserUserId).field(Y9LogSearchConsts.USER_ID));
        }
        if (StringUtils.isNotBlank(success)) {
            builder.must(QueryBuilders.queryStringQuery(success).field(Y9LogSearchConsts.SUCCESS));
        }
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                builder.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOGIN_TIME).from(simpleDateFormat.parse(startTime).getTime()).to(simpleDateFormat.parse(endTime).getTime()));
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(builder).withPageable(PageRequest.of((page < 1) ? 0 : page - 1, rows)).withSort(SortBuilders.fieldSort(Y9LogSearchConsts.LOGIN_TIME).order(SortOrder.DESC)).build();
        SearchHits<Y9logUserLoginInfo> searchHits = elasticsearchOperations.search(nativeSearchQuery, Y9logUserLoginInfo.class, index);

        List<Y9logUserLoginInfo> list = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        int totalPages = (int)searchHits.getTotalHits() / rows;
        return Y9Page.success(page, searchHits.getTotalHits() % rows == 0 ? totalPages : totalPages + 1, searchHits.getTotalHits(), list);
    }

    @Override
    public Page<Y9logUserLoginInfo> pageBySuccessAndServerIpAndUserName(String success, String userHostIp, String userId, int page, int rows) {
        String parserUserId = Y9Util.escape(userId);
        String parserUserHostIp = Y9Util.escape(userHostIp);
        Pageable pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.by(Sort.Direction.DESC, Y9LogSearchConsts.LOGIN_TIME));
        Page<Y9logUserLoginInfo> userLoginInfoPage = y9logUserLoginInfoRepository.findBySuccessAndUserHostIpAndUserId(success, parserUserHostIp, parserUserId, pageable);
        return userLoginInfoPage;
    }

    @Override
    public Page<Y9logUserLoginInfo> pageByTenantIdAndManagerLevel(String tenantId, String managerLevel, int page, int rows) {
        Pageable pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.by(Sort.Direction.DESC, Y9LogSearchConsts.LOGIN_TIME));
        Page<Y9logUserLoginInfo> userLoginInfoPage = y9logUserLoginInfoRepository.findByTenantIdAndManagerLevel(tenantId, managerLevel, pageable);
        return userLoginInfoPage;
    }

    @Override
    public Y9Page<Map<String, Object>> pageByUserHostIpAndSuccess(String userHostIp, String success, int page, int rows) {
        int startIndex = (page - 1) * rows;
        int endIndex = page * rows;
        List<Map<String, Object>> strList = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest(Y9ESIndexConst.LOGIN_INFO_INDEX);
        searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 查询条件
        BoolQueryBuilder builder = QueryBuilders.boolQuery().must(QueryBuilders.termQuery(Y9LogSearchConsts.USER_HOST_IP, userHostIp)).must(QueryBuilders.termQuery(Y9LogSearchConsts.SUCCESS, success));
        // 聚合
        AggregationBuilder aggregation = AggregationBuilders.terms("aggs").field(Y9LogSearchConsts.USER_NAME).subAggregation(AggregationBuilders.topHits("top").size(1));
        searchSourceBuilder.from(startIndex).size(endIndex).aggregation(aggregation).query(builder).sort(Y9LogSearchConsts.LOGIN_TIME, SortOrder.DESC).explain(true);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        long totalCount = 0;
        try {
            searchResponse = elasticsearchRestHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        if (searchResponse != null) {
            totalCount = searchResponse.getHits().getHits().length;

            Terms aggs = searchResponse.getAggregations().get("aggs");
            aggs.getBuckets().forEach(bucket -> {
                long count = bucket.getDocCount();

                TopHits topHits = bucket.getAggregations().get("top");
                topHits.getHits().forEach(hit -> {
                    Map<String, Object> sourceMap = hit.getSourceAsMap();
                    String userId = (String)sourceMap.get(Y9LogSearchConsts.USER_ID);
                    String userName = (String)sourceMap.get(Y9LogSearchConsts.USER_NAME);
                    Map<String, Object> map = new HashMap<>();
                    map.put(Y9LogSearchConsts.USER_ID, userId);
                    map.put(Y9LogSearchConsts.USER_NAME, userName);
                    map.put("serverCount", String.valueOf(count));
                    strList.add(map);
                });
            });
        }
        return Y9Page.success(page, (int)Math.ceil((float)totalCount / (float)page), totalCount, strList);
    }

    @Override
    public Y9Page<Map<String, Object>> pageByUserHostIpAndSuccessAndUserNameLike(String userHostIp, String success, String userName, int page, int rows) {
        int startIndex = (page - 1) * rows;
        int endIndex = page * rows;
        List<Map<String, Object>> strList = new ArrayList<>();
        Map<String, Object> pageMap = new HashMap<>();

        SearchRequest searchRequest = new SearchRequest(Y9ESIndexConst.LOGIN_INFO_INDEX);
        searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder builder = QueryBuilders.boolQuery().must(QueryBuilders.termQuery(Y9LogSearchConsts.USER_HOST_IP, userHostIp)).must(QueryBuilders.termQuery(Y9LogSearchConsts.SUCCESS, success)).must(QueryBuilders.wildcardQuery(Y9LogSearchConsts.USER_NAME, "*" + userName + "*"));
        searchSourceBuilder.from(0).size(1000000).aggregation(AggregationBuilders.terms("aggs").field(Y9LogSearchConsts.USER_NAME).subAggregation(AggregationBuilders.topHits("top").size(1)).size(100)).query(builder).sort(Y9LogSearchConsts.LOGIN_TIME, SortOrder.DESC).explain(true);
        searchRequest.source(searchSourceBuilder);
        int totalCount = 0;
        SearchResponse searchResponse = null;
        try {
            searchResponse = elasticsearchRestHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        if (searchResponse != null) {
            Terms aggs = searchResponse.getAggregations().get("aggs");
            if (aggs.getBuckets().size() > startIndex) {
                totalCount = aggs.getBuckets().size();
                endIndex = endIndex < aggs.getBuckets().size() ? endIndex : aggs.getBuckets().size();
                for (int i = startIndex; i < endIndex; i++) {
                    Terms.Bucket entry = aggs.getBuckets().get(i);
                    long count = entry.getDocCount();
                    TopHits topHits = entry.getAggregations().get("top");
                    for (SearchHit hit : topHits.getHits().getHits()) {
                        Map<String, Object> sourceMap = hit.getSourceAsMap();
                        String userId = (String)sourceMap.get(Y9LogSearchConsts.USER_ID);
                        String name = (String)sourceMap.get(Y9LogSearchConsts.USER_NAME);
                        Map<String, Object> map = new HashMap<>();
                        map.put(Y9LogSearchConsts.USER_ID, userId);
                        map.put(Y9LogSearchConsts.USER_NAME, name);
                        map.put("serverCount", String.valueOf(count));
                        strList.add(map);
                    }
                }
                pageMap.put("totalCount", totalCount);
                pageMap.put("strList", strList);
            }
        }
        return Y9Page.success(page, (int)Math.ceil((float)totalCount / (float)page), totalCount, strList);
    }

    @Override
    public Y9Page<Y9logUserLoginInfo> pageByUserHostIpAndUserIdAndTenantIdAndLoginTime(String userHostIp, String userId, String tenantId, String success, String startTime, String endTime, int page, int rows) {
        IndexCoordinates index = IndexCoordinates.of(Y9ESIndexConst.LOGIN_INFO_INDEX);
        Pageable pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.by(Sort.Direction.ASC, Y9LogSearchConsts.LOGIN_TIME));
        BoolQueryBuilder builder = QueryBuilders.boolQuery();

        if (StringUtils.isNotBlank(userHostIp)) {
            builder.must(QueryBuilders.queryStringQuery(userHostIp).field(Y9LogSearchConsts.USER_HOST_IP));
        }
        if (StringUtils.isNotBlank(userId)) {
            builder.must(QueryBuilders.queryStringQuery(userId).field(Y9LogSearchConsts.USER_ID));
        }
        if (StringUtils.isNotBlank(tenantId)) {
            builder.must(QueryBuilders.queryStringQuery(tenantId).field(Y9LogSearchConsts.TENANT_ID));
        }
        if (StringUtils.isNotBlank(success)) {
            builder.must(QueryBuilders.queryStringQuery(success).field(Y9LogSearchConsts.SUCCESS));
        }
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                builder.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOGIN_TIME).from(simpleDateFormat.parse(startTime).getTime()).to(simpleDateFormat.parse(endTime).getTime()));
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(builder).withPageable(pageable).build();
        SearchHits<Y9logUserLoginInfo> searchHits = elasticsearchOperations.search(searchQuery, Y9logUserLoginInfo.class, index);
        List<Y9logUserLoginInfo> list = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        int totalPages = (int)searchHits.getTotalHits() / rows;
        return Y9Page.success(page, searchHits.getTotalHits() % rows == 0 ? totalPages : totalPages + 1, searchHits.getTotalHits(), list);
    }

    @Override
    public void save(Y9logUserLoginInfo y9logUserLoginInfo) {
        y9logUserLoginInfoRepository.save(y9logUserLoginInfo);
    }

    @Override
    public Y9Page<Y9logUserLoginInfo> search(Date startTime, Date endTime, String success, int page, int rows) {
        IndexCoordinates index = IndexCoordinates.of(Y9ESIndexConst.LOGIN_INFO_INDEX);

        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(success)) {
            builder.must(QueryBuilders.queryStringQuery(success).field(Y9LogSearchConsts.SUCCESS));
        }
        if (startTime != null && endTime != null) {
            builder.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOGIN_TIME).from(startTime.getTime()).to(endTime.getTime()));
        }

        NativeSearchQuery query = new NativeSearchQueryBuilder().withQuery(builder).withPageable(PageRequest.of((page < 1) ? 0 : page - 1, rows)).withSort(SortBuilders.fieldSort(Y9LogSearchConsts.LOGIN_TIME).order(SortOrder.DESC)).build();
        SearchHits<Y9logUserLoginInfo> searchHits = elasticsearchOperations.search(query, Y9logUserLoginInfo.class, index);
        List<Y9logUserLoginInfo> list = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        int totalPages = (int)searchHits.getTotalHits() / rows;
        return Y9Page.success(page, searchHits.getTotalHits() % rows == 0 ? totalPages : totalPages + 1, searchHits.getTotalHits(), list);
    }

    @Override
    public Y9Page<Y9logUserLoginInfo> search(String userHostIp, Date startTime, Date endTime, String success, int page, int rows) {
        IndexCoordinates index = IndexCoordinates.of(Y9ESIndexConst.LOGIN_INFO_INDEX);

        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(userHostIp)) {
            builder.must(QueryBuilders.wildcardQuery(Y9LogSearchConsts.USER_HOST_IP, userHostIp + "*"));
        }
        if (StringUtils.isNotBlank(success)) {
            builder.must(QueryBuilders.queryStringQuery(success).field(Y9LogSearchConsts.SUCCESS));
        }
        if (startTime != null && endTime != null) {
            builder.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOGIN_TIME).from(startTime.getTime()).to(endTime.getTime()));
        }
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(builder).withPageable(PageRequest.of((page < 1) ? 0 : page - 1, rows)).withSort(SortBuilders.fieldSort(Y9LogSearchConsts.LOGIN_TIME).order(SortOrder.DESC)).build();
        SearchHits<Y9logUserLoginInfo> searchHits = elasticsearchOperations.search(searchQuery, Y9logUserLoginInfo.class, index);

        List<Y9logUserLoginInfo> list = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        int totalPages = (int)searchHits.getTotalHits() / rows;
        return Y9Page.success(page, searchHits.getTotalHits() % rows == 0 ? totalPages : totalPages + 1, searchHits.getTotalHits(), list);
    }

    @Override
    public Y9Page<Y9logUserLoginInfo> searchQuery(String tenantId, String managerLevel, LogInfoModel loginInfoModel, int page, int rows) {
        IndexCoordinates index = IndexCoordinates.of(Y9ESIndexConst.LOGIN_INFO_INDEX);
        Pageable pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.by(Sort.Direction.DESC, Y9LogSearchConsts.LOGIN_TIME));
        BoolQueryBuilder builder = QueryBuilders.boolQuery();

        if (StringUtils.isNotBlank(tenantId)) {
            builder.must(QueryBuilders.queryStringQuery(tenantId).field(Y9LogSearchConsts.TENANT_ID));
        }
        if (StringUtils.isNotBlank(managerLevel)) {
            builder.must(QueryBuilders.queryStringQuery(managerLevel).field("managerLevel"));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getUserName())) {
            builder.must(QueryBuilders.boolQuery().should(QueryBuilders.wildcardQuery(Y9LogSearchConsts.USER_NAME, "*" + loginInfoModel.getUserName() + "*")));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getUserHostIp())) {
            builder.must(QueryBuilders.boolQuery().should(QueryBuilders.wildcardQuery(Y9LogSearchConsts.USER_HOST_IP, "*" + loginInfoModel.getUserHostIp() + "*")));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getOsName())) {
            builder.must(QueryBuilders.boolQuery().should(QueryBuilders.wildcardQuery("osName", "*" + loginInfoModel.getOsName() + "*")));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getScreenResolution())) {
            builder.must(QueryBuilders.boolQuery().should(QueryBuilders.wildcardQuery("screenResolution", "*" + loginInfoModel.getScreenResolution() + "*")));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getSuccess())) {
            builder.must(QueryBuilders.queryStringQuery(loginInfoModel.getSuccess()).field(Y9LogSearchConsts.SUCCESS));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getBrowserName())) {
            builder.must(QueryBuilders.boolQuery().should(QueryBuilders.wildcardQuery("browserName", "*" + loginInfoModel.getBrowserName() + "*")));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getBrowserVersion())) {
            builder.must(QueryBuilders.boolQuery().should(QueryBuilders.wildcardQuery("browserVersion", "*" + loginInfoModel.getBrowserVersion() + "*")));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getStartTime()) && StringUtils.isNotBlank(loginInfoModel.getEndTime())) {
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
                builder.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOGIN_TIME).from(s).to(e).format("yyyy-MM-dd'T'HH:mm:ss'Z'"));
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(builder).withPageable(pageable).build();
        SearchHits<Y9logUserLoginInfo> searchHits = elasticsearchOperations.search(searchQuery, Y9logUserLoginInfo.class, index);
        List<Y9logUserLoginInfo> list = searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        int totalPages = (int)searchHits.getTotalHits() / rows;
        return Y9Page.success(page, searchHits.getTotalHits() % rows == 0 ? totalPages : totalPages + 1, searchHits.getTotalHits(), list);
    }
}
