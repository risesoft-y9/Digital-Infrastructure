package net.risesoft.y9.sqlddl;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 */
public class DdlMssql {
    private static Logger log = LoggerFactory.getLogger(DdlMssql.class);

    private DdlMssql() {
        throw new IllegalStateException("DdlMssql Utility class");
    }

    public static void addTableColumn(DataSource dataSource, String tableName, String jsonDbColumns) throws Exception {
        log.info("addTableColumn");
    }

    public static void alterTableColumn(DataSource dataSource, String tableName, String jsonDbColumns)
        throws Exception {
        log.info("alterTableColumn");
    }

    public static void dropTable(DataSource dataSource, String tableName) throws Exception {
        log.info("dropTable");
    }

    public static void dropTableColumn(DataSource dataSource, String tableName, String columnName) throws Exception {
        log.info("dropTableColumn");
    }

    public static void renameTable(DataSource dataSource, String tableNameOld, String tableNameNew) throws Exception {
        log.info("renameTable");
    }
}
