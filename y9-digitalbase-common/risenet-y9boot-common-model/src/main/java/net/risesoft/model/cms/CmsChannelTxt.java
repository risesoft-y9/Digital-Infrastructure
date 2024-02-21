package net.risesoft.model.cms;

import java.io.Serializable;

import lombok.Data;

/**
 * 内容管理的栏目说明信息
 *
 * @author mengjuhua
 */
@Data
public class CmsChannelTxt implements Serializable {

    private static final long serialVersionUID = 6217104897365538032L;

    /**
     * 唯一标识
     */
    private java.lang.Integer id;

    /**
     * 栏目说明
     */
    private java.lang.String txtval;

}