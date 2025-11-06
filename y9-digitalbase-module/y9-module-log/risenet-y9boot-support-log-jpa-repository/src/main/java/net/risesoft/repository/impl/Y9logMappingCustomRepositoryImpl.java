package net.risesoft.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
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

import net.risesoft.log.domain.Y9LogMappingDO;
import net.risesoft.log.repository.Y9logMappingCustomRepository;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.Y9LogMapping;
import net.risesoft.y9public.repository.Y9LogMappingRepository;

/**
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 */
@Component
@RequiredArgsConstructor
public class Y9logMappingCustomRepositoryImpl implements Y9logMappingCustomRepository {

    private final Y9LogMappingRepository y9logMappingRepository;

    private static PageImpl<Y9LogMappingDO> poPageToDoPage(Page<Y9LogMapping> logMappingPage) {
        List<Y9LogMappingDO> list = logMappingPage.getContent()
            .stream()
            .map(l -> Y9ModelConvertUtil.convert(l, Y9LogMappingDO.class))
            .collect(Collectors.toList());
        return new PageImpl<>(list, logMappingPage.getPageable(), logMappingPage.getTotalElements());
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
    public List<Y9LogMappingDO> findByModularName(String name) {
        return y9logMappingRepository.findByModularName(name)
            .stream()
            .map(m -> Y9ModelConvertUtil.convert(m, Y9LogMappingDO.class))
            .collect(Collectors.toList());
    }

    @Override
    public String getCnModularName(String modularName) {
        List<Y9LogMapping> list = y9logMappingRepository.findByModularName(modularName);
        if (list.isEmpty()) {
            return "";
        }
        return list.get(0).getModularCnName();
    }

    @Override
    public Page<Y9LogMappingDO> page(Pageable pageable) {
        Page<Y9LogMapping> y9LogMappingPage = y9logMappingRepository.findAll(pageable);
        return poPageToDoPage(y9LogMappingPage);
    }

    @Override
    public Page<Y9LogMappingDO> pageSearchList(Integer page, Integer rows, String modularName, String modularCnName) {
        Sort sort = Sort.by(Sort.Direction.DESC, "modularCnName");
        Pageable pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, sort);

        Page<Y9LogMapping> y9LogMappingPage = y9logMappingRepository.findAll(new Specification<>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9LogMapping> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();

                if (StringUtils.isNotBlank(modularCnName)) {
                    list.add(criteriaBuilder.equal(root.get("modularCnName").as(String.class), modularCnName));
                }
                if (StringUtils.isNotBlank(modularName)) {
                    list.add(criteriaBuilder.equal(root.get("modularName").as(String.class), modularName));
                }

                // 如果没有条件，返回空查询
                if (list.isEmpty()) {
                    return criteriaBuilder.conjunction(); // 相当于 WHERE 1=1
                }
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        }, pageable);
        return poPageToDoPage(y9LogMappingPage);
    }

    @Override
    public void save(Y9LogMappingDO y9LogMappingDO) {
        Y9LogMapping y9LogMapping = Y9ModelConvertUtil.convert(y9LogMappingDO, Y9LogMapping.class);
        y9logMappingRepository.save(y9LogMapping);
    }
}
