package net.risesoft.model.platform.org;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.model.BaseModel;

/**
 * 自定义群组中的成员
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Data
public class CustomGroupMember extends BaseModel {

    private static final long serialVersionUID = 1337171458162811639L;

    /**
     * 主键
     */
    private String id;

    /**
     * 成员名称
     */
    private String memberName;

    /**
     * 成员id
     */
    @NotBlank
    private String memberId;

    /**
     * 成员类型为person时，人员的性别
     */
    private Integer sex;

    /**
     * 所在群组id
     */
    @NotBlank
    private String groupId;

    /**
     * 所在组织架构父节点id
     */
    private String parentId;

    /**
     * 成员类型
     */
    private OrgTypeEnum memberType;

    /**
     * 排序
     */
    private Integer tabIndex;

}
