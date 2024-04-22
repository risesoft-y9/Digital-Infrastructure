package net.risesoft.y9public.repository.custom.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.log.constant.Y9ESIndexConst;
import net.risesoft.pojo.Y9Page;
import net.risesoft.y9public.entity.Y9logIpDeptMapping;
import net.risesoft.y9public.repository.custom.Y9logIpDeptMappingCustomRepository;

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
public class Y9logIpDeptMappingCustomRepositoryImpl implements Y9logIpDeptMappingCustomRepository {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public Y9Page<Y9logIpDeptMapping> pageSearchList(int page, int rows, String clientIp4Abc, String deptName) {
        IndexCoordinates index = IndexCoordinates.of(Y9ESIndexConst.IP_DEPT_MAPPING_INDEX);

        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.existsQuery("clientIpSection"));
        if (StringUtils.isNotBlank(deptName)) {
            query.must(QueryBuilders.wildcardQuery("deptName", "*" + deptName + "*"));
        }
        if (StringUtils.isNotBlank(clientIp4Abc)) {
            query.must(QueryBuilders.wildcardQuery("clientIpSection", "*" + clientIp4Abc + "*"));
        }
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(query)
            .withPageable(PageRequest.of((page < 1) ? 0 : page - 1, rows))
            .withSorts(SortBuilders.fieldSort("clientIpSection").order(SortOrder.ASC)).build();
        SearchHits<Y9logIpDeptMapping> searchHits =
            elasticsearchOperations.search(searchQuery, Y9logIpDeptMapping.class, index);

        List<Y9logIpDeptMapping> list = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        long total = searchHits.getTotalHits();
        int totalPages = (int)total / rows;
        return Y9Page.success(page, total % rows == 0 ? totalPages : totalPages + 1, total, list);
    }

}
