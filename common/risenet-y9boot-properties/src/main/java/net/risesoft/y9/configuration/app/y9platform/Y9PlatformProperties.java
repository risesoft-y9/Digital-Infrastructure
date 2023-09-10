package net.risesoft.y9.configuration.app.y9platform;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Y9PlatformProperties {

    /**
     * 系统名称
     */
    private String systemName = "riseplatform";
    
    /**
     * 岗位名称格式，默认格式为 职位名称（人员名称），例 总经理（张三） {0} 职位名称 {1} 人员名称
     */
    private String positionNamePattern = "{0}（{1}）";

}
