package net.risesoft.pojo;

import java.io.Serializable;

import lombok.Data;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Data
public class PersonInformation implements Serializable {

    private static final long serialVersionUID = 8092547953292121627L;

    @ExcelProperty("中文名称")
    private String name;

    @ExcelProperty("登录名称")
    private String loginName;

    @ExcelProperty("所属部门（用英文逗号分割各级部门）")
    private String departmentNamePath;

    @ExcelProperty("性别")
    private String sex;

    @ExcelProperty("手机号码")
    private String mobile;

    @ExcelProperty("邮箱")
    private String email;

    @ExcelProperty("职务（职位）（多个用英文逗号分割）")
    private String jobs;

}
