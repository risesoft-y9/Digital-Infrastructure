package net.risesoft.y9public.specification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Y9TenantAppSpecification<TenantApp> implements Specification<TenantApp> {

    private static final long serialVersionUID = -4477532138695659736L;

    private String tenantName;
    private Boolean verify;
    private String createTime;
    private String verifyTime;
    private Boolean tenancy;
    private String systemId;

    private String appName;

    public Y9TenantAppSpecification() {
        super();
    }

    public Y9TenantAppSpecification(Boolean verify, String tenantName, String createTime, String verifyTime,
        Boolean tenancy, String systemId) {
        super();
        this.verify = verify;
        this.tenantName = tenantName;
        this.createTime = createTime;
        this.verifyTime = verifyTime;
        this.tenancy = tenancy;
        this.systemId = systemId;
    }

    public Y9TenantAppSpecification(Boolean verify, String tenantName, String createTime, String verifyTime,
        Boolean tenancy, String systemId, String appName) {
        super();
        this.verify = verify;
        this.tenantName = tenantName;
        this.createTime = createTime;
        this.verifyTime = verifyTime;
        this.tenancy = tenancy;
        this.systemId = systemId;
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }

    public String getcreateTime() {
        return createTime;
    }

    public String getSystemId() {
        return systemId;
    }

    public Boolean getTenancy() {
        return tenancy;
    }

    public String getTenantName() {
        return tenantName;
    }

    public Boolean getVerify() {
        return verify;
    }

    public String getverifyTime() {
        return verifyTime;
    }

    private boolean isNullOrEmpty(Object value) {
        if (value instanceof String) {
            return value == null || "".equals(value);
        }
        return value == null;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setcreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public void setTenancy(Boolean tenancy) {
        this.tenancy = tenancy;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public void setVerify(Boolean verify) {
        this.verify = verify;
    }

    public void setVerifyTime(String verifyTime) {
        this.verifyTime = verifyTime;
    }

    @Override
    public Predicate toPredicate(Root<TenantApp> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Predicate predicate = cb.conjunction();
        List<Expression<Boolean>> expressions = predicate.getExpressions();
        if (verify != null) {
            expressions.add(cb.equal(root.get("verify").as(Boolean.class), verify));
        }
        if (StringUtils.isNotBlank(tenantName)) {
            expressions.add(cb.like(root.get("tenantName").as(String.class), "%" + tenantName + "%"));
        }

        if (StringUtils.isNotBlank(appName)) {
            expressions.add(cb.like(root.get("appName").as(String.class), "%" + appName + "%"));
        }
        if (createTime != null || verifyTime != null) {
            if (!isNullOrEmpty(createTime)) {
                try {
                    expressions
                        .add(cb.greaterThanOrEqualTo(root.get("verifyTime").as(Date.class), sdf.parse(createTime)));
                } catch (ParseException e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }
            if (!isNullOrEmpty(verifyTime)) {
                try {
                    expressions.add(cb.lessThanOrEqualTo(root.get("verifyTime").as(Date.class), sdf.parse(verifyTime)));
                } catch (ParseException e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }
        }
        if (tenancy != null) {
            expressions.add(cb.equal(root.get("tenancy"), tenancy));
        }
        if (systemId != null && !"3".equals(systemId) && !"".equals(systemId)) {
            String[] sid = systemId.split(",");
            // in条件拼接
            In<String> in = cb.in(root.get("systemId").as(String.class));
            for (String id : sid) {
                in.value(id);
            }
            expressions.add(in);
        }
        return predicate;
    }

}
