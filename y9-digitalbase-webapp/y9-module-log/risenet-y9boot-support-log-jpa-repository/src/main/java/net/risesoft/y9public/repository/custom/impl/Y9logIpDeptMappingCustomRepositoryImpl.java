package net.risesoft.y9public.repository.custom.impl;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import net.risesoft.pojo.Y9Page;
import net.risesoft.y9public.entity.Y9logIpDeptMapping;
import net.risesoft.y9public.repository.Y9logIpDeptMappingRepository;
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

    private final Y9logIpDeptMappingRepository y9logIpDeptMappingRepository;

    @Override
    public Y9Page<Y9logIpDeptMapping> pageSearchList(int page, int rows, String clientIp4Abc, String deptName) {

        Pageable pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.by(Sort.Direction.ASC, "clientIpSection"));

        Page<Y9logIpDeptMapping> pageInfo =
            y9logIpDeptMappingRepository.findAll(new Specification<Y9logIpDeptMapping>() {
                private static final long serialVersionUID = -2210269486911993525L;

                @Override
                public Predicate toPredicate(Root<Y9logIpDeptMapping> root, CriteriaQuery<?> query,
                    CriteriaBuilder criteriaBuilder) {
                    Predicate predicate = criteriaBuilder.conjunction();
                    List<Expression<Boolean>> list = predicate.getExpressions();

                    if (StringUtils.isNotBlank(deptName)) {
                        list.add(criteriaBuilder.equal(root.get("deptName").as(String.class), "*" + deptName + "*"));
                    }
                    if (StringUtils.isNotBlank(clientIp4Abc)) {
                        list.add(criteriaBuilder.equal(root.get("clientIpSection").as(String.class),
                            "*" + clientIp4Abc + "*"));
                    }

                    return predicate;
                }
            }, pageable);
        return Y9Page.success(page, pageInfo.getTotalPages(), pageInfo.getTotalElements(), pageInfo.getContent());
    }

}
