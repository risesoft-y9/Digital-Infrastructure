package net.risesoft.model.datacenter;

import java.io.Serializable;

import lombok.Data;

/**
 * 办件表单信息
 * 
 * @author Think
 *
 */
@Data
public class EformInfo implements Serializable {

    private static final long serialVersionUID = -11853904695870303L;

    /**
     * 模板中文名称
     */
    private String eformName;

    /**
     * 字段名，&拼接
     */
    private String fieldNames;

    /**
     * 值value，&拼接
     */
    private String fieldValues;

}
