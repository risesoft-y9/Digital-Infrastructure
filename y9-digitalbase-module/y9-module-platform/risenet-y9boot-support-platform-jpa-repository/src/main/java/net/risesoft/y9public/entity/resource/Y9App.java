package net.risesoft.y9public.entity.resource;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import net.risesoft.enums.platform.resource.AppOpenTypeEnum;
import net.risesoft.enums.platform.resource.AppTypeEnum;
import net.risesoft.enums.platform.resource.ResourceTypeEnum;
import net.risesoft.persistence.EnumConverter;

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
@DynamicUpdate
@Comment("应用市场表")
@Data
@SuperBuilder
@NoArgsConstructor
public class Y9App extends Y9ResourceBase {

    private static final long serialVersionUID = 1771730705695533602L;

    {
        super.setResourceType(ResourceTypeEnum.APP);
    }

    /** 资源别名 */
    @Column(name = "ALIAS_NAME", length = 255)
    @Comment("资源别名")
    private String aliasName;

    /** 是否审核通过 */
    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @ColumnDefault("0")
    @Column(name = "CHECKED", nullable = false)
    @Comment("是否审核通过")
    @Builder.Default
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
    @Builder.Default
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

    /** app类型：1业务协同,2事项办理,3数据服务 */
    @Column(name = "TYPE", nullable = false)
    @Comment("分类")
    @Convert(converter = EnumConverter.AppTypeEnumConverter.class)
    @Builder.Default
    private AppTypeEnum type = AppTypeEnum.WORKFLOW;

    /** 应用打开方式:0在桌面窗口打开；1在新浏览器窗口打开 */
    @Column(name = "OPEN_TYPE", nullable = false)
    @Comment(value = "应用打开方式:0在桌面窗口打开；1在新浏览器窗口打开")
    @Convert(converter = EnumConverter.AppOpenTypeEnumConverter.class)
    @Builder.Default
    private AppOpenTypeEnum opentype = AppOpenTypeEnum.BROWSE;

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
    @Builder.Default
    private Boolean autoInit = Boolean.FALSE;

    @Override
    public String getAppId() {
        return id;
    }

    @Override
    public String getParentId() {
        return null;
    }

}
