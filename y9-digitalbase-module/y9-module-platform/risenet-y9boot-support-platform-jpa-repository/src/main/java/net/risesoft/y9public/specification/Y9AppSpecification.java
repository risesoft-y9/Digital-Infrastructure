package net.risesoft.y9public.specification;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import net.risesoft.y9public.entity.resource.Y9App;

public class Y9AppSpecification implements Specification<Y9App> {

    private static final long serialVersionUID = 5719564952546180907L;

    private String ids;
    private String systemId;

    private String appName;

    public Y9AppSpecification() {
        super();
    }

    public Y9AppSpecification(String appName) {
        super();
        this.appName = appName;
    }

    public Y9AppSpecification(String systemId, String appName) {
        this.systemId = systemId;
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @Override
    public Predicate toPredicate(Root<Y9App> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate predicate = cb.conjunction();
        List<Predicate> predicateList = new ArrayList<>();
        List<Expression<Boolean>> expressions = predicate.getExpressions();
        if (StringUtils.hasText(ids)) {
            String[] sid = ids.split(",");
            // in条件拼接
            In<String> in = cb.in(root.get("id").as(String.class));
            for (String id : sid) {
                in.value(id);
            }
            predicateList.add(in);
        }
        if (StringUtils.hasText(systemId)) {
            expressions.add(cb.equal(root.get("systemId").as(String.class), systemId));
            predicateList.add(cb.equal(root.get("systemId").as(String.class), systemId));
        }
        if (StringUtils.hasText(appName)) {
            expressions.add(cb.like(root.get("name").as(String.class), "%" + appName + "%"));
            predicateList.add(cb.like(root.get("name").as(String.class), "%" + appName + "%"));
        }
        return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
    }

    public static Specification<Y9App> searchBysystemIdAndName(String systemId, String name) {
        return (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (StringUtils.hasText(systemId)) {
                predicateList.add(cb.equal(root.get("systemId").as(String.class), systemId));
            }
            if (StringUtils.hasText(name)) {
                predicateList.add(cb.like(root.get("name").as(String.class), "%" + name + "%"));
            }
            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
        };
    }

}
