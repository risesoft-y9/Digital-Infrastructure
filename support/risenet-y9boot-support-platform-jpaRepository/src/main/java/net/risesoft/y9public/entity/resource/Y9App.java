package net.risesoft.y9public.entity.resource;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import net.risesoft.enums.AppOpenTypeEnum;
import net.risesoft.enums.AppTypeEnum;
import net.risesoft.enums.ResourceTypeEnum;

/**
 * 应用市场表
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_COMMON_APP_STORE")
@Comment("应用市场表")
@Data
public class Y9App extends Y9ResourceBase {

    private static final long serialVersionUID = 1771730705695533602L;

    /** 资源别名 */
    @Column(name = "ALIAS_NAME", length = 255)
    @Comment("资源别名")
    private String aliasName;

    /** 是否审核通过 */
    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @ColumnDefault("0")
    @Column(name = "CHECKED", nullable = false)
    @Comment("是否审核通过")
    private Boolean checked = Boolean.FALSE;

    /** 审核人 */
    @Column(name = "VERIFY_USER_NAME", length = 30)
    @Comment("审核人")
    private String verifyUserName;

    /** 是否显示右上角数字，0=不显示，1=显示 */
    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @ColumnDefault("0")
    @Column(name = "SHOW_NUMBER", nullable = false)
    @Comment("是否显示右上角数字，0=不显示，1=显示")
    private Boolean showNumber = Boolean.FALSE;

    /** 获取数字的URL */
    @Column(name = "NUMBER_URL", length = 255)
    @Comment("获取数字的URL")
    private String numberUrl;

    /** 角色管理的URL */
    @Column(name = "ROLE_ADMIN_URL", length = 255)
    @Comment("角色管理的URL")
    private String roleAdminUrl;

    /** 资源管理的URL */
    @Column(name = "RESOURCE_ADMIN_URL", length = 255)
    @Comment("资源管理的URL")
    private String resourceAdminUrl;

    /**
     * app类型：1业务协同,2事项办理,3数据服务
     * 
     * {@link AppTypeEnum}
     */
    @Column(name = "TYPE")
    @Comment("分类")
    private Integer type = AppTypeEnum.WORKFLOW.getValue();

    /**
     * 应用打开方式:0在桌面窗口打开；1在新浏览器窗口打开
     * 
     * {@link AppOpenTypeEnum}
     */
    @Column(name = "OPEN_TYPE")
    @Comment(value = "应用打开方式:0在桌面窗口打开；1在新浏览器窗口打开")
    private Integer opentype = AppOpenTypeEnum.BROWSE.getValue();

    /** 图标图片的base64 */
    @Lob
    @Column(name = "ICON_DATA", nullable = true)
    @Comment("图标图片的base64")
    private String iconData;

    /** 是否自动租用应用 */
    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @Column(name = "AUTO_INIT", nullable = false)
    @Comment("是否自动租用应用")
    @ColumnDefault("0")
    private Boolean autoInit = Boolean.FALSE;

    /** 租用状态 */
    @Transient
    private String tenancyStatus;

    public Y9App() {
        super.setResourceType(ResourceTypeEnum.APP.getValue());
    }

    @Override
    public String getAppId() {
        return this.id;
    }

}
