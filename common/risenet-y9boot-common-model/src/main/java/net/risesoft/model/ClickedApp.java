package net.risesoft.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * 应用点击详情
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Data
@Builder
public class ClickedApp implements Serializable {

    private static final long serialVersionUID = 7190005083758488330L;

    /**
     * id
     */
    private String id;

    /** 人员id */
    private String personId;

    /** 租户id */
    private String tenantId;

    /** 应用id */
    private String appId;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 保存时间
     */
    private Date saveDate;

}
