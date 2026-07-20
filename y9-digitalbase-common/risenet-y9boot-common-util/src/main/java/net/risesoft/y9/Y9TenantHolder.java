package net.risesoft.y9;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.ttl.TransmittableThreadLocal;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.exception.GlobalErrorCodeEnum;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;

/**
 * 租户 holder
 *
 * @author shidaobang
 * @date 2026/03/23
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Y9TenantHolder {

    private static final TransmittableThreadLocal<String> TENANT_ID_HOLDER = new TransmittableThreadLocal<>();

    private static final Set<String> TENANT_ID_SET = ConcurrentHashMap.newKeySet();

    public static String getCurrentTenantId() {
        return TENANT_ID_HOLDER.get();
    }

    public static void setCurrentTenantId(final String tenantId) {
        if (StringUtils.isNotBlank(tenantId) && !InitDataConsts.OPERATION_TENANT_ID.equals(tenantId)
            && !Y9TenantHolder.getAllTenantIds().isEmpty() && !Y9TenantHolder.getAllTenantIds().contains(tenantId)) {
            throw Y9ExceptionUtil.businessException(GlobalErrorCodeEnum.TENANT_NOT_FOUND, tenantId);
        }
        TENANT_ID_HOLDER.set(tenantId);
    }

    public static Set<String> getAllTenantIds() {
        return TENANT_ID_SET;
    }

    public static void setAllTenantIds(Collection<String> tenantIds) {
        TENANT_ID_SET.clear();
        TENANT_ID_SET.addAll(tenantIds);
    }

    public static void clearCurrentTenantId() {
        TENANT_ID_HOLDER.remove();
    }
}
