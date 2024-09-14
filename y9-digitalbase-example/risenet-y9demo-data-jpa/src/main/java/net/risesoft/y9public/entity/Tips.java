package net.risesoft.y9public.entity;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "RISESOFT_DEMO_TIPS")
@DynamicUpdate
@Comment("提示详情")
@NoArgsConstructor
@Data
public class Tips implements Serializable {
    private static final long serialVersionUID = 5858697659406924473L;

    @Id
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    @Comment(value = "主键")
    private String id;

    @Column(name = "USER_ID", length = 50)
    @Comment(value = "人员主键")
    private String userId;

    @Column(name = "MESSAGE", length = 1000)
    @Comment(value = "提示详情")
    private String message;

    @Column(name = "LINK", length = 500)
    @Comment(value = "链接地址")
    private String link;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_TIME")
    @Comment(value = "创建时间")
    private Date createTime;

}
