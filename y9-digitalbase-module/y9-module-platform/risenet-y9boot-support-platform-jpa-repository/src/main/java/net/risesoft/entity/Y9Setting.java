package net.risesoft.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;

/**
 * Y9组织身份设置
 *
 * @author shidaobang
 * @date 2024/03/27
 */
@Entity
@Table(name = "Y9_ORG_SETTING")
@DynamicUpdate
@org.hibernate.annotations.Table(comment = "设置表", appliesTo = "Y9_ORG_SETTING")
@NoArgsConstructor
@Data
public class Y9Setting extends BaseEntity {

    @Id
    @Column(name = "SETTING_KEY")
    @Comment("设置key")
    private String key;

    @Column(name = "SETTING_VALUE")
    @Comment("设置value")
    private String value;

}
