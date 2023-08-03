package net.risesoft.y9public.service.event.impl;

import java.util.Date;
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
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.y9public.entity.event.Y9PublishedEvent;
import net.risesoft.y9public.repository.event.Y9PublishedEventRepository;
import net.risesoft.y9public.service.event.Y9PublishedEventService;
import net.risesoft.y9public.specification.Y9PublishedEventSpecification;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service
@RequiredArgsConstructor
public class Y9PublishedEventServiceImpl implements Y9PublishedEventService {

    private final Y9PublishedEventRepository y9PublishedEventRepository;

    @Override
    public List<Y9PublishedEvent> listByTenantId(String tenantId) {
        Sort sort = Sort.by(Sort.Direction.ASC, "createTime");
        return y9PublishedEventRepository.findByTenantId(tenantId, sort);
    }

    @Override
    public List<Y9PublishedEvent> listByTenantId(String tenantId, Date startTime) {
        Sort sort = Sort.by(Sort.Direction.ASC, "createTime");
        return y9PublishedEventRepository.findAll(new Y9PublishedEventSpecification(tenantId, startTime) {
            private static final long serialVersionUID = 4690624487610572887L;

            @Override
            public Predicate toPredicate(Root<Y9PublishedEvent> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> expressions = predicate.getExpressions();
                if (StringUtils.isNotBlank(tenantId)) {
                    expressions.add(criteriaBuilder.equal(root.<String>get("tenantId"), tenantId));
                }
                if (startTime != null) {
                    expressions.add(criteriaBuilder.greaterThanOrEqualTo(root.<Date>get("createTime"), startTime));
                }
                return predicate;
            }
        }, sort);
    }

    @Override
    public Page<Y9PublishedEvent> page(int page, int rows, String eventName, String eventDescription, Date startTime,
        Date endTime) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, Sort.by(Sort.Direction.DESC, "createTime"));
        Y9PublishedEventSpecification spec =
            new Y9PublishedEventSpecification(eventName, eventDescription, startTime, endTime);
        return y9PublishedEventRepository.findAll(spec, pageable);
    }

    @Override
    public Page<Y9PublishedEvent> page(int page, int rows, String tenantId, String eventName, String eventDescription,
        Date startTime, Date endTime) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, Sort.by(Sort.Direction.DESC, "createTime"));
        Y9PublishedEventSpecification spec =
            new Y9PublishedEventSpecification(tenantId, eventName, eventDescription, startTime, endTime);
        return y9PublishedEventRepository.findAll(spec, pageable);
    }
}
