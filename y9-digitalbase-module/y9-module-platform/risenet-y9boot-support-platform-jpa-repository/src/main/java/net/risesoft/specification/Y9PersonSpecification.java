package net.risesoft.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.org.Y9Person;
import net.risesoft.query.platform.PersonQuery;

/**
 * 人员查询
 *
 * @author shidaobang
 * @date 2025/10/28
 */
@RequiredArgsConstructor
public class Y9PersonSpecification implements Specification<Y9Person> {

    private static final long serialVersionUID = -6187562834875406725L;

    private final PersonQuery personQuery;

    @Override
    public Predicate toPredicate(Root<Y9Person> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> list = new ArrayList<>();
        if (StringUtils.hasText(personQuery.getParentId())) {
            list.add(criteriaBuilder.equal(root.get("parentId"), personQuery.getParentId()));
        }
        if (StringUtils.hasText(personQuery.getName())) {
            list.add(criteriaBuilder.like(root.get("name"), "%" + personQuery.getName() + "%"));
        }
        if (personQuery.getDisabled() != null) {
            list.add(criteriaBuilder.equal(root.get("disabled"), personQuery.getDisabled()));
        }
        // 如果没有条件，返回空查询
        if (list.isEmpty()) {
            return criteriaBuilder.conjunction(); // 相当于 WHERE 1=1
        }
        return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
    }
}
