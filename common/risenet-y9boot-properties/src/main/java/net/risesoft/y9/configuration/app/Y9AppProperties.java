package net.risesoft.y9.configuration.app;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.y9.configuration.app.y9addressbook.Y9AddressBookProperties;
import net.risesoft.y9.configuration.app.y9cms.Y9CmsProperties;
import net.risesoft.y9.configuration.app.y9datacenter.Y9DatacenterProperties;
import net.risesoft.y9.configuration.app.y9digitalbase.Y9DigitalBaseProperties;
import net.risesoft.y9.configuration.app.y9email.Y9EmailProperties;
import net.risesoft.y9.configuration.app.y9filemanager.Y9FileManagerProperties;
import net.risesoft.y9.configuration.app.y9flowable.Y9FlowableProperties;
import net.risesoft.y9.configuration.app.y9home.Y9HomeProperties;
import net.risesoft.y9.configuration.app.y9im.Y9ImProperties;
import net.risesoft.y9.configuration.app.y9itemadmin.Y9ItemAdminProperties;
import net.risesoft.y9.configuration.app.y9log.Y9LogProperties;
import net.risesoft.y9.configuration.app.y9platform.Y9PlatformProperties;
import net.risesoft.y9.configuration.app.y9processadmin.y9ProcessAdminProperties;
import net.risesoft.y9.configuration.app.y9risecloud.Y9RisecloudProperties;
import net.risesoft.y9.configuration.app.y9scalendar.Y9sCalendarProperties;
import net.risesoft.y9.configuration.app.y9sms.Y9SmsProperties;
import net.risesoft.y9.configuration.app.y9soa.Y9SoaProperties;
import net.risesoft.y9.configuration.app.y9sso.Y9SsoServerProperties;
import net.risesoft.y9.configuration.app.y9subscription.Y9SubscriptionProperties;
import net.risesoft.y9.configuration.app.y9todo.Y9TodoProperties;
import net.risesoft.y9.configuration.app.y9useronline.Y9UserOnlineProperties;
import net.risesoft.y9.configuration.app.y9webmail.Y9WebmailProperties;

@Getter
@Setter
public class Y9AppProperties {

    @NestedConfigurationProperty
    private Y9DigitalBaseProperties y9DigitalBase = new Y9DigitalBaseProperties();

    @NestedConfigurationProperty
    private Y9CmsProperties cms = new Y9CmsProperties();

    @NestedConfigurationProperty
    private Y9ImProperties im = new Y9ImProperties();

    @NestedConfigurationProperty
    private Y9LogProperties log = new Y9LogProperties();

    @NestedConfigurationProperty
    private Y9PlatformProperties platform = new Y9PlatformProperties();

    @NestedConfigurationProperty
    private Y9SoaProperties soa = new Y9SoaProperties();

    @NestedConfigurationProperty
    private Y9SsoServerProperties sso = new Y9SsoServerProperties();

    @NestedConfigurationProperty
    private Y9TodoProperties todo = new Y9TodoProperties();

    @NestedConfigurationProperty
    private y9ProcessAdminProperties processAdmin = new y9ProcessAdminProperties();

    @NestedConfigurationProperty
    private Y9sCalendarProperties calendar = new Y9sCalendarProperties();

    @NestedConfigurationProperty
    private Y9UserOnlineProperties userOnline = new Y9UserOnlineProperties();

    @NestedConfigurationProperty
    private Y9RisecloudProperties risecloud = new Y9RisecloudProperties();

    @NestedConfigurationProperty
    private Y9ItemAdminProperties itemAdmin = new Y9ItemAdminProperties();

    @NestedConfigurationProperty
    private Y9FlowableProperties flowable = new Y9FlowableProperties();

    @NestedConfigurationProperty
    private Y9HomeProperties home = new Y9HomeProperties();

    @NestedConfigurationProperty
    private Y9SubscriptionProperties subscription = new Y9SubscriptionProperties();

    @NestedConfigurationProperty
    private Y9DatacenterProperties datacenter = new Y9DatacenterProperties();

    @NestedConfigurationProperty
    private Y9SmsProperties sms = new Y9SmsProperties();

    @NestedConfigurationProperty
    private Y9EmailProperties email = new Y9EmailProperties();

    @NestedConfigurationProperty
    private Y9FileManagerProperties fileManager = new Y9FileManagerProperties();

    @NestedConfigurationProperty
    private Y9AddressBookProperties addressBook = new Y9AddressBookProperties();

    @NestedConfigurationProperty
    private Y9WebmailProperties webmail = new Y9WebmailProperties();

}
