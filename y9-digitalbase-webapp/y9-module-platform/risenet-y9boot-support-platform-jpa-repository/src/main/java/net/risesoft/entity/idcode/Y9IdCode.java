package net.risesoft.entity.idcode;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Y9_ORG_IDCODE")
@DynamicUpdate
@Comment("统一码信息表")
@NoArgsConstructor
@Data
public class Y9IdCode implements Serializable {

    private static final long serialVersionUID = -6474908965306317186L;

    @Id
    @Column(name = "ID", length = 100)
    @Comment("idCode码")
    private String id;

    @Comment("品类注册ID")
    @Column(name = "CATEGORY_REG_ID", nullable = false, unique = true, length = 50)
    private String regId;

    @Comment("二维码地址")
    @Column(name = "IMG_URL", length = 200)
    private String imgUrl;

    @Comment("组织ID")
    @Column(name = "ORG_UNIT_ID", nullable = false, unique = true, length = 50)
    private String orgUnitId;
}