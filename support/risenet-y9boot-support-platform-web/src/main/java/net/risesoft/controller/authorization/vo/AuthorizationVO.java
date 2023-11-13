package net.risesoft.controller.authorization.vo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

import net.risesoft.enums.platform.AuthorityEnum;

/**
 * 权限
 *
 * @author shidaobang
 * @date 2022/4/20
 */
@Data
public class AuthorizationVO implements Serializable {

    private static final long serialVersionUID = 2949054509784778130L;

    /** 唯一标识 */
    private String id;

    /** 角色id */
    private String roleId;

    /** 角色名称 */
    private String roleName;

    /** 组织id */
    private String orgId;

    /** 组织名称 */
    private String orgName;

    /** 组织类型 */
    private String orgType;

    /** 资源id */
    private String resourceId;

    /** 资源名称 */
    private String resourceName;

    /** 资源url */
    private String url;

    /** 授权人 */
    private String authorizer;

    /** 授权时间 */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date authorizeTime;

    /** 操作 */
    private AuthorityEnum authority;

    /** 操作（中文） */
    private String authorityStr;

    /** 是否继承 */
    private String inherit;

}
