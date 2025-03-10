package y9.entity;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Entity(name = Y9KeyValue.ENTITY_NAME)
@Table(name = "Y9_COMMON_KEY_VALUE", indexes = {@Index(columnList = "EXPIRE_TIME")})
@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuperBuilder
@NoArgsConstructor
@Accessors(chain = true)
public class Y9KeyValue implements Serializable {
    private static final long serialVersionUID = -3351125761118770968L;

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
    private Instant expireTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Comment("创建时间")
    @CreationTimestamp
    @Column(name = "CREATE_TIME", updatable = false)
    private Instant createTime;

    /**
     * 创建时会自动设值
     *
     * @param createTime
     */
    private void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

}
