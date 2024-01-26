package net.risesoft.y9public.entity.resource;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;

/**
 * 图标实体类
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_COMMON_APP_ICON")
@org.hibernate.annotations.Table(comment = "图标实体表", appliesTo = "Y9_COMMON_APP_ICON")
@NoArgsConstructor
@Data
public class Y9AppIcon extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 8320658336416173052L;

    /** 主键 */
    @Id
    @Column(name = "ID", length = 38)
    @Comment("主键")
    private String id;

    /** 名称 */
    @Column(name = "NAME", length = 255, nullable = false)
    @Comment("名称")
    private String name;

    /** 类型 */
    @Column(name = "ICON_TYPE", length = 38)
    @Comment("类型")
    private String type;

    /** 地址：Y9FileStore 的 id */
    @Column(name = "PATH", length = 20)
    @Comment("地址：Y9FileStore 的 id")
    private String path;

    /** 备注 */
    @Column(name = "REMARK", length = 200)
    @Comment("备注")
    private String remark;

    /** 图标图片的base64 */
    @Lob
    @Column(name = "ICON_DATA", nullable = true)
    @Comment("图标图片的base64")
    private String iconData;

}
