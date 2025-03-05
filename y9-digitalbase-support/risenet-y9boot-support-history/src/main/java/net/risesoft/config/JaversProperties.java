package net.risesoft.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * jaVers 自定义的属性
 *
 * @author shidaobang
 * @date 2025/03/05
 */
@ConfigurationProperties(prefix = "javers")
@Getter
@Setter
public class JaversProperties {

    private boolean enabled = true;

    private Dialect dialect = Dialect.AUTO;

    public enum Dialect {
        AUTO, H2, POSTGRES, ORACLE, MYSQL, MSSQL;
    }
}
