package net.risesoft.y9.configuration.app.y9subscription;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.y9.configuration.app.y9subscription.minjian.MinjianProperties;
import net.risesoft.y9.configuration.app.y9subscription.risesoft.RiseSoftProperties;
import net.risesoft.y9.configuration.app.y9subscription.visitor.VisitorProperties;
import net.risesoft.y9.configuration.app.y9subscription.zgcsa.ZGCSAProperties;

@Getter
@Setter
public class Y9SubscriptionProperties {

    /**
     * 推送主题父节点id
     */
    private String adminRoleNode = "26afefdf09a446028cb8ebfad04a648c";

    private String baseUrl = "http://www.youshengyun.com/subscription";

    private String defaultArticleImage = "/subscription/static/images/Y9Tree3.png";

    private String defaultDeptAccountAvatar = "/subscription/static/images/subscription.png";

    private boolean sendToDataCenter = false;

    private String serverName;

    @NestedConfigurationProperty
    private VisitorProperties visitor = new VisitorProperties();

    @NestedConfigurationProperty
    private RiseSoftProperties riseSoft = new RiseSoftProperties();

    @NestedConfigurationProperty
    private ZGCSAProperties zgcsa = new ZGCSAProperties();

    @NestedConfigurationProperty
    private MinjianProperties minjian = new MinjianProperties();

}
