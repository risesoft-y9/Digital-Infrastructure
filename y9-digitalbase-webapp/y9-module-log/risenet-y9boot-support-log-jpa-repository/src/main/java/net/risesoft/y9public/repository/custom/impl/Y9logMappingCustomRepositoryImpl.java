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

        return y9logMappingRepository.findAll(new Specification<Y9logMapping>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9logMapping> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> list = predicate.getExpressions();

                if (StringUtils.isNotBlank(modularCnName)) {
                    list.add(criteriaBuilder.equal(root.get("modularCnName").as(String.class), modularCnName));
                }
                if (StringUtils.isNotBlank(modularName)) {
                    list.add(criteriaBuilder.equal(root.get("modularName").as(String.class), modularName));
                }

                return predicate;
            }
        }, pageable);
    }
}
