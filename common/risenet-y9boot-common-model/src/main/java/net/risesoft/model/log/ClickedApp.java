package net.risesoft.model.log;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 应用点击详情
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClickedApp implements Serializable {

    private static final long serialVersionUID = 7190005083758488330L;

    /**
     * id
     */
    private String id;

    /** 人员id */
    private String personId;

    /** 租户id */
    private String tenantId;

    /** 应用id */
    private String appId;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 保存时间
     */

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date saveDate;

}
