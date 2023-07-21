package net.risesoft.model.cms;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 模板信息
 *
 * @author mengjuhua
 *
 */
@Data
public class CmsTpl implements Serializable {

    private static final long serialVersionUID = -4775648699676406834L;

    /**
     * 获得模板完整名称，是文件的唯一标识。
     */
    private String name;

    /**
     * 获得路径，不包含文件名的路径。
     */
    private String path;

    /**
     * 获得模板名称，不包含路径的文件名。
     */
    private String filename;

    /**
     * 获得模板内容
     */
    private String source;

    /**
     * 获得最后修改时间（毫秒）
     */
    private long lastModified;

    /**
     * 获得最后修改时间（日期）
     */
    private Date lastModifiedDate;

    /**
     * 获得文件大小，单位bytes
     */
    private long length;

    /**
     * 获得文件大小，单位K bytes
     */
    private int size;

    /**
     * 是否目录
     */
    private boolean isDirectory;
}
