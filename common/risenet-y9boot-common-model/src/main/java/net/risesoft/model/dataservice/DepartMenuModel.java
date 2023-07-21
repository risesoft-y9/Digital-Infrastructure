package net.risesoft.model.dataservice;

import java.io.Serializable;

import lombok.Data;

/**
 * 人力资源地区信息表
 * 
 * @author mjh
 *
 */
@Data
public class DepartMenuModel implements Serializable {

    private static final long serialVersionUID = 1L;

    // 主键id
    private String id;

    // 名称
    private String name;

    // 排序号
    private Integer tabIndex;

    // 创建时间
    private String createDate;

}
