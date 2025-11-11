package net.risesoft.model.platform;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

import net.risesoft.enums.platform.ApiAccessControlType;
import net.risesoft.model.BaseModel;

/**
 * 接口访问控制
 *
 * @author shidaobang
 * @date 2025/10/31
 */
@Data
public class ApiAccessControl extends BaseModel {

    private static final long serialVersionUID = -449458656068638930L;

    /** 主键 */
    private String id;

    /**
     * 不同的访问控制类型有不同的值 <br>
     * 其中黑白名单支持具体 IP、IP 网段格式，例子：192.168.1.1,192.168.1.0/24,192.168.1.1-100,192.168.1.*，多个用英文逗号分割
     */
    @NotBlank
    private String value;

    /** 访问控制类型：白名单、黑名单、appId-secret */
    @NotNull
    private ApiAccessControlType type;

    /** 是否启用 */
    private Boolean enabled;

    /** 备注 */
    private String remark;

    /** 序列号 */
    private Integer tabIndex;
}
