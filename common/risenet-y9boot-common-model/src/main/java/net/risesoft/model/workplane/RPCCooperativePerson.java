package net.risesoft.model.workplane;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 协作平面人员表
 * 
 * @author caijingjing
 *
 */
@Data
public class RPCCooperativePerson implements Serializable {

    private static final long serialVersionUID = 7107722139307658755L;

    // 协作人员ID(主键)")
    private String personId;

    // 人员ID")
    private String userId;

    // 租户ID")
    private String tenantId;

    // 协作平面ID")
    private String planeId;

    // 级别")
    private String level;

    // 禁言")
    private String shutup;

    // 禁言结束时间")
    private Date shutupEndTime;

    // @Transient
    private String shutupEndTimeString;

    // 成员名称")
    private String userName; // 成员名称

    // 性别")
    private int sex; // 性别

    // 职务")
    private String duty; // 职务

    // @Column(name = "photo",length = 100)
    // 头像")
    private String photo;

    // @Column(name = "isDel",length = 50)
    // 删除标识")
    private boolean isDel;

    // @Column(name = "tabIndex",length = 50)
    // ")
    private int tabIndex;

    // @Column(name = "createdBy",length = 50)
    // 创建人")
    private String createdBy;

    // @Column(name = "creationDate",length = 50)
    // 创建时间")
    private Date creationDate;

    // @Column(name = "updatedBy",length = 50)
    // 修改人")
    private String updatedBy;

    // @Column(name = "updateDate",length = 50)
    // 修改时间")
    private Date updateDate;

    // @Column(name = "attribute1",length = 255)
    // 预留字段")
    private String attribute1;

    // @Column(name = "attribute2",length = 255)
    // 预留字段")
    private String attribute2;

    // @Column(name = "attribute3",length = 255)
    // 预留字段")
    private String attribute3;

    // @Column(name = "attribute4",length = 255)
    // 预留字段")
    private String attribute4;

    // @Column(name = "attribute5",length = 255)
    // 预留字段")
    private String attribute5;

    private String mobile;

    private String email;

}
