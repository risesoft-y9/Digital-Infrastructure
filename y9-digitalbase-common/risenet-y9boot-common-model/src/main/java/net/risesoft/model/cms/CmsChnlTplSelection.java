package net.risesoft.model.cms;

import java.io.Serializable;

import lombok.Data;

/**
 * 栏目模板选择
 *
 * @author mengjuhua
 *
 */
@Data
public class CmsChnlTplSelection implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 唯一标识
     */
    private Integer id;

    /**
     * 模板id
     */
    private Integer modelId;

    /**
     * 模板路径
     */
    private String tplDoc;
}