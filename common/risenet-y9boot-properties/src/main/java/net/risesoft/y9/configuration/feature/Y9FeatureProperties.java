package net.risesoft.y9.configuration.feature;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.y9.configuration.feature.apisix.Y9ApisixProperties;
import net.risesoft.y9.configuration.feature.cache.Y9CacheProperties;
import net.risesoft.y9.configuration.feature.cxf.Y9CxfProperties;
import net.risesoft.y9.configuration.feature.elasticsearch.Y9ElasticProperties;
import net.risesoft.y9.configuration.feature.file.Y9FileProperties;
import net.risesoft.y9.configuration.feature.idgenerator.Y9IdGeneratorProperties;
import net.risesoft.y9.configuration.feature.jpa.Y9JpaProperties;
import net.risesoft.y9.configuration.feature.jwt.Y9JwtProperties;
import net.risesoft.y9.configuration.feature.license.Y9LicenseProperties;
import net.risesoft.y9.configuration.feature.listener.Y9ListenerProperties;
import net.risesoft.y9.configuration.feature.log.Y9LogProperties;
import net.risesoft.y9.configuration.feature.mongo.Y9MongoProperties;
import net.risesoft.y9.configuration.feature.oauth2.Y9Oauth2Properties;
import net.risesoft.y9.configuration.feature.permission.Y9PermissionProperties;
import net.risesoft.y9.configuration.feature.publish.Y9PublishProperties;
import net.risesoft.y9.configuration.feature.quartz.Y9QuartzProperties;
import net.risesoft.y9.configuration.feature.security.Y9SecurityProperties;
import net.risesoft.y9.configuration.feature.session.Y9sessionProperties;
import net.risesoft.y9.configuration.feature.sms.Y9SmsProperties;
import net.risesoft.y9.configuration.feature.sso.Y9SsoClientProperties;

/**
 * 特性、功能配置
 *
 * @author liansen
 * @date 2022/09/28
 */
@Getter
@Setter
public class Y9FeatureProperties {

    /**
     * 缓存
     */
    @NestedConfigurationProperty
    private Y9CacheProperties cache = new Y9CacheProperties();

    /**
     * cxf
     */
    @NestedConfigurationProperty
    private Y9CxfProperties cxf = new Y9CxfProperties();

    /**
     * apisix
     */
    @NestedConfigurationProperty
    private Y9ApisixProperties apisix = new Y9ApisixProperties();

    /**
     * 文件存储
     */
    @NestedConfigurationProperty
    private Y9FileProperties file = new Y9FileProperties();

    /**
     * id生成器
     */
    @NestedConfigurationProperty
    private Y9IdGeneratorProperties idGenerator = new Y9IdGeneratorProperties();

    /**
     * jpa
     */
    @NestedConfigurationProperty
    private Y9JpaProperties jpa = new Y9JpaProperties();

    /**
     * mongo
     */
    @NestedConfigurationProperty
    private Y9MongoProperties mongo = new Y9MongoProperties();

    /**
     * elasticsearch
     */
    @NestedConfigurationProperty
    private Y9ElasticProperties elasticsearch = new Y9ElasticProperties();

    /**
     * 日志
     */
    @NestedConfigurationProperty
    private Y9LogProperties log = new Y9LogProperties();

    /**
     * 权限
     */
    @NestedConfigurationProperty
    private Y9PermissionProperties permission = new Y9PermissionProperties();

    /**
     * 消息发布
     */
    @NestedConfigurationProperty
    private Y9PublishProperties publish = new Y9PublishProperties();

    /**
     * 消息监听
     */
    @NestedConfigurationProperty
    private Y9ListenerProperties listener = new Y9ListenerProperties();

    /**
     * quartz定时任务
     */
    @NestedConfigurationProperty
    private Y9QuartzProperties quartz = new Y9QuartzProperties();

    /**
     * 会话
     */
    @NestedConfigurationProperty
    private Y9sessionProperties session = new Y9sessionProperties();

    /**
     * 短信
     */
    @NestedConfigurationProperty
    private Y9SmsProperties sms = new Y9SmsProperties();

    /**
     * sso
     */
    @NestedConfigurationProperty
    private Y9SsoClientProperties sso = new Y9SsoClientProperties();

    /**
     * 授权
     */
    @NestedConfigurationProperty
    private Y9LicenseProperties license = new Y9LicenseProperties();

    /**
     * 安全配置
     */
    @NestedConfigurationProperty
    private Y9SecurityProperties security = new Y9SecurityProperties();

    /**
     * oauth2
     */
    @NestedConfigurationProperty
    private Y9Oauth2Properties oauth2 = new Y9Oauth2Properties();

    /**
     * jwt
     */
    @NestedConfigurationProperty
    private Y9JwtProperties jwt = new Y9JwtProperties();

}
