package net.risesoft.model.dataservice;

import java.io.Serializable;

import lombok.Data;

/**
 * 人员信息表
 */
@Data
public class PersonResourcesModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 分类id
     */
    private String departId;

    /**
     * 团队id（扩展字段）
     */
    private String teamInfoId;

    /**
     * 组织人员id（扩展字段）
     */
    private String personId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 所属公司
     */
    private String company;

    /**
     * 身份证号码
     */
    private String ID_Card;

    /**
     * 性别
     */
    private String sex;

    /**
     * 出生年月
     */
    private String birth;

    /**
     * 学历
     */
    private String education;

    /**
     * 学位
     */
    private String degree;

    /**
     * 是否为国家承认学历 0:是 1：不是
     */
    private String istrue;

    /**
     * 毕业学校
     */
    private String school;

    /**
     * 所学专业
     */
    private String professional;

    /**
     * 毕业时间
     */
    private String graduation;

    /**
     * 入职时间
     */
    private String induction;

    /**
     * 入职说明
     */
    private String instructions;

    /**
     * 离职时间
     */
    private String leave;

    /**
     * 离职说明
     */
    private String leaveThat;

    /**
     * 所属部门
     */
    private String department;

    /**
     * 职务
     */
    private String duty;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 座机号码
     */
    private String landline;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 技术职称（国家认可）
     */
    private String technical;

    /**
     * 劳动合同起点
     */
    private String laborStart;

    /**
     * 劳动合同终点
     */
    private String laborEnd;

    /**
     * 备注
     */
    private String remark;

    /**
     * 0:离职 1：在职
     */
    private String disabled;

    /**
     * 排序号
     */
    private Integer tabIndex;

    /**
     * 特殊排序
     */
    private Integer specialIndex;

    /**
     * 创建时间
     */
    private String createDate;

}
