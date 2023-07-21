package net.risesoft.y9public.specification;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import net.risesoft.y9public.entity.event.Y9PublishedEvent;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public class Y9PublishedEventSpecification implements Specification<Y9PublishedEvent> {
    private static final long serialVersionUID = -242256666295779942L;

    private String eventName;
    private String eventDescription;
    private Date startTime;
    private Date endTime;
    private String tenantId;

    public Y9PublishedEventSpecification(String tenantId, Date startTime) {
        this.tenantId = tenantId;
        this.startTime = startTime;
    }

    public Y9PublishedEventSpecification(String eventName, String eventDescription, Date startTime, Date endTime) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Y9PublishedEventSpecification(String tenantId, String eventName, String eventDescription, Date startTime, Date endTime) {
        this.tenantId = tenantId;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public Predicate toPredicate(Root<Y9PublishedEvent> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        Predicate predicate = criteriaBuilder.conjunction();
        List<Expression<Boolean>> expressions = predicate.getExpressions();
        if (StringUtils.hasText(tenantId)) {
            expressions.add(criteriaBuilder.equal(root.<String>get("tenantId"), tenantId));
        }
        if (StringUtils.hasText(eventName)) {
            expressions.add(criteriaBuilder.like(root.<String>get("eventName"), "%" + eventName + "%"));
        }
        if (StringUtils.hasText(eventDescription)) {
            expressions.add(criteriaBuilder.like(root.<String>get("eventDescription"), "%" + eventDescription + "%"));
        }
        if (startTime != null) {
            expressions.add(criteriaBuilder.greaterThanOrEqualTo(root.<Date>get("createTime"), startTime));
        }
        if (endTime != null) {
            expressions.add(criteriaBuilder.lessThanOrEqualTo(root.<Date>get("createTime"), endTime));
        }
        return predicate;
    }
}
