package net.risesoft.repository.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.data.elasticsearch.core.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.log.constant.Y9ESIndexConst;
import net.risesoft.log.constant.Y9LogSearchConsts;
import net.risesoft.log.domain.Y9CommonAppForPersonDO;
import net.risesoft.log.domain.Y9LogAccessLogDO;
import net.risesoft.log.repository.Y9CommonAppForPersonCustomRepository;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9.util.Y9Util;
import net.risesoft.y9public.entity.Y9ClickedApp;
import net.risesoft.y9public.entity.Y9CommonAppForPerson;
import net.risesoft.y9public.repository.Y9CommonAppForPersonRepository;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;

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

    private static final IndexCoordinates INDEX = IndexCoordinates.of(Y9ESIndexConst.CLICKED_APP_INDEX);
    private final ElasticsearchOperations elasticsearchOperations;

    private final Y9CommonAppForPersonRepository y9CommonAppForPersonRepository;

    @Override
    public List<String> getAppNamesByPersonId(String personId) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("personId", Y9Util.escape(personId)));
        boolQueryBuilder
            .must(QueryBuilders.termQuery(Y9LogSearchConsts.TENANT_ID, Y9Util.escape(Y9LoginUserHolder.getTenantId())));

        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        builder.withQuery(boolQueryBuilder);
        builder.withAggregations(AggregationBuilders.terms("by_appName").field("appName").size(100000));
        builder.withTrackTotalHits(true);

        try {
            List<String> list = new ArrayList<>();
            SearchHits<Y9ClickedApp> searchHits =
                elasticsearchOperations.search(builder.build(), Y9ClickedApp.class, INDEX);
            ElasticsearchAggregations aggregations = (ElasticsearchAggregations)searchHits.getAggregations();
            if (aggregations != null) {
                Terms terms = aggregations.aggregations().get("by_appName");
                List<? extends Terms.Bucket> buckets = terms.getBuckets();
                buckets.forEach(bucket -> {
                    list.add(bucket.getKeyAsString());
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
            .and(Y9LogSearchConsts.LOG_TIME)
            .between(startTime, endTime);
        CriteriaQuery query = new CriteriaQuery(criteria);
        query.setTrackTotalHits(true);
        return elasticsearchOperations.count(query, Y9LogAccessLogDO.class);
    }

    @Override
    public Y9CommonAppForPersonDO findByPersonId(String personId) {
        Y9CommonAppForPerson y9CommonAppForPerson = y9CommonAppForPersonRepository.findByPersonId(personId);
        return Y9ModelConvertUtil.convert(y9CommonAppForPerson, Y9CommonAppForPersonDO.class);
    }

    @Override
    public void save(Y9CommonAppForPersonDO cafp) {
        Y9CommonAppForPerson y9CommonAppForPerson = Y9ModelConvertUtil.convert(cafp, Y9CommonAppForPerson.class);
        y9CommonAppForPersonRepository.save(y9CommonAppForPerson);
    }

}
