package net.risesoft.specification;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.relation.Y9CustomGroupMember;
import net.risesoft.query.platform.CustomGroupMemberQuery;

/**
 * 自定义用户组成员查询
 *
 * @author shidaobang
 * @date 2025/08/14
 */
@RequiredArgsConstructor
public class Y9CustomGroupMemberSpecification implements Specification<Y9CustomGroupMember> {

    private static final long serialVersionUID = 6387415638540309899L;

    private final CustomGroupMemberQuery customGroupMemberQuery;

    @Override
    public Predicate toPredicate(Root<Y9CustomGroupMember> root, CriteriaQuery<?> query,
        CriteriaBuilder criteriaBuilder) {
        List<Predicate> list = new ArrayList<>();
        if (StringUtils.hasText(customGroupMemberQuery.getGroupId())) {
            list.add(criteriaBuilder.equal(root.get("groupId"), customGroupMemberQuery.getGroupId()));
        }
        if (customGroupMemberQuery.getMemberType() != null) {
            list.add(criteriaBuilder.equal(root.get("memberType"), customGroupMemberQuery.getMemberType()));
        }
        // 如果没有条件，返回空查询
        if (list.isEmpty()) {
            return criteriaBuilder.conjunction(); // 相当于 WHERE 1=1
        }
        return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
    }
}
