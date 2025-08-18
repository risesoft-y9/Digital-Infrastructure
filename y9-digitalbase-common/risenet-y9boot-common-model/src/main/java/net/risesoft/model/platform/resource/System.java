package net.risesoft.model.platform.resource;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 系统
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Data
public class System implements Serializable {
    private static final long serialVersionUID = 8905896381019503361L;

    /**
     * 唯一主键
     */
    private String id;

    /**
     * 开发商租户标识
     */
    private String isvGuid;

    /**
     * 系统名称
     */
    private String name;

    /** 系统中文名称 */
    private String cnName;

    /**
     * 描述
     */
    private String description;

    /**
     * 系统程序上下文
     */
    private String contextPath;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 是否启用独立数据源
     */
    private Boolean singleDatasource;

    /**
     * 是否自动租用系统
     */
    private Boolean autoInit;

    /** 排序 */
    private Integer tabIndex;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
