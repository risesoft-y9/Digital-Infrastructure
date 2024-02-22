package net.risesoft.y9.configuration.feature.apisix;

import lombok.Getter;
import lombok.Setter;

/**
 * apisix 属性配置
 *
 * @author liansen
 * @date 2022/09/26
 */
@Getter
@Setter
public class Y9ApisixProperties {

    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * 管理密钥
     */
    private String adminKey;

    /**
     * 管理地址
     */
    private String adminAddress;

    /**
     * upstream 节点
     */
    private String upstreamNodes;

    /**
     * upstream 类型
     */
    private String upstreamType;

    /**
     * api 包路径
     */
    private String apiBasePackages;

    /**
     * api 版本
     */
    private String apiVersion;

    /**
     * api 域
     */
    private String apiDomain;

    /**
     * etcd 地址
     */
    private String etcdAddress;

    /**
     * consul 地址
     */
    private String consulAddress;

    /**
     * 是否启用消费者
     */
    private boolean consumerEnabled = false;

    /**
     * 验证类型
     */
    private String authenticationType = "key-auth";

}
