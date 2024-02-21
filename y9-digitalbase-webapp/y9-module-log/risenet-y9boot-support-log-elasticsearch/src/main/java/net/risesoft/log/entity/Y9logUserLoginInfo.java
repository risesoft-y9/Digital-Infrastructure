package net.risesoft.log.entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.enums.platform.ManagerLevelEnum;

@NoArgsConstructor
@Data
@Document(indexName = "userlogininfo")
public class Y9logUserLoginInfo implements Serializable {
    private static final long serialVersionUID = -6476891120156676097L;

    @Id
    private String id;

    @Field(type = FieldType.Date, index = true, store = true)
    private Date loginTime;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String loginType;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String userId;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String userName;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String userLoginName;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String userHostIp;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String userHostMac;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String userHostName;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String userHostDiskId;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String tenantId;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String tenantName;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String serverIp;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String success = "true";

    @Field(type = FieldType.Text, index = true, store = true)
    private String logMessage;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String browserName;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String browserVersion;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String osName;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String screenResolution;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String clientIpSection;

    /**
     * 三员级别 {@link ManagerLevelEnum}
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String managerLevel;
}
