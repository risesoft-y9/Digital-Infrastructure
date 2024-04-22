package net.risesoft.y9public.repository.custom.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitsIterator;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.log.constant.Y9ESIndexConst;
import net.risesoft.log.constant.Y9LogSearchConsts;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;
import net.risesoft.y9public.entity.Y9ClickedApp;
import net.risesoft.y9public.entity.Y9CommonAppForPerson;
import net.risesoft.y9public.entity.Y9logAccessLog;
import net.risesoft.y9public.repository.Y9CommonAppForPersonRepository;
import net.risesoft.y9public.repository.custom.Y9CommonAppForPersonCustomRepository;

/**
 *
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class Y9CommonAppForPersonCustomRepositoryImpl implements Y9CommonAppForPersonCustomRepository {

    private static final String APP_MODULARNAME = "net.risesoft.controller.admin.WebsiteController.saveAppCheckCount";

    private final Y9CommonAppForPersonRepository commonAppForPersonRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final RestHighLevelClient restHighLevelClient;

    @Override
    public List<String> getAppNamesByPersonId(String personId) {
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(personId)) {
            query.must(QueryBuilders.queryStringQuery(Y9Util.escape(personId)).field("personId"));
            query.must(QueryBuilders.queryStringQuery(Y9Util.escape(Y9LoginUserHolder.getTenantId()))
                .field(Y9LogSearchConsts.TENANT_ID));
        }

        SearchRequest request = new SearchRequest(Y9ESIndexConst.CLICKED_APP_INDEX);
        SearchSourceBuilder searchSourceBuilder =
            new SearchSourceBuilder().aggregation(AggregationBuilders.terms("by_appName").field("appName").size(100000))
                .query(query).trackTotalHits(true);
        request.source(searchSourceBuilder);
        try {
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            Terms terms = response.getAggregations().get("by_appName");
            List<? extends Bucket> buckets = terms.getBuckets();
            List<String> list = new ArrayList<>();
            for (Bucket bucket : buckets) {
                String appName = bucket.getKeyAsString();
                list.add(appName);
            }
            return list;
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    @Override
    public String getAppNamesFromLog(String personId) {
        String appNamesStr = "";
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.queryStringQuery(APP_MODULARNAME).field(Y9LogSearchConsts.MODULAR_NAME));
        query.must(QueryBuilders.queryStringQuery(personId).field("userId"));
        query.must(QueryBuilders.queryStringQuery(Y9LoginUserHolder.getTenantId()).field(Y9LogSearchConsts.TENANT_ID));
        // 最近半年
        Calendar c = Calendar.getInstance();
        long endTime = c.getTime().getTime();
        c.add(Calendar.MONTH, -6);
        long startTime = c.getTime().getTime();
        query.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOG_TIME).from(startTime).to(endTime));

        SearchRequest request = new SearchRequest(Y9ESIndexConst.ACCESS_LOG_INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().query(query)
            .aggregation(AggregationBuilders.terms("by_methodName").field("methodName").size(100000))
            .trackTotalHits(true);
        request.source(searchSourceBuilder);

        try {
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            Terms terms = response.getAggregations().get("by_methodName");
            List<? extends Bucket> buckets = terms.getBuckets();
            List<String> list = new ArrayList<>();
            for (Bucket bucket : buckets) {
                String appName = bucket.getKeyAsString();
                list.add(appName);
            }
            appNamesStr = StringUtils.join(list, ",");
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        return appNamesStr;
    }

    @Override
    public long getCount() {
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.queryStringQuery(APP_MODULARNAME).field(Y9LogSearchConsts.MODULAR_NAME));
        // 最近半年
        Calendar c = Calendar.getInstance();
        long endTime = c.getTime().getTime();
        c.add(Calendar.MONTH, -6);
        long startTime = c.getTime().getTime();
        query.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOG_TIME).from(startTime).to(endTime));

        IndexCoordinates index = IndexCoordinates.of(Y9ESIndexConst.ACCESS_LOG_INDEX);
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(query).build();
        searchQuery.setTrackTotalHits(true);
        return elasticsearchOperations.count(searchQuery, index);
    }

    @Override
    public String saveForQuery() {
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.queryStringQuery(APP_MODULARNAME).field(Y9LogSearchConsts.MODULAR_NAME));
        // 最近半年
        Calendar c = Calendar.getInstance();
        long endTime = c.getTime().getTime();
        c.add(Calendar.MONTH, -6);
        long startTime = c.getTime().getTime();
        query.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOG_TIME).from(startTime).to(endTime));

        // 聚合子查询
        TermsAggregationBuilder userIdBuilder = AggregationBuilders.terms("by_userId").field("userId").size(100000);
        TermsAggregationBuilder methodNameBuilder =
            AggregationBuilders.terms("by_methodName").field("methodName").size(100000);
        userIdBuilder.subAggregation(methodNameBuilder);

        SearchRequest request = new SearchRequest(Y9ESIndexConst.ACCESS_LOG_INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().query(query).aggregation(userIdBuilder);
        request.source(searchSourceBuilder);
        try {
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            Terms terms = response.getAggregations().get("by_userId");
            List<? extends Bucket> buckets = terms.getBuckets();
            for (Bucket bucket : buckets) {
                List<String> list = new ArrayList<>();
                String userId = bucket.getKeyAsString();
                Aggregations aggregations = bucket.getAggregations();
                Terms methodNameTerms = aggregations.get("by_methodName");
                List<? extends Bucket> methodNameBuckets = methodNameTerms.getBuckets();
                for (Bucket methodNameBucket : methodNameBuckets) {
                    String appName = methodNameBucket.getKeyAsString();
                    list.add(appName);
                }
                String appNameStr = StringUtils.join(list, ",");
                Y9CommonAppForPerson cafp = commonAppForPersonRepository.findByPersonId(userId);
                if (null != cafp) {
                    cafp.setAppIds(appNameStr);
                    this.saveOrUpdate(cafp);
                } else {
                    Y9CommonAppForPerson commonApp = new Y9CommonAppForPerson();
                    commonApp.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    commonApp.setPersonId(userId);
                    commonApp.setAppIds(appNameStr);
                    this.saveOrUpdate(commonApp);
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return "失败";
        }
        return "成功";
    }

    public void saveOrUpdate(Y9CommonAppForPerson cafp) {
        commonAppForPersonRepository.save(cafp);
    }

    @Override
    public String syncData() {
        // 最近半年
        Calendar c = Calendar.getInstance();
        long endTime = c.getTime().getTime();
        c.add(Calendar.MONTH, -6);
        long startTime = c.getTime().getTime();
        long tempTime = (endTime - startTime) / 10;

        try {
            for (int i = 1; i <= 10; i++) {
                List<IndexQuery> queries = new ArrayList<>();
                int count = 0;
                long sTime = startTime + tempTime * (i - 1);
                long eTime = sTime + tempTime - 1;

                BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
                queryBuilder
                    .must(QueryBuilders.queryStringQuery(APP_MODULARNAME).field(Y9LogSearchConsts.MODULAR_NAME));
                queryBuilder.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOG_TIME).from(sTime).to(eTime));

                NativeSearchQuery query =
                    new NativeSearchQueryBuilder().withQuery(queryBuilder).withTrackTotalHits(true).build();
                // 使用bulk批量插入数据
                SearchHitsIterator<Y9logAccessLog> searchForStream = elasticsearchOperations.searchForStream(query,
                    Y9logAccessLog.class, IndexCoordinates.of(Y9ESIndexConst.ACCESS_LOG_INDEX));
                while (searchForStream.hasNext()) {
                    SearchHit<Y9logAccessLog> searchHit = searchForStream.next();
                    Y9logAccessLog y9logAccessLog = searchHit.getContent();
                    Y9ClickedApp clickedApp = new Y9ClickedApp();
                    clickedApp.setId(y9logAccessLog.getId());
                    clickedApp.setAppName(y9logAccessLog.getMethodName());
                    clickedApp.setPersonId(y9logAccessLog.getUserId());
                    clickedApp.setSaveDate(y9logAccessLog.getLogTime());

                    IndexQuery indexQuery =
                        new IndexQueryBuilder().withId(clickedApp.getId()).withObject(clickedApp).build();
                    queries.add(indexQuery);
                    if (++count % 1000 == 0) {
                        elasticsearchOperations.bulkIndex(queries, Y9ClickedApp.class);
                        queries.clear();
                    }
                }
                if (queries.size() > 0) {
                    elasticsearchOperations.bulkIndex(queries, Y9ClickedApp.class);
                }
                LOGGER.info("-----------dataCount:{}------------", count);
            }
        } catch (Exception e) {
            LOGGER.error("exception in syncData", e);
            return "failed";
        }
        return "success";
    }
}
