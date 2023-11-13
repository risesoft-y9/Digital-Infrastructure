package net.risesoft.model.platform;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

import net.risesoft.enums.platform.MaritalStatusEnum;

/**
 * 人员信息扩展
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Data
public class PersonExt implements Serializable {
    private static final long serialVersionUID = 1274186459521666925L;

    /**
     * 人员ID
     */
    protected String personId;

    /**
     * 名称
     */
    protected String name;

    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 照片
     */
    private byte[] photo;

    /**
     * 签名
     */
    private byte[] sign;

    /**
     * 出生日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date birthday;

    /**
     * 居住国家
     */
    private String country;

    /**
     * 居住城市
     */
    private String city;

    /**
     * 证件类型
     */
    private String idType;

    /**
     * 证件号码
     */
    private String idNum;

    /**
     * 人员籍贯
     */
    private String province;

    /**
     * 家庭地址
     */
    private String homeAddress;

    /**
     * 家庭电话
     */
    private String homePhone;

    /**
     * 政治面貌
     */
    private String politicalStatus;

    /**
     * 学历
     */
    private String education;

    /**
     * 婚姻状况
     */
    private MaritalStatusEnum maritalStatus;

    /**
     * 专业
     */
    private String professional;

    /**
     * 入职时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date workTime;

}