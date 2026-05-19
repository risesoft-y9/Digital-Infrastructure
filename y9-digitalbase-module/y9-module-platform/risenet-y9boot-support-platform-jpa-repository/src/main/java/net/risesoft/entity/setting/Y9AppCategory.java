package net.risesoft.entity.setting;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;

/**
 * 应用分类排序表
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_COMMON_APP_CATEGORY")
@org.hibernate.annotations.Table(comment = "应用分类排序表", appliesTo = "Y9_COMMON_APP_CATEGORY")
@NoArgsConstructor
@Data
public class Y9AppCategory extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -7873198033987361720L;

    /** 主键 */
    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38, nullable = false)
    private String id;

    /** 应用所属分类id */
    @Comment("应用所属分类id")
    @Column(name = "CATEGORY_ID", length = 38)
    private String categoryId;

    /** 应用id */
    @Comment("应用id")
    @Column(name = "APP_ID", length = 38)
    private String appId;

    /** 排序号 */
    @Comment("排序号")
    @Column(name = "TAB_INDEX")
    private Integer tabIndex;

}
