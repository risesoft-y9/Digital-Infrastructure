package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据库方言
 * 
 * @author qinman
 * @date 2022/12/20
 */
@Getter
@AllArgsConstructor
public enum DialectEnum {
    /** mysql */
    MYSQL("mysql"),
    /** oracle */
    ORACLE("oracle"),
    /** mssql */
    MSSQL("mssql"),
    /** 达梦 */
    DM("dm"),
    /** 人大金仓 */
    KINGBASE("kingbase");

    private final String value;
}
