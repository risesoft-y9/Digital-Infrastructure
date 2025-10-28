package net.risesoft.query.platform;

import lombok.Builder;
import lombok.Data;

/**
 * 人员查询
 *
 * @author shidaobang
 * @date 2025/10/28
 */
@Data
@Builder
public class PersonQuery {

    /**
     * 人员名称
     */
    private String name;

    /**
     * 父节点id
     */
    private String parentId;

    /**
     * 是否禁用
     */
    private Boolean disabled;

}
