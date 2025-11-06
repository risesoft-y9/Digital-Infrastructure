package net.risesoft.example.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 描述：人员
 */
@Entity
@Table(name = "risesoft_demo_user")
@DynamicUpdate
@Comment("人员表")
@NoArgsConstructor
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 5938370940741691627L;

    /**
     * 主键
     */
    @Id
    @Column(name = "user_id", length = 38, nullable = false)
    @Comment("UUID字段")
    private String id;

    /**
     * 名称
     */
    @Column(name = "user_name", length = 50)
    private String name;

    /**
     * 年龄
     */
    @Column(name = "user_age", length = 10)
    private Integer age;

    /**
     * 性别
     */
    @Column(name = "user_sex", length = 10)
    private String sex;

    /**
     * 出生年月
     */
    @Column(name = "user_birth", length = 50)
    private String birth;

    /**
     * 教育
     */
    @Column(name = "user_education", length = 50)
    private String education;

    /**
     * 电话
     */
    @Column(name = "user_mobile", length = 50)
    private String mobile;

    /**
     * 文件系统id
     */
    @Column(name = "user_fileId", length = 50)
    private String fileId;

    /**
     * 创建时间
     */
    @Column(name = "user_createTime", length = 50)
    private String createTime;

}