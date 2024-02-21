package net.risesoft.model.platform;

import java.io.Serializable;

import lombok.Data;

/**
 * 职位
 *
 * @author shidaobang
 * @date 2022/10/17
 */
@Data
public class Job implements Serializable {

    private static final long serialVersionUID = 1878774546924910882L;

    /** 主键id */
    private String id;

    /** 数据代码 */
    private String code;

    /** 名称 */
    private String name;

    /** 排序号 */
    private Integer tabIndex;

}
