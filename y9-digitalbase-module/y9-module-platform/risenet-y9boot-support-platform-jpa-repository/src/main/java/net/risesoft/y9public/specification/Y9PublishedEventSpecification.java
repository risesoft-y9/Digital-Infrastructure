package net.risesoft.y9public.specification;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import net.risesoft.y9public.entity.event.Y9PublishedEvent;
import net.risesoft.y9public.specification.query.PublishedEventQuery;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public class Y9PublishedEventSpecification implements Specification<Y9PublishedEvent> {
    private static final long serialVersionUID = -242256666295779942L;

    private final PublishedEventQuery eventQuery;

    public Y9PublishedEventSpecification(PublishedEventQuery eventQuery) {
        this.eventQuery = eventQuery;
    }

    @Override
    public Predicate toPredicate(Root<Y9PublishedEvent> root, CriteriaQuery<?> criteriaQuery,
        CriteriaBuilder criteriaBuilder) {
        List<Predicate> list = new ArrayList<>();
        if (StringUtils.hasText(eventQuery.getTenantId())) {
            list.add(criteriaBuilder.equal(root.<String>get("tenantId"), eventQuery.getTenantId()));
        }
        if (StringUtils.hasText(eventQuery.getEventName())) {
            list.add(criteriaBuilder.like(root.get("eventName"), "%" + eventQuery.getEventName() + "%"));
        }
        if (StringUtils.hasText(eventQuery.getEventDescription())) {
            list.add(criteriaBuilder.like(root.get("eventDescription"), "%" + eventQuery.getEventDescription() + "%"));
        }
        if (eventQuery.getStartTime() != null) {
            list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), eventQuery.getStartTime()));
        }
        if (eventQuery.getEndTime() != null) {
            list.add(criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), eventQuery.getEndTime()));
        }
        // 如果没有条件，返回空查询
        if (list.isEmpty()) {
            return criteriaBuilder.conjunction(); // 相当于 WHERE 1=1
        }
        return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
    }
}
