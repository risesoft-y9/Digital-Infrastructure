package net.risesoft.y9public.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

public class Y9TenantSpecification<Y9Tenant> implements Specification<Y9Tenant> {

    private static final long serialVersionUID = -1085154903492093886L;

    private String name;
    private Integer enabled;

    private Integer tenantType;

    public Y9TenantSpecification() {
        super();
    }

    public Y9TenantSpecification(String name, Integer enabled, Integer tenantType) {
        super();
        this.name = name;
        this.enabled = enabled;
        this.tenantType = tenantType;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }

    public Integer getTenantType() {
        return tenantType;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTenantType(Integer tenantType) {
        this.tenantType = tenantType;
    }

    @Override
    public Predicate toPredicate(Root<Y9Tenant> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate predicate = cb.conjunction();
        List<Expression<Boolean>> expressions = predicate.getExpressions();
        if (name != null && !"".equals(name)) {
            expressions.add(cb.like(root.get("name").as(String.class), "%" + name + "%"));
        }
        if (null != enabled) {
            expressions.add(cb.equal(root.get("enabled").as(Integer.class), enabled));
        }
        if (null != tenantType) {
            expressions.add(cb.equal(root.get("tenantType").as(Integer.class), tenantType));
        }
        return predicate;
    }

}
