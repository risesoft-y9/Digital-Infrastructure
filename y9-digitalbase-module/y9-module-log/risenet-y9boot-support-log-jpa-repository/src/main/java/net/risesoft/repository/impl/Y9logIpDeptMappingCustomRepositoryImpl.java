package net.risesoft.repository.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.InitDataConsts;
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

    private final Y9LogIpDeptMappingRepository y9logIpDeptMappingRepository;

    @Override
    public Y9Page<Y9LogIpDeptMappingDO> pageSearchList(int page, int rows, String clientIp4Abc, String deptName) {

        Pageable pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.by(Sort.Direction.ASC, "clientIpSection"));

        Page<Y9LogIpDeptMapping> pageInfo = y9logIpDeptMappingRepository.findAll(new Specification<>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9LogIpDeptMapping> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> list = predicate.getExpressions();

                if (StringUtils.isNotBlank(deptName)) {
                    list.add(criteriaBuilder.like(root.get("deptName").as(String.class), "*" + deptName + "*"));
                }
                if (StringUtils.isNotBlank(clientIp4Abc)) {
                    list.add(
                        criteriaBuilder.like(root.get("clientIpSection").as(String.class), "*" + clientIp4Abc + "*"));
                }

                String tenantId = Y9LoginUserHolder.getTenantId();
                if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
                    list.add(criteriaBuilder.equal(root.get("tenantId").as(String.class), tenantId));
                }
                return predicate;
            }
        }, pageable);
        List<Y9LogIpDeptMappingDO> list = pageInfo.getContent()
            .stream()
            .map(m -> Y9ModelConvertUtil.convert(m, Y9LogIpDeptMappingDO.class))
            .collect(Collectors.toList());
        return Y9Page.success(page, pageInfo.getTotalPages(), pageInfo.getTotalElements(), list);
    }

    @Override
    public List<Y9LogIpDeptMappingDO> findByClientIpSection(String clientIpSection) {
        return y9logIpDeptMappingRepository.findByClientIpSection(clientIpSection)
            .stream()
            .map(m -> Y9ModelConvertUtil.convert(m, Y9LogIpDeptMappingDO.class))
            .collect(Collectors.toList());
    }

    @Override
    public List<Y9LogIpDeptMappingDO> findByTenantIdAndClientIpSection(String tenantId, String clientIpSection) {
        return y9logIpDeptMappingRepository.findByTenantIdAndClientIpSection(tenantId, clientIpSection)
            .stream()
            .map(m -> Y9ModelConvertUtil.convert(m, Y9LogIpDeptMappingDO.class))
            .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        y9logIpDeptMappingRepository.deleteById(id);
    }

    @Override
    public Y9LogIpDeptMappingDO save(Y9LogIpDeptMappingDO y9LogIpDeptMappingDO) {
        Y9LogIpDeptMapping y9LogIpDeptMapping =
            Y9ModelConvertUtil.convert(y9LogIpDeptMappingDO, Y9LogIpDeptMapping.class);
        return Y9ModelConvertUtil.convert(y9logIpDeptMappingRepository.save(y9LogIpDeptMapping),
            Y9LogIpDeptMappingDO.class);
    }

    @Override
    public List<Y9LogIpDeptMappingDO> findByTenantId(String tenantId, Sort sort) {
        return y9logIpDeptMappingRepository.findByTenantId(tenantId, sort)
            .stream()
            .map(m -> Y9ModelConvertUtil.convert(m, Y9LogIpDeptMappingDO.class))
            .collect(Collectors.toList());
    }

    @Override
    public List<Y9LogIpDeptMappingDO> findAll(Sort sort) {
        return y9logIpDeptMappingRepository.findAll(sort)
            .stream()
            .map(m -> Y9ModelConvertUtil.convert(m, Y9LogIpDeptMappingDO.class))
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Y9LogIpDeptMappingDO> findById(String id) {
        return y9logIpDeptMappingRepository.findById(id)
            .map(m -> Y9ModelConvertUtil.convert(m, Y9LogIpDeptMappingDO.class));
    }

    @Override
    public Page<Y9LogIpDeptMappingDO> page(Pageable pageable) {
        Page<Y9LogIpDeptMapping> page = y9logIpDeptMappingRepository.findAll(pageable);
        List<Y9LogIpDeptMappingDO> list = page.getContent()
            .stream()
            .map(m -> Y9ModelConvertUtil.convert(m, Y9LogIpDeptMappingDO.class))
            .collect(Collectors.toList());
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

}
