package net.risesoft.model.email;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SmtpServerProperty implements Serializable {

    private static final long serialVersionUID = -5848000720571784296L;

    /**
     * smtp服务器
     */
    private String host;
    /**
     * 用户名(一般也为邮箱)
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 是否使用 SSL
     */
    private boolean ssl = false;
    /**
     * SSL 端口
     */
    private int port = -1;
}
