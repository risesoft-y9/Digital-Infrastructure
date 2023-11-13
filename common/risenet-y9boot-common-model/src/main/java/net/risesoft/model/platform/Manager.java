package net.risesoft.model.platform;

import java.io.Serializable;

import lombok.Data;

import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.enums.platform.SexEnum;
import net.risesoft.model.user.UserInfo;

/**
 * 人员
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Data
public class Manager extends OrgUnit implements Serializable {
    private static final long serialVersionUID = 1095290600488048717L;

    /**
     * 登录名称
     */
    private String loginName;

    /**
     * 密码
     */
    private String password;

    /**
     * 人员头像
     */
    private String avator;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 性别
     */
    private SexEnum sex;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 排序序列号
     */
    private String orderedPath;

    /**
     * 是否全局管理员
     */
    private boolean globalManager;

    /**
     * 三员类别
     */
    private ManagerLevelEnum managerLevel;

    public UserInfo toUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setAvator(avator);
        userInfo.setCaid(null);
        userInfo.setDn(dn);
        userInfo.setEmail(email);
        userInfo.setGuidPath(guidPath);
        userInfo.setLoginName(loginName);
        userInfo.setMobile(mobile);
        userInfo.setName(name);
        userInfo.setOriginal(true);
        userInfo.setOriginalId(null);
        userInfo.setParentId(parentId);
        userInfo.setPassword(password);
        userInfo.setPersonId(id);
        userInfo.setPositionId(null);
        userInfo.setPositions(null);
        userInfo.setY9Roles(null);
        userInfo.setSex(sex);
        userInfo.setTenantId(tenantId);
        userInfo.setGlobalManager(globalManager);
        userInfo.setManagerLevel(managerLevel);
        return userInfo;
    }

}