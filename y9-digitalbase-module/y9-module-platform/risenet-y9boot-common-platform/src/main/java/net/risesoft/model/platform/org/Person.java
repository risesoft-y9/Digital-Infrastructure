package net.risesoft.model.platform.org;

import java.io.Serializable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Data;

import net.risesoft.enums.platform.org.SexEnum;
import net.risesoft.y9.validation.Mobile;

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
     * 人员头像
     */
    private String avator;

    /**
     * CA认证码
     */
    private String caid;

    /**
     * 邮箱
     */
    @Email
    private String email;

    /**
     * 登录名称
     */
    @NotBlank
    private String loginName;

    /**
     * 手机号
     */
    @Mobile
    private String mobile;

    /**
     * 办公室地址
     */
    private String officeAddress;

    /**
     * 电话传真
     */
    private String officeFax;

    /**
     * 办公室电话
     */
    private String officePhone;

    /**
     * 编制名称
     */
    private Integer official;

    /**
     * 编制类型
     */
    private String officialType;

    // FIXME 密码为敏感字段 不返回？
    private String password;

    /**
     * 性别
     */
    private SexEnum sex;

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
    private boolean original = true;

    /**
     * 原始人员id
     */
    private String originalId;

}