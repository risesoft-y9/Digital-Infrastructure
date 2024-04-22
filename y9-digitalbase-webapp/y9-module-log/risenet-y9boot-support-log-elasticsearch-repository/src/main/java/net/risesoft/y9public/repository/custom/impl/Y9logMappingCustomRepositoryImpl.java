package net.risesoft.y9public.repository.custom.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import net.risesoft.log.constant.Y9ESIndexConst;
import net.risesoft.y9public.entity.Y9logMapping;
import net.risesoft.y9public.repository.Y9logMappingRepository;
import net.risesoft.y9public.repository.custom.Y9logMappingCustomRepository;

/**
 *
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
@Component
@RequiredArgsConstructor
public class Y9logMappingCustomRepositoryImpl implements Y9logMappingCustomRepository {

    private final Y9logMappingRepository y9logMappingRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public String getCnModularName(String modularName) {
        List<Y9logMapping> list = y9logMappingRepository.findByModularName(modularName);
        if (list.isEmpty()) {
            return "";
        }
        return list.get(0).getModularCnName();
    }

    @Override
    public Page<Y9logMapping> pageSearchList(Integer page, Integer rows, String modularName, String modularCnName) {
        IndexCoordinates index = IndexCoordinates.of(Y9ESIndexConst.LOG_MAPPING_INDEX);
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        Sort sort = Sort.by(Sort.Direction.DESC, "modularCnName");
        Pageable pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, sort);

        BoolQueryBuilder query = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(modularCnName)) {
            query.must(QueryBuilders.termQuery("modularCnName", modularCnName));
        }
        if (StringUtils.isNotBlank(modularName)) {
            query.must(QueryBuilders.termQuery("modularName", modularName));
        }
        NativeSearchQuery searchQuery = searchQueryBuilder.withQuery(query).withPageable(pageable).build();
        searchQuery.setTrackTotalHits(true);
        SearchHits<Y9logMapping> searchHits = elasticsearchOperations.search(searchQuery, Y9logMapping.class, index);
        List<Y9logMapping> list = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        Page<Y9logMapping> pageResult = new PageImpl<>(list, pageable, searchHits.getTotalHits());
        return pageResult;
    }
}
