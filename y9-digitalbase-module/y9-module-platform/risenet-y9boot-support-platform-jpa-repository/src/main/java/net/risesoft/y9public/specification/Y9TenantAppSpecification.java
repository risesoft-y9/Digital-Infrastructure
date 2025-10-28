package net.risesoft.y9public.specification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.query.platform.TenantAppQuery;
import net.risesoft.y9public.entity.tenant.Y9TenantApp;

@Slf4j
public class Y9TenantAppSpecification implements Specification<Y9TenantApp> {

    private static final long serialVersionUID = -4477532138695659736L;

    private TenantAppQuery tenantAppQuery;

    public Y9TenantAppSpecification() {
        super();
    }

    public Y9TenantAppSpecification(TenantAppQuery tenantAppQuery) {
        super();
        this.tenantAppQuery = tenantAppQuery;
    }

    private boolean isNullOrEmpty(Object value) {
        if (value instanceof String) {
            return value == null || "".equals(value);
        }
        return value == null;
    }

    @Override
    public Predicate toPredicate(Root<Y9TenantApp> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Predicate> list = new ArrayList<>();
        if (tenantAppQuery.getVerify() != null) {
            list.add(cb.equal(root.get("verify").as(Boolean.class), tenantAppQuery.getVerify()));
        }
        if (StringUtils.isNotBlank(tenantAppQuery.getTenantName())) {
            list.add(cb.like(root.get("tenantName").as(String.class), "%" + tenantAppQuery.getTenantName() + "%"));
        }

        if (StringUtils.isNotBlank(tenantAppQuery.getAppName())) {
            list.add(cb.like(root.get("appName").as(String.class), "%" + tenantAppQuery.getAppName() + "%"));
        }

        if (tenantAppQuery.getTenancy() != null) {
            list.add(cb.equal(root.get("tenancy"), tenantAppQuery.getTenancy()));
        }
        if (tenantAppQuery.getSystemIds() != null && !"3".equals(tenantAppQuery.getSystemIds())
            && !"".equals(tenantAppQuery.getSystemIds())) {
            String[] sid = tenantAppQuery.getSystemIds().split(",");
            // in条件拼接
            In<String> in = cb.in(root.get("systemId").as(String.class));
            for (String id : sid) {
                in.value(id);
            }
            list.add(in);
        }

        if (tenantAppQuery.getCreateTime() != null || tenantAppQuery.getVerifyTime() != null) {
            if (!isNullOrEmpty(tenantAppQuery.getCreateTime())) {
                try {
                    list.add(cb.greaterThanOrEqualTo(root.get("verifyTime").as(Date.class),
                        sdf.parse(tenantAppQuery.getCreateTime())));
                } catch (ParseException e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }
            if (!isNullOrEmpty(tenantAppQuery.getVerifyTime())) {
                try {
                    list.add(cb.lessThanOrEqualTo(root.get("verifyTime").as(Date.class),
                        sdf.parse(tenantAppQuery.getVerifyTime())));
                } catch (ParseException e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }
        }
        // 如果没有条件，返回空查询
        if (list.isEmpty()) {
            return cb.conjunction(); // 相当于 WHERE 1=1
        }
        return cb.and(list.toArray(new Predicate[list.size()]));
    }

}
