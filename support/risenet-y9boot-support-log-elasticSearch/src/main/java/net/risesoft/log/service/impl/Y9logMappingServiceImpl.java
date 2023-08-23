package net.risesoft.log.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
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
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.log.entity.Y9logMapping;
import net.risesoft.log.repository.Y9logMappingRepository;
import net.risesoft.log.service.Y9logMappingService;

/**
 *
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
@Service
@RequiredArgsConstructor
public class Y9logMappingServiceImpl implements Y9logMappingService {
    private final Y9logMappingRepository y9logMappingRepository;
    private final ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public void deleteFieldMapping(String id) {
        y9logMappingRepository.deleteById(id);
    }

    @Override
    public String getCnModularName(String modularName) {
        List<Y9logMapping> list = y9logMappingRepository.findByModularName(modularName);
        if (list.isEmpty()) {
            return "";
        }
        return list.get(0).getModularCnName();
    }

    @Override
    public Y9logMapping getFieldMappingEntity(String id) {
        return y9logMappingRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Y9logMapping> page(int page, int rows, String sort) {
        if (sort != null && !"".equals(sort)) {
            Pageable pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.by(Sort.Direction.DESC, sort));
            return y9logMappingRepository.findAll(pageable);
        }
        Pageable pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.by(Sort.Direction.DESC, "modularName"));
        return y9logMappingRepository.findAll(pageable);
    }

    @Override
    public Page<Y9logMapping> pageSearchList(Integer page, Integer rows, String modularName, String modularCnName) {
        Sort sort = Sort.by(Sort.Direction.DESC, "modularCnName");
        Pageable pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, sort);
        Criteria criteria = new Criteria();
        if (StringUtils.isNotBlank(modularCnName)) {
            criteria.and("modularCnName").contains(modularCnName);
        }
        if (StringUtils.isNotBlank(modularName)) {
            criteria.and("modularName").contains(modularName);
        }
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        query.setTrackTotalHits(true);
        SearchHits<Y9logMapping> search = elasticsearchTemplate.search(query, Y9logMapping.class);
        List<Y9logMapping> list = search.stream().map(SearchHit::getContent).collect(Collectors.toList());
        Page<Y9logMapping> pageResult = new PageImpl<>(list, pageable, search.getTotalHits());
        return pageResult;
    }

    @Override
    public void save(Y9logMapping y9logMapping) {
        y9logMappingRepository.save(y9logMapping);
    }

    @Override
    public List<Y9logMapping> validateName(String name) {
        return y9logMappingRepository.findByModularName(name);
    }

}
