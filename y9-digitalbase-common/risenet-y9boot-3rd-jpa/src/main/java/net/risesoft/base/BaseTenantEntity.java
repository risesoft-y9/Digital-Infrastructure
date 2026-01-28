package net.risesoft.base;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.EqualsAndHashCode;
import lombok.Getter;
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
@Getter
@SuperBuilder
@EntityListeners(BaseTenantEntityListener.class)
public class BaseTenantEntity implements Serializable {

    private static final long serialVersionUID = 7442864321155282821L;

    /** 租户id */
    @Column(name = "TENANT_ID", length = 38, comment = "租户id")
    protected String tenantId;

    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @CreationTimestamp
    @Column(name = "CREATE_TIME", updatable = false, comment = "创建时间")
    protected Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @UpdateTimestamp
    @Column(name = "UPDATE_TIME", comment = "更新时间")
    protected Date updateTime;

    /**
     * 会自动设值
     *
     * @param tenantId
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * 创建时会自动设值
     *
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 更新时会自动设值
     *
     * @param updateTime
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
