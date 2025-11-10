package net.risesoft.model.platform.org;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * 自定义群组
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Data
public class CustomGroup implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -5239928400538145528L;

    /**
     * 主键
     */
    private String id;

    /**
     * 自定义id
     */
    private String customId;

    /**
     * 群组名称
     */
    @NotBlank
    private String groupName;

    /**
     * 用户id
     */
    private String personId;

    /**
     * 用户名称
     */
    private String personName;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 分享人Id
     */
    private String shareId;

    /**
     * 分享人
     */
    private String shareName;

    /**
     * 排序字段
     */
    private Integer tabIndex;

}
