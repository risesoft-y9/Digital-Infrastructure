package net.risesoft.y9public.repository.custom.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

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
@DependsOn(value = "elasticsearchTemplate")
public class Y9logMappingCustomRepositoryImpl implements Y9logMappingCustomRepository {
    private final Y9logMappingRepository y9logMappingRepository;
    private final ElasticsearchTemplate elasticsearchTemplate;

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
        Sort sort = Sort.by(Sort.Direction.DESC, "modularCnName");
        Pageable pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, sort);
        Criteria criteria = new Criteria();
        if (StringUtils.isNotBlank(modularCnName)) {
            criteria.subCriteria(new Criteria("modularCnName").contains(modularCnName));
        }
        if (StringUtils.isNotBlank(modularName)) {
            criteria.subCriteria(new Criteria("modularName").contains(modularName));
        }
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);
        SearchHits<Y9logMapping> search = elasticsearchTemplate.search(query, Y9logMapping.class);
        List<Y9logMapping> list = search.stream().map(SearchHit::getContent).collect(Collectors.toList());
        Page<Y9logMapping> pageResult = new PageImpl<>(list, pageable, search.getTotalHits());
        return pageResult;
    }

}
