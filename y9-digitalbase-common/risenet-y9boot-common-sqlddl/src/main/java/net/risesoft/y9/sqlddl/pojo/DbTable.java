package net.risesoft.y9.sqlddl.pojo;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 *
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 */
@Getter
@Setter
@EqualsAndHashCode
public class DbTable implements Serializable {
    private static final long serialVersionUID = 6672185772405591089L;

    private String catalog;
    private String schema;

    private String name;
    private String type;
    private String remarks;

}
