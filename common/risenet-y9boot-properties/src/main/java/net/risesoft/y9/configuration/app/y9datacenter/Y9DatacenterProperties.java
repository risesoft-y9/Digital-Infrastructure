package net.risesoft.y9.configuration.app.y9datacenter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Y9DatacenterProperties {

    private String systemName = "datacenter";

    private String systemCnName = "数据服务门户";

    private String articleUrl = "http://www.youshengyun.com/subscription/user/article?articleId=";

    private String newsUrl = "http://www.youshengyun.com";

    private boolean saveNeo4j = false;

    private String kafkaServer;

}
