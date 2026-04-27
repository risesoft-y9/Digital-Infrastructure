package net.risesoft.y9.sqlddl;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

/**
 *
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 */
@Slf4j
public class DdlMssql {

    public static void addTableColumn(DataSource dataSource, String tableName, String jsonDbColumns) throws Exception {
        LOGGER.info("addTableColumn");
    }

    public static void alterTableColumn(DataSource dataSource, String tableName, String jsonDbColumns)
        throws Exception {
        LOGGER.info("alterTableColumn");
    }

    public static void dropTable(DataSource dataSource, String tableName) throws Exception {
        LOGGER.info("dropTable");
    }

    public static void dropTableColumn(DataSource dataSource, String tableName, String columnName) throws Exception {
        LOGGER.info("dropTableColumn");
    }

    public static void renameTable(DataSource dataSource, String tableNameOld, String tableNameNew) throws Exception {
        LOGGER.info("renameTable");
    }

    private DdlMssql() {
        throw new IllegalStateException("DdlMssql Utility class");
    }
}
