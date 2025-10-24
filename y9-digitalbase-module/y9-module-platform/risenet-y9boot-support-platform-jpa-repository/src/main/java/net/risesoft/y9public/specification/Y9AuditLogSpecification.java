package net.risesoft.y9public.specification;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
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
        List<Predicate> list = new ArrayList<>();
        if (StringUtils.hasText(auditLogQuery.getTenantId())) {
            list.add(criteriaBuilder.equal(root.get("tenantId"), auditLogQuery.getTenantId()));
        }
        if (StringUtils.hasText(auditLogQuery.getAction())) {
            list.add(criteriaBuilder.equal(root.get("action"), auditLogQuery.getAction()));
        }
        if (StringUtils.hasText(auditLogQuery.getDescription())) {
            list.add(criteriaBuilder.like(root.get("description"), "%" + auditLogQuery.getDescription() + "%"));
        }
        if (auditLogQuery.getStartTime() != null) {
            list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), auditLogQuery.getStartTime()));
        }
        if (auditLogQuery.getEndTime() != null) {
            list.add(criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), auditLogQuery.getEndTime()));
        }
        // 如果没有条件，返回空查询
        if (list.isEmpty()) {
            return criteriaBuilder.conjunction(); // 相当于 WHERE 1=1
        }
        return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
    }
}
