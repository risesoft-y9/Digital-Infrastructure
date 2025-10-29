package net.risesoft.model.platform.org;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

import net.risesoft.enums.platform.org.OrgTypeEnum;

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
public class CustomGroupMember implements Serializable {

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
    private String memberId;

    /**
     * 所在群组id
     */
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
     * 成员类型为person时，人员的性别
     */
    private Integer sex;

    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime = new Date();

    /**
     * 修改时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 排序
     */
    private Integer tabIndex;

}
