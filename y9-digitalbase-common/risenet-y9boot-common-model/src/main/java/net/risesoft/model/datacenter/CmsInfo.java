package net.risesoft.model.datacenter;

import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * cms信息
 */
@Data
@Builder
public class CmsInfo implements Serializable {
    private static final long serialVersionUID = -1900080157365837431L;

    /**
     * 主键
     */
    private String id;

    /**
     * 系统英文名称
     */
    private String systemName;

    /**
     * 系统中文名称
     */
    private String systemCnName;

    /**
     * 应用中文名称
     */
    private String appCnName;

    /**
     * 标题
     */
    private String title;

    /**
     * 正文内容
     */
    private String text;

    /**
     * 来源
     */
    private String from;

    /**
     * 创建日期
     */
    private Date createDate;

    /**
     * 路径
     */
    private String textUrl;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 摘要
     */
    private String zy;

    /**
     * 信息公开 1-公开 0-不公开
     */
    private String disabled;

}
