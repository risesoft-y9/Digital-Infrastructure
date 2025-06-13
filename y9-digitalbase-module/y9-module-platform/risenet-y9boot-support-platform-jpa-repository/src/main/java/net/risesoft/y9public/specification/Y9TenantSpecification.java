package net.risesoft.y9public.specification;

import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

public class Y9TenantSpecification<Y9Tenant> implements Specification<Y9Tenant> {

    private static final long serialVersionUID = -1085154903492093886L;

    private String name;
    private Integer enabled;

    public Y9TenantSpecification() {
        super();
    }

    public Y9TenantSpecification(String name, Integer enabled) {
        super();
        this.name = name;
        this.enabled = enabled;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return predicate;
    }

}
