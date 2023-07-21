package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 签名照片
 *
 */
@Data
public class SignaturePictureModel implements Serializable {

    private static final long serialVersionUID = -6941227076957244692L;

    /**
     * 唯一标示
     */
    private String id;

    /**
     * 文件仓库Id
     */
    private String fileStoreId;

    /**
     * 租户Id
     */
    private String tenantId;

    /**
     * 签名归属人员id
     */
    private String userId;

    /**
     * 签名归属人员名称
     */
    private String userName;

    /**
     * 生成时间
     */
    private String createDate;

    /**
     * 最后的修改时间
     */
    private String modifyDate;

}
