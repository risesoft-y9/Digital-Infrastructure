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
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.log.constant.Y9ESIndexConst;
import net.risesoft.log.constant.Y9LogSearchConsts;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;
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
    public long getCount() {
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(
            QueryBuilders.queryStringQuery(Y9LogSearchConsts.APP_MODULARNAME).field(Y9LogSearchConsts.MODULAR_NAME));
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

}
