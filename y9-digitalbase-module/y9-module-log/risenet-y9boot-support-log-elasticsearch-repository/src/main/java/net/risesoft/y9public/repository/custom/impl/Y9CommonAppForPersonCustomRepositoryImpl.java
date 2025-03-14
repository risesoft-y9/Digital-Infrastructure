package net.risesoft.y9public.repository.custom.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.log.constant.Y9ESIndexConst;
import net.risesoft.log.constant.Y9LogSearchConsts;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;
import net.risesoft.y9public.entity.Y9ClickedApp;
import net.risesoft.y9public.entity.Y9logAccessLog;
import net.risesoft.y9public.repository.custom.Y9CommonAppForPersonCustomRepository;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;

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
public class Y9CommonAppForPersonCustomRepositoryImpl implements Y9CommonAppForPersonCustomRepository {

    private static final IndexCoordinates INDEX = IndexCoordinates.of(Y9ESIndexConst.CLICKED_APP_INDEX);
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public List<String> getAppNamesByPersonId(String personId) {
        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        boolQueryBuilder.must(QueryBuilders.term(t -> t.field("personId").value(Y9Util.escape(personId))));
        boolQueryBuilder.must(QueryBuilders
            .term(t -> t.field(Y9LogSearchConsts.TENANT_ID).value(Y9Util.escape(Y9LoginUserHolder.getTenantId()))));

        Aggregation appAggs = Aggregation.of(a -> a.terms(t -> t.field("appName").size(100000)));

        NativeQueryBuilder builder = new NativeQueryBuilder();
        builder.withQuery(boolQueryBuilder.build()._toQuery());
        builder.withAggregation("by_appName", appAggs);
        builder.withTrackTotalHits(true);

        try {
            List<String> list = new ArrayList<>();
            SearchHits<Y9ClickedApp> searchHits =
                elasticsearchOperations.search(builder.build(), Y9ClickedApp.class, INDEX);
            ElasticsearchAggregations aggregations = (ElasticsearchAggregations)searchHits.getAggregations();
            // 指定聚合的名称
            ElasticsearchAggregation elasticsearchAggregation = aggregations.get("by_appName");
            // 获得聚合
            Aggregate aggregate = elasticsearchAggregation.aggregation().getAggregate();
            if (aggregate != null) {
                List<? extends StringTermsBucket> buckets = aggregate.sterms().buckets().array();
                buckets.forEach(bucket -> {
                    String appName = bucket.key().stringValue();
                    list.add(appName);
                });
            }
            return list;
        } catch (ElasticsearchException e) {
            LOGGER.error(e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public long getCount() {
        // 最近半年
        Calendar c = Calendar.getInstance();
        long endTime = c.getTime().getTime();
        c.add(Calendar.MONTH, -6);
        long startTime = c.getTime().getTime();

        Criteria criteria = new Criteria(Y9LogSearchConsts.APP_METHODNAME).is(Y9LogSearchConsts.METHOD_NAME)
            .and(Y9LogSearchConsts.LOG_TIME).between(startTime, endTime);
        CriteriaQuery query = new CriteriaQuery(criteria);
        query.setTrackTotalHits(true);
        return elasticsearchOperations.count(query, Y9logAccessLog.class);
    }

}
