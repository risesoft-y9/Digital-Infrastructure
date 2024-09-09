package net.risesoft.y9public.repository.custom.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

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
@RequiredArgsConstructor
public class Y9logIpDeptMappingCustomRepositoryImpl implements Y9logIpDeptMappingCustomRepository {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public Y9Page<Y9logIpDeptMapping> pageSearchList(int page, int rows, String clientIpSection, String deptName) {
        Criteria criteria = new Criteria();
        if (StringUtils.isNotBlank(deptName)) {
            criteria.subCriteria(new Criteria("deptName").contains(deptName));
        }
        if (StringUtils.isNotBlank(clientIpSection)) {
            criteria.subCriteria(new Criteria("clientIpSection").contains(clientIpSection));
        }
        Query query = new CriteriaQueryBuilder(criteria).withPageable(PageRequest.of((page < 1) ? 0 : page - 1, rows))
            .withSort(Sort.by(Direction.ASC, "clientIpSection")).build();
        SearchHits<Y9logIpDeptMapping> search = elasticsearchOperations.search(query, Y9logIpDeptMapping.class);
        List<Y9logIpDeptMapping> list = search.stream().map(SearchHit::getContent).collect(Collectors.toList());
        long total = search.getTotalHits();
        int totalPages = (int)total / rows;
        return Y9Page.success(page, total % rows == 0 ? totalPages : totalPages + 1, total, list);
    }

}
