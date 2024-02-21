package net.risesoft.model.user;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

/**
 * 人员信息标签
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 *
 */
@Data
public class UserProfile implements Serializable {
    private static final long serialVersionUID = -9003196864955967463L;

    /** 主键 */
    private String id;

    /** 参数 */
    private Map<String, Object> attributes = new HashMap<>(16);
}
