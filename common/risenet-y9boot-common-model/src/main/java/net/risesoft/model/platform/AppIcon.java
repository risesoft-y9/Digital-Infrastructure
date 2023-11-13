package net.risesoft.model.platform;

import java.io.Serializable;

import lombok.Data;

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
public class AppIcon implements Serializable {

    private static final long serialVersionUID = -9188764434950145342L;

    /**
     * 唯一标识
     */
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 类型
     */
    private String type;

    /**
     * 创建时间
     */
    private String createDateTime;

    /**
     * 更新时间
     */
    private String updateDateTime;

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

}
