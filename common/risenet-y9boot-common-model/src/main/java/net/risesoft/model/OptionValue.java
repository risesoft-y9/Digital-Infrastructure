package net.risesoft.model;

import java.io.Serializable;

import lombok.Data;

/**
 * 字典数据
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Data
public class OptionValue implements Serializable {
    private static final long serialVersionUID = -3091695448276608201L;

    /**
     * 唯一标识
     */
    private String id;

    /**
     * 数据代码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 排序号
     */
    private Integer tabIndex;

    /**
     * 字典类型
     */
    private String type;
}
