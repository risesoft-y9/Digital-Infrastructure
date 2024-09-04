package net.risesoft.elastic.entity;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 描述：人员
 */
@Document(indexName = "risesoftdemouser")
public class User implements Serializable {

    private static final long serialVersionUID = 5938370940741691627L;

    /**
     * 主键
     */
    @Id
    private String id;

    /**
     * 名称
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String name;

    /**
     * 年龄
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private Integer age;

    /**
     * 性别
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String sex;

    /**
     * 出生年月
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String birth;

    /**
     * 教育
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String education;

    /**
     * 电话
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String mobile;

    /**
     * 文件系统id
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String fileId;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String createTime;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return this.age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirth() {
        return this.birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getEducation() {
        return this.education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", age=" + age + ", sex=" + sex + ", birth=" + birth
            + ", education=" + education + ", mobile=" + mobile + ", fileId=" + fileId + ", createTime=" + createTime
            + "]";
    }

}