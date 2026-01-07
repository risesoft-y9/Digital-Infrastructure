package net.risesoft.pojo;

import java.io.Serializable;

import lombok.Data;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 数据目录的 excel 导入导出
 *
 * @author shidaobang
 * @date 2026/01/07
 */
@Data
public class DataCatalog4Excel implements Serializable {

    private static final long serialVersionUID = 5749253240867865995L;

    /**
     * 名称
     */
    @ExcelProperty("名称")
    private String name;

    /**
     * 所属目录（用英文逗号分割各级目录）
     */
    @ExcelProperty("所属目录（用英文逗号分割各级目录）")
    private String ancestorNamePath;

    /**
     * 自定义 id
     */
    @ExcelProperty("自定义 id")
    private String customId;

    /**
     * 描述
     */
    @ExcelProperty("描述")
    private String description;

}
