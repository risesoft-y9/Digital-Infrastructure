package net.risesoft.model.user;

import java.io.Serializable;

import lombok.Data;

import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.enums.platform.org.SexEnum;

/***
 * 登录人员信息
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 *
 */
@Data
public class UserInfo implements Serializable {

    private static final long serialVersionUID = -867112844100528415L;

    /** 租户Id */
    private String tenantId;

    /** 租户英文名称 */
    private String tenantShortName;

    /** 租户名称 */
    private String tenantName;

    /** 人员id */
    private String personId;

    /** 登录名称 */
    private String loginName;

    /** 密码 */
    private String password;

    /** 性别 */
    private SexEnum sex;

    /** CA认证码 */
    private String caid;

    /** 邮箱 */
    private String email;

    /** 手机号 */
    private String mobile;

    /** 由ID组成的父子关系列表，之间用逗号分隔 */
    private String guidPath;

    /** 登录方式 */
    private String loginType;

    /** 父节点id */
    private String parentId;

    /** 是否原始人员，0:添加的人员，1：新增的人员 */
    private boolean original;
    /**
     * 原始人员id
     */
    private String originalId;

    /** 是否全局管理员 */
    private boolean globalManager;

    /** 三员类别 */
    private ManagerLevelEnum managerLevel;

    /** 人员头像 */
    private String avator;

    /** 拥有的岗位列表 */
    private String positions;

    /** 当前的岗位Id */
    private String positionId;

    /** 域名称 */
    private String dn;

    /** 人员名称 */
    private String name;

    /** 人员类型 */
    private String personType;

    /** 身份信息 */
    private String idNum;

    /** json字符串 */
    private String jsonStr;

    /** 排序序列号 */
    private String orderedPath;

    /** 微信id */
    private String weixinId;
    // 以上是sso server返回的用户属性
    // 以下字段是Y9Person中的字段

    /*
    private String jsonStr;
    private String orderedPath;
    private String weixinId;
    
    private Integer official;
    private String officialType;
    private String duty;
    private Integer dutyLevel;
    private String dutyLevelName;
    private String province;
    private String officeAddress;
    private String officePhone;
    private String officeFax;
    
    private String id;
    private Date createTime;
    private Date updateTime;
    private Boolean deleted;
    private Boolean disabled;
    private String description;
    private String customId;
    private String orgType;
    private String properties;
    private Integer tabIndex;
    */

}