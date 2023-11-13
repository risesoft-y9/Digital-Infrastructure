package net.risesoft.y9public.entity.tenant;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import org.hibernate.annotations.Comment;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;
import net.risesoft.enums.platform.DataSourceTypeEnum;
import net.risesoft.persistence.EnumConverter;

/**
 * 数据源基本信息表
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_COMMON_DATASOURCE")
@Comment("数据源基本信息表")
@NoArgsConstructor
@Data
public class Y9DataSource extends BaseEntity {

    private static final long serialVersionUID = 4824010195634081642L;

    /** 主键 */
    @Id
    @Column(name = "ID", length = 38, nullable = false)
    @Comment("主键")
    private String id;

    /**
     * 数据源类型1=jndi; 2=hikari
     *
     * {@link DataSourceTypeEnum}
     */
    @Column(name = "TYPE")
    @Comment("数据源类型1=jndi; 2=hikari")
    @Convert(converter = EnumConverter.DataSourceTypeEnumConverter.class)
    private DataSourceTypeEnum type;

    /** 数据源名称 */
    @NotBlank
    @Column(name = "JNDI_NAME", length = 100, nullable = false)
    @Comment("数据源名称")
    private String jndiName;

    /** 驱动 */
    @Column(name = "DRIVER", length = 100)
    @Comment("驱动")
    private String driver;

    /** 路径 */
    @Column(name = "URL", length = 300)
    @Comment("路径")
    private String url;

    /** 用户名 */
    @Column(name = "USERNAME", length = 50)
    @Comment("用户名")
    private String username;

    /** 密码 */
    @Column(name = "PASSWORD", length = 20)
    @Comment("密码")
    private String password;

    /** 连接池初始化大小 */
    @Column(name = "INITIAL_SIZE")
    @Comment("连接池初始化大小")
    private Integer initialSize;

    /** 连接池最大值 */
    @Column(name = "MAX_ACTIVE")
    @Comment("连接池最大值")
    private Integer maxActive;

    /** 连接池最小值 */
    @Column(name = "MIN_IDLE")
    @Comment("连接池最小值")
    private Integer minIdle;

}
