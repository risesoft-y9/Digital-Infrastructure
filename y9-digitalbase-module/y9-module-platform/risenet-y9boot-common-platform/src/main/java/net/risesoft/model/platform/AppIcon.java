package net.risesoft.model.platform;

import javax.validation.constraints.NotBlank;

import lombok.Data;

import net.risesoft.model.BaseModel;

/**
 * 图标实体类
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Data
public class AppIcon extends BaseModel {

    private static final long serialVersionUID = -9188764434950145342L;

    /**
     * 唯一标识
     */
    private String id;

    /**
     * 名称
     */
    @NotBlank
    private String name;

    /**
     * 类型
     */
    private String type;

    /**
     * 上传路径
     */
    private String path;

    /**
     * 备注
     */
    private String remark;

    /**
     * 图标图片的base64
     */
    private String iconData;

    /**
     * 颜色类型
     */
    private String colorType;

    /**
     * 所属类别
     */
    private String category;

}
