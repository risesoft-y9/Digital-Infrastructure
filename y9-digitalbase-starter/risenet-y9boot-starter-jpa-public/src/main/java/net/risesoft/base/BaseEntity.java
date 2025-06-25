package net.risesoft.base;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 实体基类
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@MappedSuperclass
@EqualsAndHashCode
@NoArgsConstructor
@SuperBuilder
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 7442864321155282821L;

    public static final String PROPERTY_CREATE_TIME = "createTime";

    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Comment("创建时间")
    @CreationTimestamp
    @Column(name = "CREATE_TIME", updatable = false)
    protected Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Comment("更新时间")
    @UpdateTimestamp
    @Column(name = "UPDATE_TIME")
    protected Date updateTime;

    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 创建时会自动设值
     * 
     * @param createTime
     */
    private void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 更新时会自动设值
     * 
     * @param updateTime
     */
    private void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
