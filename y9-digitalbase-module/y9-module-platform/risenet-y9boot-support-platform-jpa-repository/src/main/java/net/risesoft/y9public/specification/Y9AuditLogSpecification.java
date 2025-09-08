package net.risesoft.y9public.specification;

import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

import net.risesoft.y9public.entity.auditlog.Y9AuditLog;
import net.risesoft.y9public.specification.query.AuditLogQuery;

/**
 * 审计日志查询条件
 *
 * @author shidaobang
 * @date 2025/08/14
 */
@RequiredArgsConstructor
public class Y9AuditLogSpecification implements Specification<Y9AuditLog> {

    private static final long serialVersionUID = 6387415638540309899L;

    private final AuditLogQuery auditLogQuery;

    @Override
    public Predicate toPredicate(Root<Y9AuditLog> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Predicate predicate = criteriaBuilder.conjunction();
        List<Expression<Boolean>> expressions = predicate.getExpressions();
        if (StringUtils.hasText(auditLogQuery.getTenantId())) {
            expressions.add(criteriaBuilder.equal(root.get("tenantId"), auditLogQuery.getTenantId()));
        }
        if (StringUtils.hasText(auditLogQuery.getAction())) {
            expressions.add(criteriaBuilder.equal(root.get("action"), auditLogQuery.getAction()));
        }
        if (StringUtils.hasText(auditLogQuery.getDescription())) {
            expressions.add(criteriaBuilder.like(root.get("description"), "%" + auditLogQuery.getDescription() + "%"));
        }
        if (auditLogQuery.getStartTime() != null) {
            expressions.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), auditLogQuery.getStartTime()));
        }
        if (auditLogQuery.getEndTime() != null) {
            expressions.add(criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), auditLogQuery.getEndTime()));
        }
        return predicate;
    }
}
