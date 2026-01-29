package y9.entity;

import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

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
@Table(name = "Y9_COMMON_KEY_VALUE", indexes = {@Index(columnList = "EXPIRE_TIME")}, comment = "键值对")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuperBuilder
@NoArgsConstructor
@Accessors(chain = true)
public class Y9KeyValue implements Serializable {
	
    public static final String ENTITY_NAME = "Y9KeyValue";
    
    private static final long serialVersionUID = -3351125761118770968L;
    
    @Id
    @Column(name = "KV_KEY", comment = "键")
    private String key;

    @Column(name = "KV_VALUE", comment = "值")
    private String value;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "EXPIRE_TIME", comment = "过期时间")
    private Instant expireTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @CreationTimestamp
    @Column(name = "CREATE_TIME", updatable = false, comment = "创建时间")
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
