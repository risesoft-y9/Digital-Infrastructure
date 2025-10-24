package net.risesoft.y9public.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.y9public.entity.resource.Y9App;

@Setter
@Getter
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

    @Override
    public Predicate toPredicate(Root<Y9App> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> list = new ArrayList<>();
        if (StringUtils.hasText(ids)) {
            String[] sid = ids.split(",");
            // in条件拼接
            In<String> in = cb.in(root.get("id").as(String.class));
            for (String id : sid) {
                in.value(id);
            }
            list.add(in);
        }
        if (StringUtils.hasText(systemId)) {
            list.add(cb.equal(root.get("systemId").as(String.class), systemId));
        }
        if (StringUtils.hasText(appName)) {
            list.add(cb.like(root.get("name").as(String.class), "%" + appName + "%"));
        }
        // 如果没有条件，返回空查询
        if (list.isEmpty()) {
            return cb.conjunction(); // 相当于 WHERE 1=1
        }
        return cb.and(list.toArray(new Predicate[list.size()]));
    }
}
