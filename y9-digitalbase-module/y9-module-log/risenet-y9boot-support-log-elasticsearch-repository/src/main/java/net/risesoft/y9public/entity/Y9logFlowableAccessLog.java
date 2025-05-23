package net.risesoft.y9public.entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 日志信息
 *
 * @author shidaobang
 * @author guoweijun
 * @author mengjuhua
 */
@NoArgsConstructor
@Data
@Document(indexName = "flowable_log_index-#{@esIndexDate.getDateStr()}")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Y9logFlowableAccessLog implements Serializable {

    private static final long serialVersionUID = 8085477796175696619L;
    /**
     * 主键
     */
    @Id
    private String id;

    /**
     * 流程序列号
     */
    @Field(type = FieldType.Keyword, store = true)
    private String processSerialNumber;

    /**
     * 操作类别： 0=使用，1=登录，2=退出，3=查看，4=增加，5=修改，6=删除，7=发送，8=活动
     */
    @Field(type = FieldType.Keyword, store = true)
    private String operateType;

    /**
     * 描述：主要是标题，表单数据等
     */
    @Field(type = FieldType.Text, store = true)
    private String description;

    @Field(type = FieldType.Keyword, store = true)
    private String userId;

    @Field(type = FieldType.Keyword, store = true)
    private String guidPath;

    @Field(type = FieldType.Keyword, store = true)
    private String userName;

    @Field(type = FieldType.Keyword, store = true)
    private String loginName;

    /**
     * 用户类型(涉密等级)
     */
    @Field(type = FieldType.Keyword, store = true)
    private String personType;

    @Field(type = FieldType.Keyword, store = true)
    private String dn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Field(type = FieldType.Date, store = true)
    private Date logTime;

    /**
     * 日志级别 0=普通用户 1=部门管理员 2=全局管理员
     */
    @Field(type = FieldType.Keyword, store = true)
    private String logLevel;

    /**
     * 操作名称
     */
    @Field(type = FieldType.Keyword, store = true)
    private String operateName;

    @Field(type = FieldType.Keyword, store = true)
    private String systemName;

    /**
     * 模块名称，比如：公文就转-发文-授权管理
     */
    @Field(type = FieldType.Keyword, store = true)
    private String modularName;

    /**
     * 方法类和名称
     */
    @Field(type = FieldType.Keyword, store = true)
    private String methodName;

    /**
     * 用时
     */
    @Field(type = FieldType.Long, store = true)
    private long elapsedTime;

    @Field(type = FieldType.Keyword, store = true)
    private String userHostIp;

    @Field(type = FieldType.Keyword, store = true)
    private String serverIp;

    @Field(type = FieldType.Keyword, store = true)
    private String macAddress;

    @Field(type = FieldType.Keyword, store = true)
    private String tenantId;

    @Field(type = FieldType.Keyword, store = true)
    private String tenantName;

    @Field(type = FieldType.Keyword, store = true)
    private String success;

    @Field(type = FieldType.Keyword, store = true)
    private String requestUrl;

    @Field(type = FieldType.Keyword, store = true)
    private String errorMessage;

    @Field(type = FieldType.Keyword, store = true)
    private String logMessage;

    @Field(type = FieldType.Keyword, store = true)
    private String throwable;

    @Field(type = FieldType.Keyword, store = true)
    private String userAgent;
}
