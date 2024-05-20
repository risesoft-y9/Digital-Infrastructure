package net.risesoft.y9public.repository.custom.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.log.constant.Y9ESIndexConst;
import net.risesoft.log.constant.Y9LogSearchConsts;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;
import net.risesoft.y9public.entity.Y9logAccessLog;
import net.risesoft.y9public.repository.Y9CommonAppForPersonRepository;
import net.risesoft.y9public.repository.custom.Y9CommonAppForPersonCustomRepository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.SearchRequest;

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

    private final Y9CommonAppForPersonRepository commonAppForPersonRepository;
    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ElasticsearchClient elasticsearchClient;

    @Override
    public List<String> getAppNamesByPersonId(String personId) {
        SearchRequest searchRequest = SearchRequest.of(s -> s.index(Y9ESIndexConst.CLICKED_APP_INDEX)
            .query(
                q -> q.bool(b -> b.must(m -> m.queryString(qs -> qs.fields("personId").query(Y9Util.escape(personId))))
                    .must(m -> m.queryString(qs -> qs.fields(Y9LogSearchConsts.TENANT_ID)
                        .query(Y9Util.escape(Y9LoginUserHolder.getTenantId()))))))
            .aggregations("by_appName", a -> a.terms(t -> t.field("appName").size(100000)))
            .trackTotalHits(h -> h.enabled(true)));

        try {
            List<String> list = new ArrayList<>();
            elasticsearchClient.search(searchRequest, Void.class).aggregations().get("by_appName").sterms().buckets()
                .array().forEach(bucket -> {
                    String appName = bucket.key().toString();
                    list.add(appName);
                });
            return list;
        } catch (ElasticsearchException | IOException e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    @Override
    public long getCount() {
        // 最近半年
        Calendar c = Calendar.getInstance();
        long endTime = c.getTime().getTime();
        c.add(Calendar.MONTH, -6);
        long startTime = c.getTime().getTime();

        Criteria criteria = new Criteria();
        criteria.and(Y9LogSearchConsts.APP_METHODNAME).is(Y9LogSearchConsts.METHOD_NAME);
        criteria.and(Y9LogSearchConsts.LOG_TIME).between(startTime, endTime);
        CriteriaQuery query = new CriteriaQuery(criteria);
        query.setTrackTotalHits(true);

        return elasticsearchTemplate.count(query, Y9logAccessLog.class);
    }

}
