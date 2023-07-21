package net.risesoft.y9public.specification;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class Y9AppSpecification<Y9App> implements Specification<Y9App> {

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

    public String getIds() {
        return ids;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @Override
    public Predicate toPredicate(Root<Y9App> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate predicate = cb.conjunction();
        List<Expression<Boolean>> expressions = predicate.getExpressions();
        if (StringUtils.hasText(ids)) {
            String[] sid = ids.split(",");
            // in条件拼接
            In<String> in = cb.in(root.get("id").as(String.class));
            for (String id : sid) {
                in.value(id);
            }
            expressions.add(in);
        }
        if (StringUtils.hasText(systemId)) {
            expressions.add(cb.equal(root.get("systemId").as(String.class), systemId));
        }
        if (StringUtils.hasText(appName)) {
            expressions.add(cb.like(root.get("name").as(String.class), "%" + appName + "%"));
        }
        return predicate;
    }
}
