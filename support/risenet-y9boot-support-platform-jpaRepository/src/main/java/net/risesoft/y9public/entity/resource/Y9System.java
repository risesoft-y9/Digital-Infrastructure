package net.risesoft.y9public.entity.resource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Type;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;

/**
 * 系统信息表
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_COMMON_SYSTEM")
@org.hibernate.annotations.Table(comment = "系统信息表", appliesTo = "Y9_COMMON_SYSTEM")
@NoArgsConstructor
@Data
public class Y9System extends BaseEntity {

    private static final long serialVersionUID = 8905896381019503361L;

    /** 主键 */
    @Id
    @Column(name = "ID", length = 38, nullable = false)
    @Comment("主键")
    private String id;

    /** 开发商GUID */
    @Column(name = "ISV_GUID", length = 38)
    @Comment("开发商GUID")
    private String isvGuid;

    /** 系统名称 */
    @NotBlank
    @Column(name = "NAME", length = 50, nullable = false, unique = true)
    @Comment("系统名称")
    private String name;

    /** 系统中文名称 */
    @NotBlank
    @Column(name = "CN_NAME", length = 50, nullable = false)
    @Comment("系统中文名称")
    private String cnName;

    /** 描述 */
    @Column(name = "DESCRIPTION", length = 255)
    @Comment("描述")
    private String description;

    /** 系统程序上下文 */
    @Column(name = "CONTEXT_PATH", length = 50)
    @Comment("系统程序上下文")
    private String contextPath;

    /** 是否启用 */
    @Type(type = "numeric_boolean")
    @Column(name = "ENABLED")
    @Comment("是否启用")
    private Boolean enabled;

    /** 排序 */
    @Column(name = "TAB_INDEX", nullable = false)
    @Comment("排序")
    private Integer tabIndex = 0;

    /** 是否启用独立数据源 */
    @Type(type = "numeric_boolean")
    @Column(name = "SINGE_DATASOURCE", nullable = false)
    @Comment("是否启用独立数据源")
    @ColumnDefault("0")
    private Boolean singleDatasource = Boolean.FALSE;

    /** 是否自动租用系统 */
    @Type(type = "numeric_boolean")
    @Column(name = "AUTO_INIT", nullable = false)
    @Comment("是否自动租用系统")
    @ColumnDefault("0")
    private Boolean autoInit = Boolean.FALSE;
}
