package net.risesoft.y9public.entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.enums.platform.ManagerLevelEnum;

/**
 * 日志信息
 *
 * @author shidaobang
 * @author guoweijun
 * @author mengjuhua
 *
 */
@NoArgsConstructor
@Data
@Document(indexName = "logindex-#{@esIndexDate.getDateStr()}")
public class Y9logAccessLog implements Serializable {
    private static final long serialVersionUID = 8905896381019503361L;

    /** 主键 */
    @Id
    private String id;

    /**
     * https://docs.spring.io/spring-data/elasticsearch/docs/4.0.1.RELEASE/reference/html/#elasticsearch.mapping format
     * and pattern definitions for the Date type. format must be defined for date types
     */
    @Field(type = FieldType.Date, index = true, store = true)
    private Date logTime;

    /** 日志级别 0=TRACE 1=DEBUG 2=INFO 3=WARN 4=ERROR */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String logLevel;

    /** 操作类别： 0=使用，1=登录，2=退出，3=查看，4=增加，5=修改，6=删除，7=发送，8=活动 */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String operateType;

    /** 操作名称 */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String operateName;

    /** 模块名称，比如：公文就转-发文-授权管理 */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String modularName;

    /** 方法类和名称 */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String methodName;

    /** 用时 */
    @Field(type = FieldType.Long, index = true, store = true)
    private long elapsedTime;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String userId;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String userName;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String userHostIp;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String tenantId;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String tenantName;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String serverIp;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String success;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String requestUrl;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String errorMessage;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String logMessage;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String throwable;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String dn;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String guidPath;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String systemName;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String userAgent;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String macAddress;

    @Field(type = FieldType.Keyword, index = true, store = true)
    private String loginName;

    /**
     * 三员级别 {@link ManagerLevelEnum}
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String managerLevel;
}
