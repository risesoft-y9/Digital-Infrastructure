package net.risesoft.model.platform;

import java.io.Serializable;

import lombok.Data;

/**
 * 统一码
 *
 * @author shidaobang
 * @date 2025/11/05
 */
@Data
public class IdCode implements Serializable {

    private static final long serialVersionUID = 6083819628288675514L;

    /**
     * id
     */
    private String id;

    /**
     * 品类注册ID
     */
    private String regId;

    /**
     * 二维码地址
     */
    private String imgUrl;

    /**
     * 组织节点ID
     */
    private String orgUnitId;

}