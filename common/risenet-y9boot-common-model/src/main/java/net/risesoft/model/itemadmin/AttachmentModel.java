package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

@Data
public class AttachmentModel implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 3241197746615642199L;

    /**
     * 主键
     */
    private String id;

    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 流程实例编号，草稿编号
     */
    private String processSerialNumber;

    /**
     * 文件名称
     */
    private String name;

    /**
     * 附件来源
     */
    private String fileSource;

    /**
     * 文件字节数
     */
    private String fileSize;

    /**
     *
     * 文件字节数
     */
    private String fileType;

    /**
     * 文件仓库Id
     */
    private String fileStoreId;

    /**
     * 上传时间
     */
    private String uploadTime;

    /**
     * 上传人
     */
    private String personName;

    /**
     * 上传人员Id
     */
    private String personId;

    /**
     * 上传岗位Id
     */
    private String positionId;

    /**
     * 上传人部门id
     */
    private String deptId;

    /**
     * 上传人部门名称
     */
    private String deptName;

    /**
     * 文件描述
     */
    private String describes;

    private Integer serialNumber;
}
