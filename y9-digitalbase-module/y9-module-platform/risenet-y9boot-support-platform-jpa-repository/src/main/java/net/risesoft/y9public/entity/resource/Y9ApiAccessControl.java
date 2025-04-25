package net.risesoft.y9public.entity.resource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;
import net.risesoft.consts.DefaultConsts;
import net.risesoft.enums.platform.ApiAccessControlType;

/**
 * 接口访问控制
 *
 * @author shidaobang
 * @date 2024/11/26
 * @since 9.6.8
 */
@Entity
@Table(name = "Y9_COMMON_API_ACCESS_CONTROL")
@DynamicUpdate
@org.hibernate.annotations.Table(comment = "接口访问控制", appliesTo = "Y9_COMMON_API_ACCESS_CONTROL")
@NoArgsConstructor
@Data
public class Y9ApiAccessControl extends BaseEntity {

    private static final long serialVersionUID = -6457348254550107305L;

    /** 主键 */
    @Id
    @Column(name = "ID", length = 38, nullable = false)
    @Comment("主键")
    private String id;

    /**
     * 不同的访问控制类型有不同的值 <br>
     * 其中黑白名单支持具体 IP、IP 网段格式，例子：192.168.1.1,192.168.1.0/24,192.168.1.1-100,192.168.1.*，多个用英文逗号分割
     */
    @Column(name = "ACL_VALUE", nullable = false)
    @Comment("不同的访问控制类型有不同的值")
    private String value;

    /** 访问控制类型：白名单、黑名单、appId-secret */
    @Column(name = "TYPE", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @Comment("访问控制类型：白名单、黑名单、appId-secret")
    private ApiAccessControlType type = ApiAccessControlType.WHITE_LIST;

    /** 是否启用:1=启用,0=禁用 */
    @Type(type = "numeric_boolean")
    @ColumnDefault("0")
    @Column(name = "ENABLED", nullable = false)
    @Comment("是否启用:1=启用,0=禁用")
    private Boolean enabled = Boolean.FALSE;

    /** 备注 */
    @Column(name = "REMARK")
    @Comment("备注")
    private String remark;

    /** 序列号 */
    @Column(name = "TAB_INDEX", nullable = false)
    @Comment("序列号")
    private Integer tabIndex = DefaultConsts.TAB_INDEX;
}
