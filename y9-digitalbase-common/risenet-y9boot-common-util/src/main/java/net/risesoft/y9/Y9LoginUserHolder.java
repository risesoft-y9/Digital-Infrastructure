package net.risesoft.y9;

import java.util.Map;

import com.alibaba.ttl.TransmittableThreadLocal;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.risesoft.model.user.UserInfo;

/**
 * 当前登录用户 holder
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Y9LoginUserHolder {
    private static final TransmittableThreadLocal<String> TENANT_NAME_HOLDER = new TransmittableThreadLocal<>();
    private static final TransmittableThreadLocal<String> TENANT_SHORT_NAME_HOLDER = new TransmittableThreadLocal<>();

    private static final TransmittableThreadLocal<UserInfo> USER_INFO_HOLDER = new TransmittableThreadLocal<>();

    private static final TransmittableThreadLocal<String> PERSON_ID_HOLDER = new TransmittableThreadLocal<>();
    private static final TransmittableThreadLocal<String> POSITION_ID_HOLDER = new TransmittableThreadLocal<>();

    private static final TransmittableThreadLocal<Map<String, Object>> MAP_HOLDER = new TransmittableThreadLocal<>();

    public static void clear() {
        TENANT_NAME_HOLDER.remove();
        TENANT_SHORT_NAME_HOLDER.remove();

        USER_INFO_HOLDER.remove();

        PERSON_ID_HOLDER.remove();
        POSITION_ID_HOLDER.remove();

        MAP_HOLDER.remove();
    }

    /**
     * 获取当前用户所属部门 ID
     *
     * @return 部门 ID
     */
    public static String getDeptId() {
        UserInfo userInfo = USER_INFO_HOLDER.get();
        return userInfo == null ? null : userInfo.getParentId();
    }

    /**
     * 获取自定义信息 Map
     *
     * @return 自定义信息 Map
     */
    public static Map<String, Object> getMap() {
        return MAP_HOLDER.get();
    }

    /**
     * 设置自定义信息 Map
     *
     * @param map 自定义信息 Map
     */
    public static void setMap(final Map<String, Object> map) {
        MAP_HOLDER.set(map);
    }

    /**
     * 获取人员 ID
     *
     * @return 人员 ID
     */
    public static String getPersonId() {
        return PERSON_ID_HOLDER.get();
    }

    /**
     * 设置人员 ID
     *
     * @param personId 人员 ID
     */
    public static void setPersonId(final String personId) {
        PERSON_ID_HOLDER.set(personId);
    }

    /**
     * 获取岗位 ID
     *
     * @return 岗位 ID
     */
    public static String getPositionId() {
        return POSITION_ID_HOLDER.get();
    }

    /**
     * 设置岗位 ID
     *
     * @param positionId 岗位 ID
     */
    public static void setPositionId(final String positionId) {
        POSITION_ID_HOLDER.set(positionId);
    }

    /**
     * 获取租户 ID
     *
     * @return 租户 ID
     */
    public static String getTenantId() {
        return Y9TenantHolder.getCurrentTenantId();
    }

    /**
     * 设置租户 ID
     *
     * @param tenantId 租户 ID
     * @deprecated 使用 {@link Y9TenantHolder#setCurrentTenantId } 替代
     */
    @Deprecated
    public static void setTenantId(final String tenantId) {
        Y9TenantHolder.setCurrentTenantId(tenantId);
    }

    /**
     * 获取租户名称
     *
     * @return 租户名称
     */
    public static String getTenantName() {
        return TENANT_NAME_HOLDER.get();
    }

    /**
     * 设置租户名称
     *
     * @param tenantName 租户名称
     */
    public static void setTenantName(final String tenantName) {
        TENANT_NAME_HOLDER.set(tenantName);
    }

    /**
     * 获取租户简称
     *
     * @return 租户简称
     */
    public static String getTenantShortName() {
        return TENANT_SHORT_NAME_HOLDER.get();
    }

    /**
     * 设置租户简称
     *
     * @param tenantShortName 租户简称
     */
    public static void setTenantShortName(final String tenantShortName) {
        TENANT_SHORT_NAME_HOLDER.set(tenantShortName);
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    public static UserInfo getUserInfo() {
        return USER_INFO_HOLDER.get();
    }

    /**
     * 设置当前登录用户信息，并同步租户、人员、岗位等相关信息
     *
     * @param userInfo 用户信息
     */
    public static void setUserInfo(final UserInfo userInfo) {
        USER_INFO_HOLDER.set(userInfo);

        PERSON_ID_HOLDER.set(userInfo.getPersonId());
        POSITION_ID_HOLDER.set(userInfo.getPositionId());

        Y9TenantHolder.setCurrentTenantId(userInfo.getTenantId());
        TENANT_NAME_HOLDER.set(userInfo.getTenantName());
        TENANT_SHORT_NAME_HOLDER.set(userInfo.getTenantShortName());
    }

}
