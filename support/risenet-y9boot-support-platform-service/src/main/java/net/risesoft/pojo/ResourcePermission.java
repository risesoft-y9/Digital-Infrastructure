package net.risesoft.pojo;

import java.io.Serializable;
import java.util.HashMap;

import lombok.Data;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Data
public class ResourcePermission implements Serializable {

    private static final long serialVersionUID = -369521235019935539L;
    /**
     * 授权时间
     */
    private String authorizeTime;
    /**
     * 授权者
     */
    private String authorizer;
    private HashMap<String, String> operationMap;
    private String resourceId;
    private String resourceName;
    private String roleName;
    private String roleNodeId;
    private String systemName;
    private String url;
    
}
