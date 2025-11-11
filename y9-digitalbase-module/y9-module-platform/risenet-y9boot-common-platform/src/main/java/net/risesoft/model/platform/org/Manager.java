package net.risesoft.model.platform.org;

import java.io.Serializable;
import java.util.Date;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.enums.platform.org.SexEnum;
import net.risesoft.y9.validation.Mobile;

/**
 * 三员
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
     * 人员头像
     */
    private String avator;

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

    // FIXME 密码为敏感字段 不返回？
    private String password;

    /**
     * 排序序列号
     */
    private String orderedPath;

    /**
     * 性别
     */
    private SexEnum sex;

    /**
     * 三员类别
     */
    private ManagerLevelEnum managerLevel;

    /**
     * 是否全局管理员
     */
    private boolean globalManager;

    /**
     * 允许访问的客户端IP
     */
    private String userHostIp;

    /**
     * 修改密码时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastModifyPasswordTime;

    /**
     * 上一次审查时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastReviewLogTime;

}