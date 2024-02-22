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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.CriteriaQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
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
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.y9.util.Y9Util;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;

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
    private static IndexCoordinates INDEX = IndexCoordinates.of(Y9ESIndexConst.LOGIN_INFO_INDEX);

    private final ElasticsearchClient elasticsearchClient;
    private final Y9logUserLoginInfoRepository y9logUserLoginInfoRepository;
    private final ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public long countByLoginTimeBetweenAndSuccess(Date startTime, Date endTime, String success) {
        Criteria criteria = new Criteria();
        if (StringUtils.isNotBlank(success)) {
            criteria.and(Y9LogSearchConsts.SUCCESS).is(success);
        }
        criteria.and(Y9LogSearchConsts.LOGIN_TIME).between(startTime.getTime(), endTime.getTime());
        Query query = new CriteriaQueryBuilder(criteria).build();
        return elasticsearchTemplate.count(query, INDEX);
    }

    @Override
    public long countByPersonId(String personId) {
        String parsePersonId = Y9Util.escape(personId);
        return y9logUserLoginInfoRepository.countByUserId(parsePersonId);
    }

    @Override
    public long countBySuccessAndUserHostIpAndUserId(String success, String userHostIp, String userId) {
        Criteria criteria = new Criteria();
        if (StringUtils.isNotBlank(success)) {
            criteria.and(Y9LogSearchConsts.SUCCESS).is(success);
        }
        if (StringUtils.isNotBlank(userHostIp)) {
            String parserUserHostIp = Y9Util.escape(userHostIp);
            criteria.and(Y9LogSearchConsts.USER_HOST_IP).is(parserUserHostIp);
        }
        if (StringUtils.isNotBlank(userId)) {
            String parserUserId = Y9Util.escape(userId);
            criteria.and(Y9LogSearchConsts.USER_ID).is(parserUserId);
        }
        Query query = new CriteriaQuery(criteria);
        return elasticsearchTemplate.count(query, INDEX);
    }

    @Override
    public long countByUserHostIpAndSuccess(String userHostIp, String success) {
        return y9logUserLoginInfoRepository.countByUserHostIpAndSuccess(userHostIp, success);
    }

    @Override
    public long countByUserHostIpAndSuccessAndUserName(String userHostIp, String success, String userName) {
        return y9logUserLoginInfoRepository.countByUserHostIpAndSuccessAndUserName(userHostIp, success, userName);
    }

    @Override
    public long countByUserHostIpLikeAndLoginTimeBetweenAndSuccess(String userHostIp, Date startTime, Date endTime,
        String success) {
        Criteria criteria = new Criteria();
        if (StringUtils.isNotBlank(success)) {
            criteria.and(Y9LogSearchConsts.SUCCESS).is(success);
        }
        if (StringUtils.isNotBlank(userHostIp)) {
            criteria.and(Y9LogSearchConsts.USER_HOST_IP).startsWith(userHostIp);
        }
        criteria.and(Y9LogSearchConsts.LOGIN_TIME).between(startTime.getTime(), endTime.getTime());
        Query query = new CriteriaQuery(criteria);
        return elasticsearchTemplate.count(query, INDEX);
    }

    @Override
    public Y9logUserLoginInfo getTopByTenantIdAndUserId(String tenantId, String userId) {
        return y9logUserLoginInfoRepository.findTopByTenantIdAndUserIdOrderByLoginTimeDesc(tenantId, userId);
    }

    @Override
    public Iterable<Y9logUserLoginInfo> listAll() {
        return y9logUserLoginInfoRepository.findAll();
    }

    @Override
    public List<Object[]> listDistinctUserHostIpByUserIdAndLoginTime(String userId, Date startTime, Date endTime) {
        List<Object[]> list = new ArrayList<>();

        SearchRequest searchRequest = SearchRequest.of(s -> s.index(Y9ESIndexConst.LOGIN_INFO_INDEX)
            .query(q -> q.bool(b -> b.must(m -> m.queryString(qs -> qs.fields(Y9LogSearchConsts.USER_ID).query(userId)))
                .must(m -> m.range(r -> r.field(Y9LogSearchConsts.LOGIN_TIME).from(String.valueOf(startTime.getTime()))
                    .to(String.valueOf(endTime.getTime()))))))
            .aggregations("by_userHostIp", a -> a.terms(t -> t.field(Y9LogSearchConsts.USER_HOST_IP))));

        try {
            elasticsearchClient.search(searchRequest, Y9logUserLoginInfo.class).aggregations().get("by_userHostIp")
                .sterms().buckets().array().forEach(bucket -> {
                    String[] userLoginInfo = {bucket.key().toString(), String.valueOf(bucket.docCount())};
                    list.add(userLoginInfo);
                });
        } catch (ElasticsearchException | IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> listUserHostIpByCip(String cip) {
        List<Map<String, Object>> list = new ArrayList<>();
        SearchRequest searchRequest = SearchRequest.of(s -> s.index(Y9ESIndexConst.LOGIN_INFO_INDEX)
            .query(q -> q.bool(
                b -> b.must(m -> m.queryString(qs -> qs.fields(Y9LogSearchConsts.USER_HOST_IP).query(cip + "*")))))
            .aggregations("by_UserHostIP", a -> a.terms(t -> t.field(Y9LogSearchConsts.USER_HOST_IP))));
        try {
            SearchResponse<Y9logUserLoginInfo> response =
                elasticsearchClient.search(searchRequest, Y9logUserLoginInfo.class);
            response.aggregations().get("by_UserHostIP").sterms().buckets().array().forEach(bucket -> {
                Map<String, Object> map = new HashMap<>();
                String serverIp = bucket.key().toString();
                String text = serverIp + "<span style='color:red'>(" + bucket.docCount() + ")</span>";
                map.put("serverIp", serverIp);
                map.put("text", text);
                list.add(map);
            });
        } catch (ElasticsearchException | IOException e) {
            LOGGER.warn(e.getMessage(), e);
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
    public Y9Page<Y9logUserLoginInfo> page(String tenantId, String userHostIp, String userId, String success,
        String startTime, String endTime, Y9PageQuery pageQuery) {
        Criteria criteria = new Criteria();
        if (StringUtils.isNotBlank(userHostIp)) {
            String parserUserHostIp = Y9Util.escape(userHostIp);
            criteria.and(Y9LogSearchConsts.USER_HOST_IP).is(parserUserHostIp);
        }
        if (StringUtils.isNotBlank(userId)) {
            String parseUserId = Y9Util.escape(userId);
            criteria.and(Y9LogSearchConsts.USER_ID).is(parseUserId);
        }
        if (StringUtils.isNotBlank(tenantId)) {
            criteria.and(Y9LogSearchConsts.TENANT_ID).is(tenantId);
        }
        if (StringUtils.isNotBlank(success)) {
            criteria.and(Y9LogSearchConsts.SUCCESS).is(success);
        }
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                criteria.and(Y9LogSearchConsts.LOGIN_TIME).between(simpleDateFormat.parse(startTime).getTime(),
                    simpleDateFormat.parse(endTime).getTime());
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }

        int page = pageQuery.getPage4Db();
        int size = pageQuery.getSize();
        Query query = new CriteriaQuery(criteria).setPageable(PageRequest.of(page, size))
            .addSort(Sort.by(Direction.DESC, Y9LogSearchConsts.LOGIN_TIME));
        SearchHits<Y9logUserLoginInfo> searchHits =
            elasticsearchTemplate.search(query, Y9logUserLoginInfo.class, INDEX);

        List<Y9logUserLoginInfo> list = searchHits.stream()
            .map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        int totalPages = (int)searchHits.getTotalHits() / size;
        return Y9Page.success(page, searchHits.getTotalHits() % size == 0 ? totalPages : totalPages + 1,
            searchHits.getTotalHits(), list);
    }

    @Override
    public Page<Y9logUserLoginInfo> pageBySuccessAndServerIpAndUserName(String success, String userHostIp,
        String userId, int page, int rows) {
        String parserUserId = Y9Util.escape(userId);
        String parserUserHostIp = Y9Util.escape(userHostIp);
        Pageable pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.by(Sort.Direction.DESC, Y9LogSearchConsts.LOGIN_TIME));
        Page<Y9logUserLoginInfo> userLoginInfoPage = y9logUserLoginInfoRepository
            .findBySuccessAndUserHostIpAndUserId(success, parserUserHostIp, parserUserId, pageable);
        return userLoginInfoPage;
    }

    @Override
    public Page<Y9logUserLoginInfo> pageByTenantIdAndManagerLevel(String tenantId, String managerLevel, int page,
        int rows) {
        Pageable pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.by(Sort.Direction.DESC, Y9LogSearchConsts.LOGIN_TIME));
        Page<Y9logUserLoginInfo> userLoginInfoPage =
            y9logUserLoginInfoRepository.findByTenantIdAndManagerLevel(tenantId, managerLevel, pageable);
        return userLoginInfoPage;
    }

    @Override
    public Y9Page<Map<String, Object>> pageByUserHostIpAndSuccess(String userHostIp, String success, int page,
        int rows) {
        int startIndex = (page - 1) * rows;
        int endIndex = page * rows;
        List<Map<String, Object>> strList = new ArrayList<>();

        SearchRequest searchRequest = SearchRequest.of(s -> s.index(Y9ESIndexConst.LOGIN_INFO_INDEX)
            .query(q -> q.bool(b -> b.must(m -> m.term(t -> t.field(Y9LogSearchConsts.USER_HOST_IP).value(userHostIp)))
                .must(m -> m.term(t -> t.field(Y9LogSearchConsts.SUCCESS).value(success)))))
            .aggregations("username-aggs", a -> a.terms(t -> t.field(Y9LogSearchConsts.USER_NAME))
                .aggregations("topHits", aggs -> aggs.topHits(h -> h.size(1).explain(true)))));

        try {
            SearchResponse<Y9logUserLoginInfo> response =
                elasticsearchClient.search(searchRequest, Y9logUserLoginInfo.class);
            List<StringTermsBucket> stbList = response.aggregations().get("username-aggs").sterms().buckets().array();
            int totalCount = stbList.size();
            endIndex = endIndex < totalCount ? endIndex : totalCount;
            for (int i = startIndex; i < endIndex; i++) {
                Map<String, Object> map = new HashMap<>();
                StringTermsBucket bucket = stbList.get(i);
                long count = bucket.docCount();
                Y9logUserLoginInfo y9logUserLoginInfo = bucket.aggregations().get("topHits").topHits().hits().hits()
                    .get(0).source().to(Y9logUserLoginInfo.class);
                map.put(Y9LogSearchConsts.USER_ID, y9logUserLoginInfo.getUserId());
                map.put(Y9LogSearchConsts.USER_NAME, y9logUserLoginInfo.getUserName());
                map.put("serverCount", String.valueOf(count));
                strList.add(map);
            }
            return Y9Page.success(page, (int)Math.ceil((float)totalCount / (float)page), totalCount, strList);
        } catch (ElasticsearchException | IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Y9Page<Map<String, Object>> pageByUserHostIpAndSuccessAndUserNameLike(String userHostIp, String success,
        String userName, int page, int rows) {
        int endIndex = page * rows;
        List<Map<String, Object>> strList = new ArrayList<>();

        SearchRequest searchRequest = SearchRequest.of(s -> s.index(Y9ESIndexConst.LOGIN_INFO_INDEX)
            .query(q -> q.bool(b -> b.must(m -> m.term(t -> t.field(Y9LogSearchConsts.USER_HOST_IP).value(userHostIp)))
                .must(m -> m.term(t -> t.field(Y9LogSearchConsts.SUCCESS).value(success)))
                .must(m -> m.term(t -> t.field(Y9LogSearchConsts.USER_NAME).value("*" + userName + "*")))))
            .aggregations("userNameAggs", a -> a.terms(t -> t.field(Y9LogSearchConsts.USER_NAME))
                .aggregations("topHits", aggs -> aggs.topHits(h -> h.size(1).explain(true)))));

        try {
            SearchResponse<Y9logUserLoginInfo> response =
                elasticsearchClient.search(searchRequest, Y9logUserLoginInfo.class);
            List<StringTermsBucket> list = response.aggregations().get("userNameAggs").sterms().buckets().array();
            int totalCount = list.size();
            endIndex = endIndex < totalCount ? endIndex : totalCount;
            for (StringTermsBucket bucket : list) {
                long count = bucket.docCount();
                List<Hit<JsonData>> topHitList = bucket.aggregations().get("topHits").topHits().hits().hits();
                for (Hit<JsonData> h : topHitList) {
                    Map<String, Object> map = new HashMap<>();
                    Y9logUserLoginInfo y9logUserLoginInfo = h.source().to(Y9logUserLoginInfo.class);
                    map.put(Y9LogSearchConsts.USER_ID, y9logUserLoginInfo.getUserId());
                    map.put(Y9LogSearchConsts.USER_NAME, y9logUserLoginInfo.getUserName());
                    map.put("serverCount", String.valueOf(count));
                    strList.add(map);
                }
            }
            return Y9Page.success(page, (int)Math.ceil((float)totalCount / (float)page), totalCount, strList);
        } catch (ElasticsearchException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void save(Y9logUserLoginInfo y9logUserLoginInfo) {
        y9logUserLoginInfoRepository.save(y9logUserLoginInfo);
    }

    @Override
    public Y9Page<Y9logUserLoginInfo> search(Date startTime, Date endTime, String success, int page, int rows) {
        return search(null, startTime, endTime, success, page, rows);
    }

    @Override
    public Y9Page<Y9logUserLoginInfo> search(String userHostIp, Date startTime, Date endTime, String success, int page,
        int rows) {
        Criteria criteria = new Criteria();
        if (StringUtils.isNotBlank(userHostIp)) {
            criteria.and(Y9LogSearchConsts.USER_HOST_IP).startsWith(userHostIp);
        }
        if (StringUtils.isNotBlank(success)) {
            criteria.and(Y9LogSearchConsts.SUCCESS).is(success);
        }
        if (startTime != null && endTime != null) {
            criteria.and(Y9LogSearchConsts.LOGIN_TIME).between(startTime.getTime(), endTime.getTime());
        }
        Query query = new CriteriaQuery(criteria).setPageable(PageRequest.of((page < 1) ? 0 : page - 1, rows))
            .addSort(Sort.by(Direction.DESC, Y9LogSearchConsts.LOGIN_TIME));

        SearchHits<Y9logUserLoginInfo> searchHits =
            elasticsearchTemplate.search(query, Y9logUserLoginInfo.class, INDEX);

        List<Y9logUserLoginInfo> list = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        int totalPages = (int)searchHits.getTotalHits() / rows;
        return Y9Page.success(page, searchHits.getTotalHits() % rows == 0 ? totalPages : totalPages + 1,
            searchHits.getTotalHits(), list);
    }

    @Override
    public Y9Page<Y9logUserLoginInfo> searchQuery(String tenantId, String managerLevel, LogInfoModel loginInfoModel,
        int page, int rows) {
        Pageable pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.by(Sort.Direction.DESC, Y9LogSearchConsts.LOGIN_TIME));
        Criteria criteria = new Criteria();
        if (StringUtils.isNotBlank(tenantId)) {
            criteria.and(Y9LogSearchConsts.TENANT_ID).is(tenantId);
        }
        if (StringUtils.isNotBlank(managerLevel)) {
            criteria.and(Y9LogSearchConsts.MANAGER_LEVEL).is(managerLevel);
        }
        if (StringUtils.isNotBlank(loginInfoModel.getUserName())) {
            criteria.and(Y9LogSearchConsts.USER_NAME).contains(loginInfoModel.getUserName());
        }
        if (StringUtils.isNotBlank(loginInfoModel.getUserHostIp())) {
            criteria.and(Y9LogSearchConsts.USER_HOST_IP).contains(loginInfoModel.getUserHostIp());
        }
        if (StringUtils.isNotBlank(loginInfoModel.getOsName())) {
            criteria.and("osName").contains(loginInfoModel.getOsName());
        }
        if (StringUtils.isNotBlank(loginInfoModel.getScreenResolution())) {
            criteria.and("screenResolution").contains(loginInfoModel.getScreenResolution());
        }
        if (StringUtils.isNotBlank(loginInfoModel.getSuccess())) {
            criteria.and(Y9LogSearchConsts.SUCCESS).is(loginInfoModel.getSuccess());
        }
        if (StringUtils.isNotBlank(loginInfoModel.getBrowserName())) {
            criteria.and("browserName").contains(loginInfoModel.getSuccess());
        }
        if (StringUtils.isNotBlank(loginInfoModel.getBrowserVersion())) {
            criteria.and("browserVersion").contains(loginInfoModel.getBrowserVersion());
        }
        if (StringUtils.isNotBlank(loginInfoModel.getStartTime())
            && StringUtils.isNotBlank(loginInfoModel.getEndTime())) {
            try {
                Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(loginInfoModel.getStartTime());
                Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(loginInfoModel.getEndTime());
                criteria.and(Y9LogSearchConsts.LOGIN_TIME).between(startDate, endDate);
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        SearchHits<Y9logUserLoginInfo> searchHits =
            elasticsearchTemplate.search(query, Y9logUserLoginInfo.class, INDEX);
        List<Y9logUserLoginInfo> list = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        int totalPages = (int)searchHits.getTotalHits() / rows;
        return Y9Page.success(page, searchHits.getTotalHits() % rows == 0 ? totalPages : totalPages + 1,
            searchHits.getTotalHits(), list);
    }
}
