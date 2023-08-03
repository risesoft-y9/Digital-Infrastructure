package net.risesoft.y9;

import java.util.Map;

import com.alibaba.ttl.TransmittableThreadLocal;

import net.risesoft.model.Person;
import net.risesoft.model.Position;
import net.risesoft.model.user.UserInfo;

/**
 * 当前登录用户 holder
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public abstract class Y9LoginUserHolder {
    private static final TransmittableThreadLocal<String> TENANT_ID_HOLDER = new TransmittableThreadLocal<String>();
    private static final TransmittableThreadLocal<String> TENANT_NAME_HOLDER = new TransmittableThreadLocal<String>();
    private static final TransmittableThreadLocal<String> TENANT_SHORT_NAME_HOLDER =
        new TransmittableThreadLocal<String>();

    private static final TransmittableThreadLocal<UserInfo> USER_INFO_HOLDER = new TransmittableThreadLocal<UserInfo>();
    private static final TransmittableThreadLocal<String> PERSON_ID_HOLDER = new TransmittableThreadLocal<String>();

    private static final TransmittableThreadLocal<Position> POSITION_HOLDER = new TransmittableThreadLocal<Position>();
    private static final TransmittableThreadLocal<String> POSITION_ID_HOLDER = new TransmittableThreadLocal<String>();

    private static final TransmittableThreadLocal<Map<String, Object>> MAP_HOLDER =
        new TransmittableThreadLocal<Map<String, Object>>();

    public static void clear() {
        TENANT_ID_HOLDER.remove();
        TENANT_NAME_HOLDER.remove();
        TENANT_SHORT_NAME_HOLDER.remove();

        USER_INFO_HOLDER.remove();
        PERSON_ID_HOLDER.remove();
        // personHolder.remove();

        POSITION_HOLDER.remove();
        POSITION_ID_HOLDER.remove();

        MAP_HOLDER.remove();
    }

    public static String getDeptId() {
        UserInfo userInfo = USER_INFO_HOLDER.get();
        return userInfo == null ? null : userInfo.getParentId();
    }

    public static Map<String, Object> getMap() {
        return MAP_HOLDER.get();
    }

    public static String getPersonId() {
        return PERSON_ID_HOLDER.get();
    }

    public static Position getPosition() {
        return POSITION_HOLDER.get();
    }

    public static String getPositionId() {
        return POSITION_ID_HOLDER.get();
    }

    public static String getTenantId() {
        return TENANT_ID_HOLDER.get();
    }

    public static String getTenantName() {
        return TENANT_NAME_HOLDER.get();
    }

    public static String getTenantShortName() {
        return TENANT_SHORT_NAME_HOLDER.get();
    }

    public static UserInfo getUserInfo() {
        return USER_INFO_HOLDER.get();
    }

    public static void setMap(final Map<String, Object> map) {
        MAP_HOLDER.set(map);
    }

    public static void setPerson(final Person person) {
        USER_INFO_HOLDER.set(person.toUserInfo());
        PERSON_ID_HOLDER.set(person.getId());
        TENANT_ID_HOLDER.set(person.getTenantId());
        // POSITION_ID_HOLDER.set(person.getPositionId());
    }

    public static void setPersonId(final String personId) {
        PERSON_ID_HOLDER.set(personId);
    }

    public static void setPosition(final Position position) {
        POSITION_HOLDER.set(position);
        POSITION_ID_HOLDER.set(position.getId());
    }

    public static void setPositionId(final String positionId) {
        POSITION_ID_HOLDER.set(positionId);
    }

    public static void setTenantId(final String tenantId) {
        TENANT_ID_HOLDER.set(tenantId);
    }

    public static void setTenantName(final String tenantName) {
        TENANT_NAME_HOLDER.set(tenantName);
    }

    public static void setTenantShortName(final String tenantShortName) {
        TENANT_SHORT_NAME_HOLDER.set(tenantShortName);
    }

    public static void setUserInfo(final UserInfo userInfo) {
        USER_INFO_HOLDER.set(userInfo);

        PERSON_ID_HOLDER.set(userInfo.getPersonId());
        TENANT_ID_HOLDER.set(userInfo.getTenantId());
        POSITION_ID_HOLDER.set(userInfo.getPositionId());
    }

}
