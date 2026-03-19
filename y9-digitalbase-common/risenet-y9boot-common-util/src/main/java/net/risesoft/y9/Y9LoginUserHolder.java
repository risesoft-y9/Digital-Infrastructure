package net.risesoft.y9;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.ttl.TransmittableThreadLocal;

import net.risesoft.exception.GlobalErrorCodeEnum;
import net.risesoft.model.user.UserInfo;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;

/**
 * 当前登录用户 holder
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public class Y9LoginUserHolder {
    // 缓存所有租户ID
    private static final Set<String> TENANT_ID_SET = ConcurrentHashMap.newKeySet();

    // 当前租户 id，可用于多数据源的切换
    private static final TransmittableThreadLocal<String> TENANT_ID_HOLDER = new TransmittableThreadLocal<>();
    private static final TransmittableThreadLocal<String> TENANT_NAME_HOLDER = new TransmittableThreadLocal<>();
    private static final TransmittableThreadLocal<String> TENANT_SHORT_NAME_HOLDER = new TransmittableThreadLocal<>();

    private static final TransmittableThreadLocal<UserInfo> USER_INFO_HOLDER = new TransmittableThreadLocal<>();

    private static final TransmittableThreadLocal<String> PERSON_ID_HOLDER = new TransmittableThreadLocal<>();
    private static final TransmittableThreadLocal<String> POSITION_ID_HOLDER = new TransmittableThreadLocal<>();

    private static final TransmittableThreadLocal<Map<String, Object>> MAP_HOLDER = new TransmittableThreadLocal<>();

    public static void clear() {
        TENANT_ID_HOLDER.remove();
        TENANT_NAME_HOLDER.remove();
        TENANT_SHORT_NAME_HOLDER.remove();

        USER_INFO_HOLDER.remove();

        PERSON_ID_HOLDER.remove();
        POSITION_ID_HOLDER.remove();

        MAP_HOLDER.remove();
    }

    public static void updateTenantIdSet(List<String> tenantIdList) {
        TENANT_ID_SET.clear();
        TENANT_ID_SET.addAll(tenantIdList);
    }

    public static String getDeptId() {
        UserInfo userInfo = USER_INFO_HOLDER.get();
        return userInfo == null ? null : userInfo.getParentId();
    }

    public static Map<String, Object> getMap() {
        return MAP_HOLDER.get();
    }

    public static void setMap(final Map<String, Object> map) {
        MAP_HOLDER.set(map);
    }

    public static String getPersonId() {
        return PERSON_ID_HOLDER.get();
    }

    public static void setPersonId(final String personId) {
        PERSON_ID_HOLDER.set(personId);
    }

    public static String getPositionId() {
        return POSITION_ID_HOLDER.get();
    }

    public static void setPositionId(final String positionId) {
        POSITION_ID_HOLDER.set(positionId);
    }

    public static String getTenantId() {
        return TENANT_ID_HOLDER.get();
    }

    public static void setTenantId(final String tenantId) {
        if (StringUtils.isNotBlank(tenantId) && !TENANT_ID_SET.isEmpty() && !TENANT_ID_SET.contains(tenantId)) {
            throw Y9ExceptionUtil.businessException(GlobalErrorCodeEnum.TENANT_NOT_FOUND, tenantId);
        }
        TENANT_ID_HOLDER.set(tenantId);
    }

    public static String getTenantName() {
        return TENANT_NAME_HOLDER.get();
    }

    public static void setTenantName(final String tenantName) {
        TENANT_NAME_HOLDER.set(tenantName);
    }

    public static String getTenantShortName() {
        return TENANT_SHORT_NAME_HOLDER.get();
    }

    public static void setTenantShortName(final String tenantShortName) {
        TENANT_SHORT_NAME_HOLDER.set(tenantShortName);
    }

    public static UserInfo getUserInfo() {
        return USER_INFO_HOLDER.get();
    }

    public static void setUserInfo(final UserInfo userInfo) {
        USER_INFO_HOLDER.set(userInfo);

        PERSON_ID_HOLDER.set(userInfo.getPersonId());
        POSITION_ID_HOLDER.set(userInfo.getPositionId());

        TENANT_ID_HOLDER.set(userInfo.getTenantId());
        TENANT_NAME_HOLDER.set(userInfo.getTenantName());
        TENANT_SHORT_NAME_HOLDER.set(userInfo.getTenantShortName());
    }

}
