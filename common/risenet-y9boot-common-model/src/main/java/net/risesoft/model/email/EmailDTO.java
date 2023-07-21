package net.risesoft.model.email;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * 邮件
 *
 * @author shidaobang
 */
@Data
@JsonIgnoreProperties(value = {"updateTime", "sendTime"})
public class EmailDTO implements Serializable {

    public enum Type {
        /** 正常 */
        NORMAL(1),
        /** 转发 */
        REPLY(2),
        /** 回复 */
        FORWARD(3);

        Integer value;

        Type() {

        }

        Type(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }
    }

    private static final long serialVersionUID = 3094599254952039777L;

    /**
     * 主键id Y9IdGenerator.genId(IdType.SNOWFLAKE)
     */
    private String id;

    /**
     * 创建时间
     *
     * @ignore
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     *
     * @ignore
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 发送时间
     *
     * @ignore
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sendTime;

    /**
     * 创建人id
     *
     * @ignore
     */
    private String personId;

    /**
     * 创建人姓名
     *
     * @ignore
     */
    private String personName;

    /**
     * 邮件主题
     */
    private String subject;

    /**
     * 邮件纯文本正文
     */
    private String text;

    /**
     * 邮件富文本正文
     */
    private String richText;

    /**
     * 是否为任务邮件
     */
    private Boolean task;

    /**
     * 是否短信通知
     */
    private Boolean sms;

    /**
     * 短信内容
     */
    private String smsContent;

    /**
     * 是否为分别发送
     */
    private Boolean separated;

    /**
     * 是否有附件
     *
     * @ignore
     */
    private Boolean attachment;

    /**
     * 邮件类型: 1.正常 2.转发 3.回复
     *
     * @ignore
     */
    private Integer type;
    
}
