package net.risesoft.oidc.y9.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Entity(name = Y9KeyValue.ENTITY_NAME)
@Table(name = "Y9_COMMON_KEY_VALUE", indexes = {@Index(columnList = "EXPIRE_TIME")})
@Comment("键值对")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuperBuilder
@NoArgsConstructor
@Slf4j
@Accessors(chain = true)
public class Y9KeyValue implements Serializable {
    public static final String ENTITY_NAME = "Y9KeyValue";

    @Serial
    private static final long serialVersionUID = 8188041534553034381L;

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
