package org.apereo.cas.services;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Y9_COMMON_KEY_VALUE", indexes = {@Index(columnList = "EXPIRE_TIME")})
@Comment("键值对")
@NoArgsConstructor
@Data
public class Y9KeyValue implements Serializable {
    public static final String ENTITY_NAME = "Y9KeyValue";

    @Id
    @Column(name = "KV_KEY")
    @Comment("键")
    private String key;

    @Column(name = "KV_VALUE")
    @Comment("值")
    private String value;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Comment("过期时间")
    @Column(name = "EXPIRE_TIME")
    private Date expireTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Comment("创建时间")
    @CreationTimestamp
    @Column(name = "CREATE_TIME", updatable = false)
    private Date createTime;

    /**
     * 创建时会自动设值
     *
     * @param createTime
     */
    private void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}
