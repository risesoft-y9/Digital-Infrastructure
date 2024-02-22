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
public class Person extends OrgUnit implements Serializable {
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
     * 编制名称
     */
    private Integer official;

    /**
     * 编制类型
     */
    private String officialType;

    /**
     * 职务
     */
    private String duty;

    /**
     * 职级
     */
    private Integer dutyLevel;

    /**
     * 职级名称
     */
    private String dutyLevelName;

    /**
     * CA认证码
     */
    private String caid;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 性别
     */
    private SexEnum sex;

    /**
     * 人员籍贯
     */
    private String province;

    /**
     * 办公室地址
     */
    private String officeAddress;

    /**
     * 办公室电话
     */
    private String officePhone;

    /**
     * 电话传真
     */
    private String officeFax;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 拥有的角色列表
     */
    private String roles;

    /**
     * 拥有的岗位列表
     */
    private String positions;

    /**
     * 当前的岗位Id
     */
    private String positionId;

    /**
     * 人员类型,管理员用户：adminPerson,单位用户：deptPerson,个人用户:userPerson,专家用户:expertPerson
     */
    private String personType;

    /**
     * 微信id
     */
    private String weixinId;

    /**
     * 排序序列号
     */
    private String orderedPath;

    /**
     * 是否原始人员，0:添加的人员，1：新增的人员
     */
    private boolean original;

    /**
     * 原始人员id
     */
    private String originalId;

    public UserInfo toUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setAvator(avator);
        userInfo.setCaid(caid);
        userInfo.setDn(dn);
        userInfo.setEmail(email);
        userInfo.setGuidPath(guidPath);
        userInfo.setLoginName(loginName);
        userInfo.setMobile(mobile);
        userInfo.setName(name);
        userInfo.setOriginal(original);
        userInfo.setOriginalId(originalId);
        userInfo.setParentId(parentId);
        userInfo.setPassword(password);
        userInfo.setPersonId(id);
        userInfo.setPersonType(personType);
        userInfo.setPositionId(positionId);
        userInfo.setPositions(positions);
        userInfo.setY9Roles(roles);
        userInfo.setSex(sex);
        userInfo.setTenantId(tenantId);
        userInfo.setGlobalManager(false);
        userInfo.setManagerLevel(ManagerLevelEnum.GENERAL_USER);
        return userInfo;
    }

}