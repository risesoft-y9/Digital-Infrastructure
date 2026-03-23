package net.risesoft.y9;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 租户缓存
 *
 * @author shidaobang
 * @date 2026/03/23
 */
public final class TenantCache {

    private static final Set<String> TENANT_ID_SET = ConcurrentHashMap.newKeySet();

    private TenantCache() {}

    public static boolean containsTenantId(String tenantId) {
        return TENANT_ID_SET.contains(tenantId);
    }

    public static Set<String> getTenantIdSet() {
        return TENANT_ID_SET;
    }

    public static boolean isEmpty() {
        return TENANT_ID_SET.isEmpty();
    }

    public static void updateTenantIdSet(List<String> tenantIdList) {
        TENANT_ID_SET.clear();
        TENANT_ID_SET.addAll(tenantIdList);
    }
}
