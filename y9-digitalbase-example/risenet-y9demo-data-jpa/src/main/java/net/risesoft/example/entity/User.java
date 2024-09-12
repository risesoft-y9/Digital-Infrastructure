package net.risesoft.example.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 描述：人员
 */
@Entity
@Table(name = "RISESOFT_DEMO_USER")
@DynamicUpdate
@org.hibernate.annotations.Table(comment = "人员表", appliesTo = "RISESOFT_DEMO_USER")
@NoArgsConstructor
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 5938370940741691627L;

    /**
     * 主键
     */
    @Id
    @Column(name = "USER_ID", length = 38, nullable = false)
    @Comment("UUID字段")
    private String id;

    /**
     * 名称
     */
    @Column(name = "USER_NAME", length = 50)
    private String name;

    /**
     * 年龄
     */
    @Column(name = "USER_AGE", length = 10)
    private Integer age;

    /**
     * 性别
     */
    @Column(name = "USER_SEX", length = 10)
    private String sex;

    /**
     * 出生年月
     */
    @Column(name = "USER_BIRTH", length = 50)
    private String birth;

    /**
     * 教育
     */
    @Column(name = "USER_EDUCATION", length = 50)
    private String education;

    /**
     * 电话
     */
    @Column(name = "USER_MOBILE", length = 50)
    private String mobile;


    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME", length = 50)
    private String createTime;

}