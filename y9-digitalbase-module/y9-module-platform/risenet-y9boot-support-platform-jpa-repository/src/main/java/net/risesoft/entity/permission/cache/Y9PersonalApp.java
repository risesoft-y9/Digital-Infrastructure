package net.risesoft.entity.permission.cache;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;
import net.risesoft.enums.platform.PersonalAppTabIndexTypeEnum;
import net.risesoft.persistence.EnumConverter;

/**
 * 个人应用分类排序缓存
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */

@Entity
@Table(name = "Y9_ORG_PERSONAL_APP",
    indexes = {@Index(name = "Y9_ORG_PERSONAL_APP_INDEX", columnList = "ORG_UNIT_ID,APP_ID", unique = true)})
@Comment("个人应用分类排序缓存")
@NoArgsConstructor
@Data
public class Y9PersonalApp extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 5721113214403204326L;

    /** UUID字段 */
    @Id
    @Column(name = "ID", length = 38, nullable = false)
    @Comment("UUID字段")
    private String id;

    /** 人员/岗位唯一主键 */
    @Comment("人员/岗位唯一主键")
    @Column(name = "ORG_UNIT_ID", length = 38)
    private String orgUnitId;

    /** 应用id */
    @Comment("应用id")
    @Column(name = "APP_ID", length = 38)
    private String appId;

    /** 分类id */
    @Comment("分类id")
    @Column(name = "CATEGORY_ID", length = 38)
    private String categoryId;

    /** 是否标星 */
    @Column(name = "IS_STAR", nullable = false)
    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @Comment("是否标星")
    private Boolean star = Boolean.FALSE;

    /** 排列序号类别 1:配置的默认排序 2:个人排序 */
    @Comment("排列序号类别 1:配置的默认排序 2:个人排序")
    @Column(name = "TAB_INDEX_TYPE")
    @Convert(converter = EnumConverter.PersonAppTabIndexTypeEnumConverter.class)
    private PersonalAppTabIndexTypeEnum tabIndexType = PersonalAppTabIndexTypeEnum.DEFAULT;

    /** 排列序号 */
    @Comment("排列序号")
    @Column(name = "TAB_INDEX", nullable = false)
    private Integer tabIndex = 0;

}
