package net.risesoft.repository.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import net.risesoft.consts.InitDataConsts;
import net.risesoft.log.constant.Y9LogSearchConsts;
import net.risesoft.log.domain.Y9LogIpDeptMappingDO;
import net.risesoft.log.repository.Y9logIpDeptMappingCustomRepository;
import net.risesoft.pojo.Y9Page;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.Y9LogIpDeptMapping;
import net.risesoft.y9public.repository.Y9LogIpDeptMappingRepository;

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

    private final Y9LogIpDeptMappingRepository y9logIpDeptMappingRepository;

    @Override
    public Y9Page<Y9LogIpDeptMappingDO> pageSearchList(int page, int rows, String clientIpSection, String deptName) {
        Criteria criteria = new Criteria();
        if (StringUtils.isNotBlank(deptName)) {
            criteria.subCriteria(new Criteria("deptName").contains(deptName));
        }
        if (StringUtils.isNotBlank(clientIpSection)) {
            criteria.subCriteria(new Criteria("clientIpSection").contains(clientIpSection));
        }
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
            criteria.subCriteria(new Criteria(Y9LogSearchConsts.TENANT_ID).is(tenantId));
        }
        Query query = new CriteriaQueryBuilder(criteria).withPageable(PageRequest.of((page < 1) ? 0 : page - 1, rows))
            .withSort(Sort.by(Direction.ASC, "clientIpSection"))
            .build();
        SearchHits<Y9LogIpDeptMapping> search = elasticsearchOperations.search(query, Y9LogIpDeptMapping.class);
        List<Y9LogIpDeptMappingDO> list = search.stream()
            .map(SearchHit::getContent)
            .map(m -> Y9ModelConvertUtil.convert(m, Y9LogIpDeptMappingDO.class))
            .collect(Collectors.toList());
        long total = search.getTotalHits();
        int totalPages = (int)total / rows;
        return Y9Page.success(page, total % rows == 0 ? totalPages : totalPages + 1, total, list);
    }

    @Override
    public List<Y9LogIpDeptMappingDO> findByClientIpSection(String clientIpSection) {
        List<Y9LogIpDeptMapping> y9LogIpDeptMappingList =
            y9logIpDeptMappingRepository.findByClientIpSection(clientIpSection);
        return Y9ModelConvertUtil.convert(y9LogIpDeptMappingList, Y9LogIpDeptMappingDO.class);
    }

    @Override
    public List<Y9LogIpDeptMappingDO> findByTenantIdAndClientIpSection(String tenantId, String clientIpSection) {
        List<Y9LogIpDeptMapping> y9LogIpDeptMappingList =
            y9logIpDeptMappingRepository.findByTenantIdAndClientIpSection(tenantId, clientIpSection);
        return Y9ModelConvertUtil.convert(y9LogIpDeptMappingList, Y9LogIpDeptMappingDO.class);
    }

    @Override
    public void deleteById(String id) {
        y9logIpDeptMappingRepository.deleteById(id);
    }

    @Override
    public Y9LogIpDeptMappingDO save(Y9LogIpDeptMappingDO y9LogIpDeptMappingDO) {
        Y9LogIpDeptMapping y9logIpDeptMapping =
            Y9ModelConvertUtil.convert(y9LogIpDeptMappingDO, Y9LogIpDeptMapping.class);
        return Y9ModelConvertUtil.convert(y9logIpDeptMappingRepository.save(y9logIpDeptMapping),
            Y9LogIpDeptMappingDO.class);
    }

    @Override
    public List<Y9LogIpDeptMappingDO> findByTenantId(String tenantId, Sort sort) {
        List<Y9LogIpDeptMapping> y9LogIpDeptMappingList = y9logIpDeptMappingRepository.findByTenantId(tenantId, sort);
        return Y9ModelConvertUtil.convert(y9LogIpDeptMappingList, Y9LogIpDeptMappingDO.class);
    }

    @Override
    public List<Y9LogIpDeptMappingDO> findAll(Sort sort) {
        return StreamSupport.stream(y9logIpDeptMappingRepository.findAll(sort).spliterator(), false)
            .map(m -> Y9ModelConvertUtil.convert(m, Y9LogIpDeptMappingDO.class))
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Y9LogIpDeptMappingDO> findById(String id) {
        return y9logIpDeptMappingRepository.findById(id)
            .map(y9LogIpDeptMapping -> Y9ModelConvertUtil.convert(y9LogIpDeptMapping, Y9LogIpDeptMappingDO.class));
    }

    @Override
    public Page<Y9LogIpDeptMappingDO> page(Pageable pageable) {
        Page<Y9LogIpDeptMapping> ipDeptMappingPage = y9logIpDeptMappingRepository.findAll(pageable);
        List<Y9LogIpDeptMappingDO> list = ipDeptMappingPage.getContent()
            .stream()
            .map(m -> Y9ModelConvertUtil.convert(m, Y9LogIpDeptMappingDO.class))
            .collect(Collectors.toList());
        return new PageImpl<>(list, ipDeptMappingPage.getPageable(), ipDeptMappingPage.getTotalElements());
    }

}
