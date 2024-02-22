package net.risesoft.model.itemadmin;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 *
 * @author zhangchongjie
 * @date 2023/09/21
 */
@Data
public class ActDeModel implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1019751762452808352L;

    private String id;

    // 模型名称
    private String name;

    // 模型key
    private String modelKey;

    // 描述
    private String description;

    // 创建时间
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date created;

    // 创建人
    private String createdBy;

    // 最后修改时间
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdated;

    // 最后修改人
    private String lastUpdatedBy;

    // 版本
    private Integer version;

    // 流程图数据
    private byte[] modelByte;

    // 租户id
    private String tenantId;

}