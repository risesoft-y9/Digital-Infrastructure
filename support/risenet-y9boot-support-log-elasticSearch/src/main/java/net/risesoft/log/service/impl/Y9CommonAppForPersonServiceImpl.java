package net.risesoft.log.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.log.constant.Y9ESIndexConst;
import net.risesoft.log.constant.Y9LogSearchConsts;
import net.risesoft.log.entity.Y9CommonAppForPerson;
import net.risesoft.log.entity.Y9logAccessLog;
import net.risesoft.log.repository.Y9CommonAppForPersonRepository;
import net.risesoft.log.service.Y9CommonAppForPersonService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

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
public class Y9CommonAppForPersonServiceImpl implements Y9CommonAppForPersonService {
    private static final String APP_MODULARNAME = "net.risesoft.controller.admin.WebsiteController.saveAppCheckCount";

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
    public String getAppNamesFromLog(String personId) {
        // 最近半年
        Calendar c = Calendar.getInstance();
        long endTime = c.getTime().getTime();
        c.add(Calendar.MONTH, -6);
        long startTime = c.getTime().getTime();

        SearchRequest searchRequest = SearchRequest.of(s -> s.index(Y9ESIndexConst.ACCESS_LOG_INDEX).query(q -> q.bool(
            b -> b.must(m -> m.queryString(qs -> qs.fields(Y9LogSearchConsts.MODULAR_NAME).query(APP_MODULARNAME)))
                .must(m -> m.queryString(qs -> qs.fields("userId").query(personId)))
                .must(m -> m
                    .queryString(qs -> qs.fields(Y9LogSearchConsts.TENANT_ID).query(Y9LoginUserHolder.getTenantId())))
                .must(m -> m.range(r -> r.field(Y9LogSearchConsts.LOG_TIME).from(String.valueOf(startTime))
                    .to(String.valueOf(endTime))))))
            .aggregations("by_methodName", a -> a.terms(t -> t.field("methodName").size(100000)))
            .trackTotalHits(h -> h.enabled(true)));

        try {
            List<String> list = new ArrayList<>();
            elasticsearchClient.search(searchRequest, Void.class).aggregations().get("by_methodName").sterms().buckets()
                .array().forEach(bucket -> {
                    String appName = bucket.key().toString();
                    list.add(appName);
                });
            return StringUtils.join(list, ",");
        } catch (ElasticsearchException | IOException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Y9CommonAppForPerson getCommonAppForPersonByPersonId(String personId) {
        return commonAppForPersonRepository.findByPersonId(personId);
    }

    @Override
    public long getCount() {
        // 最近半年
        Calendar c = Calendar.getInstance();
        long endTime = c.getTime().getTime();
        c.add(Calendar.MONTH, -6);
        long startTime = c.getTime().getTime();

        Criteria criteria = new Criteria();
        criteria.and(Y9LogSearchConsts.MODULAR_NAME).is(APP_MODULARNAME);
        criteria.and(Y9LogSearchConsts.LOG_TIME).between(startTime, endTime);
        CriteriaQuery query = new CriteriaQuery(criteria);
        query.setTrackTotalHits(true);

        return elasticsearchTemplate.count(query, Y9logAccessLog.class);
    }

    @Override
    public String saveForQuery() {
        return null;
    }

    @Override
    public void saveOrUpdate(Y9CommonAppForPerson cafp) {
        commonAppForPersonRepository.save(cafp);
    }

    @Override
    public String syncData() {

        return null;
    }
}
