package net.risesoft.entity.idcode;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Y9_ORG_IDCODE")
@DynamicUpdate
@org.hibernate.annotations.Table(comment = "统一码信息表", appliesTo = "Y9_ORG_IDCODE")
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