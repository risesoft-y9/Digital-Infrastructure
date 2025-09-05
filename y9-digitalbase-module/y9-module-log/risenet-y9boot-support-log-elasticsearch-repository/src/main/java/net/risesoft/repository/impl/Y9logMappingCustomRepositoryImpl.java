package net.risesoft.repository.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import net.risesoft.log.domain.Y9LogMappingDO;
import net.risesoft.log.repository.Y9logMappingCustomRepository;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.Y9LogMapping;
import net.risesoft.y9public.repository.Y9LogMappingRepository;

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

    private final Y9LogMappingRepository y9logMappingRepository;

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public String getCnModularName(String modularName) {
        List<Y9LogMapping> list = y9logMappingRepository.findByModularName(modularName);
        if (list.isEmpty()) {
            return "";
        }
        return list.get(0).getModularCnName();
    }

    @Override
    public void deleteById(String id) {
        y9logMappingRepository.deleteById(id);
    }

    @Override
    public Optional<Y9LogMappingDO> findById(String id) {
        return y9logMappingRepository.findById(id).map(m -> Y9ModelConvertUtil.convert(m, Y9LogMappingDO.class));
    }

    @Override
    public void save(Y9LogMappingDO y9LogMappingDO) {
        Y9LogMapping y9LogMapping = Y9ModelConvertUtil.convert(y9LogMappingDO, Y9LogMapping.class);
        y9logMappingRepository.save(y9LogMapping);
    }

    @Override
    public List<Y9LogMappingDO> findByModularName(String name) {
        return y9logMappingRepository.findByModularName(name)
            .stream()
            .map(m -> Y9ModelConvertUtil.convert(m, Y9LogMappingDO.class))
            .collect(Collectors.toList());
    }

    @Override
    public Page<Y9LogMappingDO> page(Pageable pageable) {
        Page<Y9LogMapping> y9LogMappingPage = y9logMappingRepository.findAll(pageable);
        List<Y9LogMappingDO> list = y9LogMappingPage.getContent()
            .stream()
            .map(m -> Y9ModelConvertUtil.convert(m, Y9LogMappingDO.class))
            .collect(Collectors.toList());
        return new PageImpl<>(list, y9LogMappingPage.getPageable(), y9LogMappingPage.getTotalElements());
    }

    @Override
    public Page<Y9LogMappingDO> pageSearchList(Integer page, Integer rows, String modularName, String modularCnName) {
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
        SearchHits<Y9LogMapping> search = elasticsearchOperations.search(query, Y9LogMapping.class);
        List<Y9LogMappingDO> list = search.stream()
            .map(SearchHit::getContent)
            .map(m -> Y9ModelConvertUtil.convert(m, Y9LogMappingDO.class))
            .collect(Collectors.toList());
        return new PageImpl<>(list, pageable, search.getTotalHits());
    }

}
