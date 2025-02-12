package net.risesoft.dataio;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * excel 导入错误
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExcelImportError implements Serializable {

    private static final long serialVersionUID = 3243769817275296845L;

    /**
     * 行数
     */
    private Integer row;

    /**
     * 错误信息
     */
    private String msg;
}
