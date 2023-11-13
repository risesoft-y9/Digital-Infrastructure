package net.risesoft.log.service.impl;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.GetAliasesResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.metadata.AliasMetadata;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.log.AccessLogModelConvertUtil;
import net.risesoft.log.constant.Y9ESIndexConst;
import net.risesoft.log.constant.Y9LogSearchConsts;
import net.risesoft.log.entity.Y9logAccessLog;
import net.risesoft.log.service.Y9logAccessLogService;
import net.risesoft.log.service.Y9logMappingService;
import net.risesoft.model.log.AccessLog;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Tenant;
import net.risesoft.model.log.LogInfoModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Day;

import y9.client.platform.org.DepartmentApiClient;
import y9.client.platform.org.GroupApiClient;
import y9.client.platform.org.OrganizationApiClient;
import y9.client.platform.org.PersonApiClient;
import y9.client.platform.org.PositionApiClient;
import y9.client.platform.tenant.TenantApiClient;

/**
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class Y9logAccessLogServiceImpl implements Y9logAccessLogService {

    private final DepartmentApiClient departmentManager;
    private final PersonApiClient personManager;
    private final PositionApiClient positionManager;
    private final GroupApiClient groupManager;
    private final OrganizationApiClient organizationManager;
    private final TenantApiClient tenantManager;
    private final Y9logMappingService y9logMappingService;
    private final RestHighLevelClient elasticsearchClient;
    private final ElasticsearchOperations elasticsearchOperations;

    // 目前日志查询页有两种情况：一种是有开始时间和结束时间，另一种是只选一个时间
    private String[] createIndexNames(String startDate, String endDate) {
        List<String> indexNameList = new ArrayList<>();
        if (StringUtils.isBlank(startDate) && StringUtils.isBlank(endDate)) {
            indexNameList.add(getCurrentYearIndexName());

        } else if (null != startDate && StringUtils.isBlank(endDate)) {
            String yearString = startDate.split("-")[0];
            indexNameList.add(Y9ESIndexConst.ACCESS_LOG_INDEX + "-" + yearString);

        } else {
            int minYearInt = Integer.parseInt(startDate.split("-")[0]);
            int maxYearInt = Integer.parseInt(endDate.split("-")[0]);
            while (minYearInt <= maxYearInt) {
                indexNameList.add(Y9ESIndexConst.ACCESS_LOG_INDEX + "-" + String.valueOf(minYearInt));
                minYearInt++;
            }
        }
        return indexNameList.toArray(new String[0]);
    }

    private String[] getAllLogindexName() {
        List<String> indexNameList = new ArrayList<>();
        try {
            GetAliasesRequest request = new GetAliasesRequest();
            GetAliasesResponse aliasesResponse =
                elasticsearchClient.indices().getAlias(request, RequestOptions.DEFAULT);
            Map<String, Set<AliasMetadata>> map = aliasesResponse.getAliases();
            for (String key : map.keySet()) {
                if (key.contains(Y9ESIndexConst.ACCESS_LOG_INDEX)) {
                    indexNameList.add(key);
                }
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return indexNameList.toArray(new String[0]);
    }

    @Override
    public Map<String, Object> getAppClickCount(String orgId, String orgType, String tenantId, String startDay,
        String endDay) throws UnknownHostException {
        String guidPath = getGuidPath(orgId, orgType, tenantId);
        Map<String, Object> returnMap = new HashMap<>();
        List<String> strList = new ArrayList<>();
        List<Long> longList = new ArrayList<>();

        SearchResponse response = null;
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.queryStringQuery("net.risesoft.controller.admin.WebsiteController.saveAppCheckCount")
            .field(Y9LogSearchConsts.MODULAR_NAME));
        if (StringUtils.isNotBlank(tenantId)) {
            query.must(QueryBuilders.queryStringQuery(tenantId).field(Y9LogSearchConsts.TENANT_ID));
        }
        if (StringUtils.isNotBlank(guidPath)) {
            query.must(QueryBuilders.queryStringQuery(guidPath + "*").field(Y9LogSearchConsts.GUID_PATH));
        }

        if (StringUtils.isNotBlank(startDay) && StringUtils.isNotBlank(endDay)) {
            Date sDay = new Date();
            Date eDay = new Date();
            try {
                sDay = Y9Day.getStartOfDay(new SimpleDateFormat("yyyy-MM-dd").parse(startDay));
                eDay = Y9Day.getEndOfDay(new SimpleDateFormat("yyyy-MM-dd").parse(endDay));
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
            query.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOG_TIME).from(sDay.getTime()).to(eDay.getTime()));
        }

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(query)
            .aggregation(AggregationBuilders.terms("by_appName").size(10000).field(Y9LogSearchConsts.METHOD_NAME));
        SearchRequest searchRequest = new SearchRequest(createIndexNames(startDay, endDay));
        searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
        searchRequest.source(searchSourceBuilder);
        try {
            response = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }

        Terms terms = response.getAggregations().get("by_appName");
        int length = terms.getBuckets().size();
        for (int i = length - 1; i >= 0; i--) {
            String appName = terms.getBuckets().get(i).getKey().toString();
            strList.add(appName);
            longList.add(terms.getBuckets().get(i).getDocCount());
        }
        returnMap.put("number", length);
        returnMap.put("name", strList);
        returnMap.put("value", longList);
        return returnMap;
    }

    public long getCountByQueryCondition(BoolQueryBuilder query, String startDay, String endDay) {
        String tenantId = Y9LoginUserHolder.getTenantId();

        if (!getTenantType(tenantId).equals(1)) {
            query.must(QueryBuilders.queryStringQuery(tenantId).field(Y9LogSearchConsts.TENANT_ID));
        }

        SearchRequest searchRequest = new SearchRequest(createIndexNames(startDay, endDay));
        searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(query);
        searchRequest.source(searchSourceBuilder);
        long count = 0L;
        try {
            count = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT).getHits().getHits().length;
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }

        return count;
    }

    public String getCurrentYearIndexName() {
        String yearStr = String.valueOf(new DateTime().getYear());
        return Y9ESIndexConst.ACCESS_LOG_INDEX + "-" + yearStr;
    }

    public String getGuidPath(String orgId, String orgType, String tenantId) {
        String guidPath = null;
        if (StringUtils.isNotBlank(orgId) && StringUtils.isNotBlank(orgType)) {
            if (orgType.equals(OrgTypeEnum.ORGANIZATION.getEnName())) {
                guidPath = organizationManager.getOrganization(tenantId, orgId).getData().getGuidPath();
            } else if (orgType.equals(OrgTypeEnum.DEPARTMENT.getEnName())) {
                // orgDepartmentService.getDNById(orgId);
                guidPath = departmentManager.getDepartment(tenantId, orgId).getData().getGuidPath();
            } else if (orgType.equals(OrgTypeEnum.GROUP.getEnName())) {
                // orgGroupService.getDNById(orgId);
                guidPath = groupManager.getGroup(tenantId, orgId).getData().getGuidPath();
            } else if (orgType.equals(OrgTypeEnum.POSITION.getEnName())) {
                // orgPositionService.getDNById(orgId);
                guidPath = positionManager.getPosition(tenantId, orgId).getData().getGuidPath();
            } else if (orgType.equals(OrgTypeEnum.PERSON.getEnName())) {
                // orgPersonService.getDNById(orgId);
                guidPath = personManager.getPerson(tenantId, orgId).getData().getGuidPath();
            }
        }
        return guidPath;
    }

    @Override
    public Map<String, Object> getModuleNameCount(String orgId, String orgType, String tenantId, String startDay,
        String endDay) {
        String guidPath = getGuidPath(orgId, orgType, tenantId);
        Map<String, Object> map = new HashMap<>();
        List<String> strList = new ArrayList<>();
        List<Long> longList = new ArrayList<>();
        SearchResponse response = null;
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.existsQuery(Y9LogSearchConsts.USER_NAME));

        if (StringUtils.isNotBlank(guidPath)) {
            // guidPath = Y9Util.escape(guidPath);
            query.must(QueryBuilders.queryStringQuery(guidPath + "*").field(Y9LogSearchConsts.GUID_PATH));
        }
        if (StringUtils.isNotBlank(tenantId)) {
            query.must(QueryBuilders.queryStringQuery(tenantId).field(Y9LogSearchConsts.TENANT_ID));
        }

        if (StringUtils.isNotBlank(startDay) && StringUtils.isNotBlank(endDay)) {
            Date sDay = new Date();
            Date eDay = new Date();
            try {
                sDay = Y9Day.getStartOfDay(new SimpleDateFormat("yyyy-MM-dd").parse(startDay));
                Y9Day.getEndOfDay(new SimpleDateFormat("yyyy-MM-dd").parse(endDay));
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
            query.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOG_TIME).from(sDay.getTime()).to(eDay.getTime()));
        }

        SearchRequest request = new SearchRequest(createIndexNames(startDay, endDay));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(query)
            .aggregation(AggregationBuilders.terms("by_modularname").size(1000).field(Y9LogSearchConsts.MODULAR_NAME));
        request.source(searchSourceBuilder);
        try {
            response = elasticsearchClient.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        Terms terms = response.getAggregations().get("by_modularname");
        for (int i = terms.getBuckets().size() - 1; i >= 0; i--) {
            String modularName = terms.getBuckets().get(i).getKey().toString();
            String modularCnName = y9logMappingService.getCnModularName(modularName);
            if (StringUtils.isNotBlank(modularCnName)) {
                modularName = modularCnName;
            }
            strList.add(modularName);
            longList.add(terms.getBuckets().get(i).getDocCount());
        }
        int num = strList.size();
        map.put("name", strList);
        map.put("value", longList);
        map.put("number", num);
        return map;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Map<String, Object> getOperateStatusCount(String selectedDate) {
        List<Integer> time = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        List<Long> countOfSuccess = new ArrayList<>();
        List<Long> countOfError = new ArrayList<>();
        String success = "成功";
        String error = "出错";
        Date day = new Date();
        if (StringUtils.isNotBlank(selectedDate)) {
            try {
                day = new SimpleDateFormat("yyyy-MM-dd").parse(selectedDate);
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
            Date startOfTime = Y9Day.getStartOfDay(day);
            Date endOfTime = null;
            for (int i = 0; i < 24; i++) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(day);
                cal.set(Calendar.HOUR_OF_DAY, i);
                startOfTime = cal.getTime();
                cal.add(Calendar.MINUTE, 59);
                cal.add(Calendar.SECOND, 59);
                endOfTime = cal.getTime();

                BoolQueryBuilder squery = QueryBuilders.boolQuery();
                BoolQueryBuilder equery = QueryBuilders.boolQuery();

                squery.must(QueryBuilders.queryStringQuery(success).field(Y9LogSearchConsts.SUCCESS));
                squery.must(QueryBuilders.existsQuery(Y9LogSearchConsts.USER_NAME));
                squery.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOG_TIME).from(startOfTime.getTime())
                    .to(endOfTime.getTime()));
                countOfSuccess.add(getCountByQueryCondition(squery, selectedDate, null));

                equery.must(QueryBuilders.queryStringQuery(error).field(Y9LogSearchConsts.SUCCESS));
                equery.must(QueryBuilders.existsQuery(Y9LogSearchConsts.USER_NAME));
                equery.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOG_TIME).from(startOfTime.getTime())
                    .to(endOfTime.getTime()));
                countOfError.add(getCountByQueryCondition(equery, selectedDate, null));

                time.add(startOfTime.getHours());
            }
        }
        map.put("time", time);
        map.put("totalOfSuccess", countOfSuccess);
        map.put("totalOfError", countOfError);
        return map;
    }

    private Page<Y9logAccessLog> getSearchList(QueryBuilder query, Integer page, Integer rows, String startDay,
        String endDay) {
        IndexCoordinates index = IndexCoordinates.of(createIndexNames(startDay, endDay));

        PageRequest pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(query).withPageable(pageable).build();
        searchQuery.setTrackTotalHits(true);
        SearchHits<Y9logAccessLog> searchHits =
            elasticsearchOperations.search(searchQuery, Y9logAccessLog.class, index);

        List<Y9logAccessLog> list = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        Page<Y9logAccessLog> pageResult = new PageImpl<>(list, pageable, searchHits.getTotalHits());
        return pageResult;
    }

    private List<String> getTenantIds() {
        List<Tenant> tenantList = tenantManager.listAllTenants().getData();
        return tenantList.stream().map(Tenant::getId).collect(Collectors.toList());
    }

    private Integer getTenantType(String tenantId) {
        Integer num = 0;
        Tenant tenant = tenantManager.getById(tenantId).getData();
        if (null != tenant) {
            num = tenant.getTenantType().getValue();
        }
        return num;
    }

    @Override
    public List<String> listAccessLog(String startTime, String endTime, String loginName, String tenantId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<String> strList = new ArrayList<>();
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = sdf.parse(startTime);
            endDate = sdf.parse(endTime);
        } catch (ParseException e1) {
            LOGGER.warn(e1.getMessage(), e1);
        }

        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.queryStringQuery(loginName).field(Y9LogSearchConsts.USER_NAME));
        query
            .must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOG_TIME).from(startDate.getTime()).to(endDate.getTime()));
        if (StringUtils.isNotBlank(tenantId)) {
            query.must(QueryBuilders.queryStringQuery(tenantId).field(Y9LogSearchConsts.TENANT_ID));
        }

        SearchRequest searchRequest =
            new SearchRequest(createIndexNames(startTime, endTime)).searchType(SearchType.DFS_QUERY_THEN_FETCH);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(query);
        searchRequest.source(searchSourceBuilder);
        org.elasticsearch.search.SearchHit[] searchhits = null;
        try {
            searchhits = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT).getHits().getHits();
            for (int i = 0; i < searchhits.length; i++) {
                String accesslogjson = searchhits[i].getSourceAsString();
                strList.add(accesslogjson);
            }
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return strList;
    }

    @Override
    public List<Long> listOperateTimeCount(String startDay, String endDay) {
        Date sDay = null;
        Date eDay = null;
        List<Long> list = new ArrayList<>();
        long[] longArray = {1L, 1000000L, 10000000L, 100000000L, 1000000000L, 5000000000L, 10000000000L};

        if (StringUtils.isNotBlank(startDay)) {
            try {
                Date day = new SimpleDateFormat("yyyy-MM-dd").parse(startDay);
                sDay = Y9Day.getStartOfDay(day);
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        if (StringUtils.isNotBlank(endDay)) {
            try {
                Date day = new SimpleDateFormat("yyyy-MM-dd").parse(endDay);
                eDay = Y9Day.getEndOfDay(day);
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        for (int i = 0; i < longArray.length; i++) {
            BoolQueryBuilder query = QueryBuilders.boolQuery();
            query.must(QueryBuilders.existsQuery(Y9LogSearchConsts.USER_NAME));
            query.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOG_TIME).from(sDay.getTime()).to(eDay.getTime()));
            if (i < longArray.length - 1) {
                query.must(
                    QueryBuilders.rangeQuery(Y9LogSearchConsts.ELAPSED_TIME).from(longArray[i]).to(longArray[i + 1]));
            } else {
                query.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.ELAPSED_TIME).gte(longArray[i]));
            }
            list.add(this.getCountByQueryCondition(query, startDay, endDay));
        }
        return list;
    }

    @Override
    public Page<Y9logAccessLog> page(int page, int rows, String sort) {
        IndexCoordinates index = IndexCoordinates.of(getCurrentYearIndexName());
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        Pageable pageable;

        String tenantId = Y9LoginUserHolder.getTenantId();
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.existsQuery(Y9LogSearchConsts.USER_NAME));
        if (tenantId != null) {
            query.must(QueryBuilders.queryStringQuery(tenantId).field(Y9LogSearchConsts.TENANT_ID));
        }

        if (StringUtils.isNoneBlank(sort)) {
            pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, sort);
        } else {
            pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        }

        NativeSearchQuery searchQuery = searchQueryBuilder.withQuery(query).withPageable(pageable).build();
        // https://www.elastic.co/guide/en/elasticsearch/reference/7.0/search-request-track-total-hits.html
        searchQuery.setTrackTotalHits(true);
        SearchHits<Y9logAccessLog> searchHits =
            elasticsearchOperations.search(searchQuery, Y9logAccessLog.class, index);
        List<Y9logAccessLog> list = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        Page<Y9logAccessLog> pageResult = new PageImpl<>(list, pageable, searchHits.getTotalHits());
        return pageResult;
    }

    @Override
    public Y9Page<AccessLog> pageByCondition(LogInfoModel searchDto, String startTime, String endTime, Integer page,
        Integer rows) {
        IndexCoordinates index = IndexCoordinates.of(createIndexNames(startTime, endTime));
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(searchDto.getLogLevel())) {
            query.must(QueryBuilders.queryStringQuery(searchDto.getLogLevel()).field(Y9LogSearchConsts.LOG_LEVEL));
        }
        if (StringUtils.isNotBlank(searchDto.getSuccess())) {
            query.must(QueryBuilders.queryStringQuery(searchDto.getSuccess()).field(Y9LogSearchConsts.SUCCESS));
        }
        if (StringUtils.isNotBlank(searchDto.getOperateType())) {
            query
                .must(QueryBuilders.queryStringQuery(searchDto.getOperateType()).field(Y9LogSearchConsts.OPERATE_TYPE));
        }
        if (StringUtils.isNotBlank(searchDto.getUserName())) {
            query.must(QueryBuilders.queryStringQuery(searchDto.getUserName()).field(Y9LogSearchConsts.USER_NAME));
        }
        if (StringUtils.isNotBlank(searchDto.getUserHostIp())) {
            query.must(QueryBuilders.queryStringQuery(searchDto.getUserHostIp()).field(Y9LogSearchConsts.USER_HOST_IP));
        }
        if (StringUtils.isNotEmpty(searchDto.getOperateName())) {
            query
                .must(QueryBuilders.queryStringQuery(searchDto.getOperateName()).field(Y9LogSearchConsts.OPERATE_NAME));
        }
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            startTime = startTime + " 00:00:00";
            endTime = endTime + " 23:59:59";
            SimpleDateFormat logDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long startDate = 0;
            try {
                startDate = logDate.parse(startTime).getTime();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            long endDate = 0;
            try {
                endDate = logDate.parse(endTime).getTime();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            query.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOG_TIME).from(startDate).to(endDate));
        }
        query.must(QueryBuilders.queryStringQuery("*").field(Y9LogSearchConsts.USER_NAME));
        Pageable pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.Direction.DESC, Y9LogSearchConsts.LOG_TIME);

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(query).withPageable(pageable).build();
        searchQuery.setTrackTotalHits(true);
        SearchHits<Y9logAccessLog> searchHits =
            elasticsearchOperations.search(searchQuery, Y9logAccessLog.class, index);
        List<Y9logAccessLog> list = searchHits.stream()
            .map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        long total = searchHits.getTotalHits();
        int totalPages = (int)total / rows;
        return Y9Page.success(page, total % rows == 0 ? totalPages : totalPages + 1, total,
            AccessLogModelConvertUtil.logEsListToModels(list));
    }

    @Override
    public Y9Page<AccessLog> pageByOperateType(String operateType, Integer page, Integer rows) {
        IndexCoordinates index = IndexCoordinates.of(getCurrentYearIndexName());
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.queryStringQuery(operateType).field(Y9LogSearchConsts.OPERATE_TYPE));
        Pageable pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.Direction.DESC, Y9LogSearchConsts.LOG_TIME);

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(query).withPageable(pageable).build();
        searchQuery.setTrackTotalHits(true);
        SearchHits<Y9logAccessLog> searchHits =
            elasticsearchOperations.search(searchQuery, Y9logAccessLog.class, index);
        List<Y9logAccessLog> list = searchHits.stream()
            .map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        long total = searchHits.getTotalHits();
        int totalPages = (int)total / rows;
        return Y9Page.success(page, total % rows == 0 ? totalPages : totalPages + 1, total,
            AccessLogModelConvertUtil.logEsListToModels(list));
    }

    @Override
    public Y9Page<AccessLog> pageByOrgType(String tenantId, String orgId, String orgType, String operateType,
        Integer page, Integer rows) {
        IndexCoordinates index = IndexCoordinates.of(getCurrentYearIndexName());
        List<String> ids = new ArrayList<>();
        List<Person> allPersons = new ArrayList<>();
        if (orgType.equals(OrgTypeEnum.DEPARTMENT.getEnName())) {
            allPersons = departmentManager.listAllPersons(tenantId, orgId).getData();
        } else if (orgType.equals(OrgTypeEnum.GROUP.getEnName())) {
            allPersons = groupManager.listPersons(tenantId, orgId).getData();
        } else if (orgType.equals(OrgTypeEnum.POSITION.getEnName())) {
            allPersons = positionManager.listPersons(tenantId, orgId).getData();
        } else if (orgType.equals(OrgTypeEnum.PERSON.getEnName())) {
            allPersons.add(personManager.getPerson(tenantId, orgId).getData());
        }
        for (Person p : allPersons) {
            ids.add(p.getId());
        }
        try {
            if (CollectionUtils.isNotEmpty(ids)) {
                CriteriaQuery criteriaQuery =
                    new CriteriaQuery(new Criteria().and(new Criteria(Y9LogSearchConsts.OPERATE_TYPE).is(operateType))
                        .and(new Criteria(Y9LogSearchConsts.USER_ID).in(ids)))
                        .setPageable(PageRequest.of((page < 1) ? 0 : page - 1, rows))
                        .addSort(Sort.by(Order.desc(Y9LogSearchConsts.LOG_TIME)));

                SearchHits<Y9logAccessLog> searchHits =
                    elasticsearchOperations.search(criteriaQuery, Y9logAccessLog.class, index);
                List<Y9logAccessLog> list =
                    searchHits.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent)
                        .collect(Collectors.toList());

                long total = searchHits.getTotalHits();
                int totalPages = (int)total / rows;
                return Y9Page.success(page, total % rows == 0 ? totalPages : totalPages + 1, total,
                    AccessLogModelConvertUtil.logEsListToModels(list));
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Page<Y9logAccessLog> pageByTenantIdAndManagerLevelAndUserId(String tenantId, String managerLevel,
        String userId, Integer page, Integer rows, String sort) {
        IndexCoordinates index = IndexCoordinates.of(getCurrentYearIndexName());
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        Pageable pageable;

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.existsQuery(Y9LogSearchConsts.USER_NAME));
        if (tenantId != null) {
            queryBuilder.must(QueryBuilders.queryStringQuery(tenantId).field(Y9LogSearchConsts.TENANT_ID));
        }
        if (StringUtils.isNotEmpty(userId)) {
            queryBuilder.must(QueryBuilders.queryStringQuery(userId).field(Y9LogSearchConsts.USER_ID));
        }
        if (StringUtils.isNotEmpty(managerLevel)) {
            queryBuilder.must(QueryBuilders.queryStringQuery(managerLevel).field(Y9LogSearchConsts.MANAGER_LEVEL));
        }
        if (StringUtils.isNoneBlank(sort)) {
            pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, sort);
        } else {
            pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        }

        NativeSearchQuery searchQuery = searchQueryBuilder.withQuery(queryBuilder).withPageable(pageable).build();
        // https://www.elastic.co/guide/en/elasticsearch/reference/7.0/search-request-track-total-hits.html
        searchQuery.setTrackTotalHits(true);
        SearchHits<Y9logAccessLog> searchHits =
            elasticsearchOperations.search(searchQuery, Y9logAccessLog.class, index);
        List<Y9logAccessLog> list = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        Page<Y9logAccessLog> pageResult = new PageImpl<>(list, pageable, searchHits.getTotalHits());
        return pageResult;
    }

    @Override
    public Page<Y9logAccessLog> pageElapsedTimeByCondition(LogInfoModel searchDto, String startDay, String endDay,
        String sTime, String lTime, Integer page, Integer rows) throws ParseException {
        String tenantId = Y9LoginUserHolder.getTenantId();
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.existsQuery(Y9LogSearchConsts.USER_NAME));
        if (StringUtils.isNotBlank(startDay) && StringUtils.isNotBlank(endDay)) {
            Date sDay = Y9Day.getStartOfDay(new SimpleDateFormat("yyyy-MM-dd").parse(startDay));
            Date eDay = Y9Day.getEndOfDay(new SimpleDateFormat("yyyy-MM-dd").parse(endDay));
            query.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOG_TIME).from(sDay.getTime()).to(eDay.getTime()));
        }
        if (!getTenantType(tenantId).equals(1)) {
            query.must(QueryBuilders.queryStringQuery(tenantId).field(Y9LogSearchConsts.TENANT_ID));
        }
        if (StringUtils.isNotBlank(searchDto.getLogLevel())) {
            query.must(QueryBuilders.queryStringQuery(searchDto.getLogLevel()).field(Y9LogSearchConsts.LOG_LEVEL));
        }
        if (StringUtils.isNotBlank(searchDto.getSuccess())) {
            query.must(QueryBuilders.queryStringQuery(searchDto.getSuccess()).field(Y9LogSearchConsts.SUCCESS));
        }
        if (StringUtils.isNotBlank(searchDto.getOperateType())) {
            query
                .must(QueryBuilders.queryStringQuery(searchDto.getOperateType()).field(Y9LogSearchConsts.OPERATE_TYPE));
        }
        if (StringUtils.isNotBlank(sTime)) {
            long smallTime = Long.parseLong(sTime);
            if (StringUtils.isNotBlank(lTime)) {
                long largeTime = Long.parseLong(lTime);
                query.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.ELAPSED_TIME).from(smallTime).to(largeTime));
            } else {
                query.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.ELAPSED_TIME).gt(smallTime));
            }
        }
        Page<Y9logAccessLog> esPage = getSearchList(query, page, rows, startDay, endDay);
        return esPage;
    }

    @Override
    public Page<Y9logAccessLog> pageOperateStatusByOperateStatus(LogInfoModel searchDto, String operateStatus,
        String date, String hour, Integer page, Integer rows) throws ParseException {
        String tenantId = Y9LoginUserHolder.getTenantId();
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.existsQuery(Y9LogSearchConsts.USER_NAME));
        if (StringUtils.isNotBlank(operateStatus)) {
            query.must(QueryBuilders.queryStringQuery(operateStatus).field(Y9LogSearchConsts.SUCCESS));
        }
        if (StringUtils.isNotBlank(date) && StringUtils.isNotBlank(hour)) {
            int h = Integer.parseInt(hour);
            Calendar cal = Calendar.getInstance();
            Date day = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            Date dat = Y9Day.getStartOfDay(day);
            cal.setTime(dat);
            cal.add(Calendar.HOUR_OF_DAY, h);
            Date startOfTime = cal.getTime();
            cal.add(Calendar.MINUTE, 59);
            cal.add(Calendar.SECOND, 59);
            Date endOfTime = cal.getTime();
            query.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOG_TIME).from(startOfTime.getTime())
                .to(endOfTime.getTime()));
        }
        if (!getTenantType(tenantId).equals(1)) {
            query.must(QueryBuilders.queryStringQuery(tenantId).field(Y9LogSearchConsts.TENANT_ID));
        }
        if (StringUtils.isNotBlank(searchDto.getUserName())) {
            query.must(QueryBuilders.queryStringQuery(searchDto.getUserName()).field(Y9LogSearchConsts.USER_NAME));
        }
        if (StringUtils.isNotBlank(searchDto.getTenantName())) {
            query.must(QueryBuilders.queryStringQuery(searchDto.getTenantName()).field(Y9LogSearchConsts.TENANT_NAME));
        }
        if (StringUtils.isNotBlank(searchDto.getLogLevel())) {
            query.must(QueryBuilders.queryStringQuery(searchDto.getLogLevel()).field(Y9LogSearchConsts.LOG_LEVEL));
        }
        if (StringUtils.isNotBlank(searchDto.getOperateType())) {
            query
                .must(QueryBuilders.queryStringQuery(searchDto.getOperateType()).field(Y9LogSearchConsts.OPERATE_TYPE));
        }
        return getSearchList(query, page, rows, date, null);
    }

    @Override
    public Page<Y9logAccessLog> pageSearchByCondition(LogInfoModel searchDto, String startTime, String endTime,
        Integer page, Integer rows) {
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (!getTenantType(tenantId).equals(1)) {
            query.must(QueryBuilders.queryStringQuery(tenantId).field(Y9LogSearchConsts.TENANT_ID));
        }
        if (StringUtils.isNotBlank(searchDto.getLogLevel())) {
            query.must(QueryBuilders.queryStringQuery(searchDto.getLogLevel()).field(Y9LogSearchConsts.LOG_LEVEL));
        }
        if (StringUtils.isNotBlank(searchDto.getSuccess())) {
            query.must(QueryBuilders.queryStringQuery(searchDto.getSuccess()).field(Y9LogSearchConsts.SUCCESS));
        }
        if (StringUtils.isNotBlank(searchDto.getOperateType())) {
            query
                .must(QueryBuilders.queryStringQuery(searchDto.getOperateType()).field(Y9LogSearchConsts.OPERATE_TYPE));
        }
        if (StringUtils.isNotBlank(searchDto.getUserName())) {
            query.must(QueryBuilders.queryStringQuery(searchDto.getUserName()).field(Y9LogSearchConsts.USER_NAME));
        }
        if (StringUtils.isNotBlank(searchDto.getUserHostIp())) {
            query.must(QueryBuilders.queryStringQuery(searchDto.getUserHostIp()).field(Y9LogSearchConsts.USER_HOST_IP));
        }
        if (StringUtils.isNotBlank(searchDto.getTenantName())) {
            query.must(QueryBuilders.queryStringQuery(searchDto.getTenantName()).field(Y9LogSearchConsts.TENANT_NAME));
        }
        if (StringUtils.isNotBlank(searchDto.getModularName())) {
            query.must(QueryBuilders.boolQuery().should(
                QueryBuilders.wildcardQuery(Y9LogSearchConsts.MODULAR_NAME, "*" + searchDto.getModularName() + "*")));
        }
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            startTime = startTime + " 00:00:00";
            endTime = endTime + " 23:59:59";
            SimpleDateFormat logDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long startDate = 0L;
            long endDate = 0L;
            try {
                startDate = logDate.parse(startTime).getTime();
                endDate = logDate.parse(endTime).getTime();
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
            query.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOG_TIME).from(startDate).to(endDate));
        }
        query.must(QueryBuilders.existsQuery(Y9LogSearchConsts.USER_NAME));
        Page<Y9logAccessLog> esPage = getSearchList(query, page, rows, startTime, endTime);
        return esPage;
    }

    @Override
    public void save(Y9logAccessLog y9logAccessLog) {
        elasticsearchOperations.save(y9logAccessLog);
    }

    @Override
    public Page<Y9logAccessLog> searchQuery(String tenantId, String managerLevel, LogInfoModel loginInfoModel,
        Integer page, Integer rows) {
        IndexCoordinates index = IndexCoordinates.of(getCurrentYearIndexName());
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        Pageable pageable;

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        // queryBuilder.must(QueryBuilders.existsQuery(USER_NAME));
        if (StringUtils.isNotBlank(tenantId)) {
            queryBuilder.must(QueryBuilders.queryStringQuery(tenantId).field(Y9LogSearchConsts.TENANT_ID));
        }
        if (StringUtils.isNotEmpty(managerLevel)) {
            queryBuilder.must(QueryBuilders.queryStringQuery(managerLevel).field(Y9LogSearchConsts.MANAGER_LEVEL));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getUserName())) {
            queryBuilder.must(QueryBuilders.boolQuery().should(
                QueryBuilders.wildcardQuery(Y9LogSearchConsts.USER_NAME, "*" + loginInfoModel.getUserName() + "*")));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getUserHostIp())) {
            queryBuilder.must(QueryBuilders.boolQuery().should(QueryBuilders
                .wildcardQuery(Y9LogSearchConsts.USER_HOST_IP, "*" + loginInfoModel.getUserHostIp() + "*")));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getModularName())) {
            queryBuilder.must(QueryBuilders.boolQuery().should(QueryBuilders
                .wildcardQuery(Y9LogSearchConsts.MODULAR_NAME, "*" + loginInfoModel.getModularName() + "*")));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getOperateName())) {
            queryBuilder.must(QueryBuilders.boolQuery().should(QueryBuilders
                .wildcardQuery(Y9LogSearchConsts.OPERATE_NAME, "*" + loginInfoModel.getOperateName() + "*")));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getOperateType())) {
            queryBuilder.must(
                QueryBuilders.queryStringQuery(loginInfoModel.getOperateType()).field(Y9LogSearchConsts.OPERATE_TYPE));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getSuccess())) {
            queryBuilder
                .must(QueryBuilders.queryStringQuery(loginInfoModel.getSuccess()).field(Y9LogSearchConsts.SUCCESS));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getLogLevel())) {
            queryBuilder
                .must(QueryBuilders.queryStringQuery(loginInfoModel.getLogLevel()).field(Y9LogSearchConsts.LOG_LEVEL));
        }
        if (StringUtils.isNotBlank(loginInfoModel.getStartTime())
            && StringUtils.isNotBlank(loginInfoModel.getEndTime())) {
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
                queryBuilder.must(QueryBuilders.rangeQuery(Y9LogSearchConsts.LOG_TIME).from(s).to(e)
                    .format("yyyy-MM-dd'T'HH:mm:ss'Z'"));
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }

        if (StringUtils.isNotBlank(loginInfoModel.getSortName())) {
            pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, loginInfoModel.getSortName());
        } else {
            pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Direction.DESC, Y9LogSearchConsts.LOG_TIME);
        }

        NativeSearchQuery searchQuery = searchQueryBuilder.withQuery(queryBuilder).withPageable(pageable).build();
        // https://www.elastic.co/guide/en/elasticsearch/reference/7.0/search-request-track-total-hits.html
        searchQuery.setTrackTotalHits(true);
        SearchHits<Y9logAccessLog> searchHits =
            elasticsearchOperations.search(searchQuery, Y9logAccessLog.class, index);
        List<Y9logAccessLog> list = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        return new PageImpl<>(list, pageable, searchHits.getTotalHits());
    }
}
