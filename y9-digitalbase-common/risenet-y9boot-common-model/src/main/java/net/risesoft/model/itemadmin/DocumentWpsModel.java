package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

@Data
public class DocumentWpsModel implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 5923508219519638522L;

    /**
     * 主键
     */
    private String id;

    /**
     * @FieldCommit(value = "文件Id")
     */
    private String fileId;

    /**
     * @FieldCommit(value = "卷Id")
     */
    private String volumeId;

    /**
     * 租户Id
     */
    private String tenantId;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 包括文件名+后缀
     */
    private String fileName;

    /**
     * 上传人员id
     */
    private String userId;

    /**
     * 是否套红、1为套红word，0为word
     */
    private String istaohong;

    /**
     * 保存时间
     */
    private String saveDate;

    /**
     * 流程序号
     */
    private String processSerialNumber;

    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 是否有内容，1为是，0为否
     */
    private String hasContent = "0";

}
