package net.risesoft.model.log;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 保存访问日志 dto
 *
 * @author shidaobang
 * @date 2026/08/13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccessLogDTO implements Serializable {

    /**
     * 租户id
     */
    @NotBlank
    private String tenantId;

    /**
     * 人员id
     */
    @NotBlank
    private String personId;

    /**
     * 系统名称
     */
    @NotBlank
    private String systemName;

    /**
     * 模块名称，比如：公文就转-发文-授权管理
     */
    @NotBlank
    private String modularName;

    /**
     * 方法类和名称
     */
    private String methodName;

    /**
     * 参数
     */
    private String paramsJson;

    /**
     * 日志记录时间
     */
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date logTime;

    /**
     * 日志级别： 普通日志、管理日志、错误日志、警告日志、信息日志、调试日志、跟踪日志
     */
    private String logLevel;

    /**
     * 操作类别： 查看，增加，修改，删除，发送，活动，登录，退出，检查，导出，导入，下载，上传 ……
     */
    private String operateType;

    /**
     * 操作名称
     */
    @NotBlank
    private String operateName;

    /**
     * 用时（毫秒）
     */
    @NotNull
    private Long elapsedTime;

    /**
     * 服务器ip
     */
    private String serverIp;

    /**
     * 操作状态：成功、出错
     */
    @NotBlank
    private String success;

    /**
     * 访问路径
     */
    private String requestUrl;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 日志信息
     */
    private String logMessage;

    /**
     * 异常信息
     */
    private String throwable;

    /**
     * 用户ip
     */
    @NotBlank
    private String userHostIp;

    /**
     * 浏览器信息
     */
    private String userAgent;

    /**
     * mac地址
     */
    private String macAddress;

}
