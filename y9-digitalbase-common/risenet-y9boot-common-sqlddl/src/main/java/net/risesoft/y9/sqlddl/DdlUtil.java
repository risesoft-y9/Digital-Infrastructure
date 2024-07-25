package net.risesoft.y9.sqlddl;

import javax.sql.DataSource;

import net.risesoft.consts.SqlConstants;

/**
 *
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 */
public class DdlUtil {

    private DdlUtil() {
        throw new IllegalStateException("Ddl Utility class");
    }

    public static void addTableColumn(DataSource dataSource, String tableName, String jsonDbColumns) throws Exception {
        String dbType = DbMetaDataUtil.getDatabaseDialectName(dataSource);
        if (SqlConstants.DBTYPE_MYSQL.equalsIgnoreCase(dbType)) {
            DdlMysql.addTableColumn(dataSource, tableName, jsonDbColumns);
        } else if (SqlConstants.DBTYPE_ORACLE.equalsIgnoreCase(dbType)) {
            DdlOracle.addTableColumn(dataSource, tableName, jsonDbColumns);
        } else if (SqlConstants.DBTYPE_MSSQL.equalsIgnoreCase(dbType)) {
            DdlMssql.addTableColumn(dataSource, tableName, jsonDbColumns);
        } else if (SqlConstants.DBTYPE_DM.equalsIgnoreCase(dbType)) {
            DdlDm.addTableColumn(dataSource, tableName, jsonDbColumns);
        }
    }

    public static void alterTableColumn(DataSource dataSource, String tableName, String jsonDbColumns)
        throws Exception {
        String dbType = DbMetaDataUtil.getDatabaseDialectName(dataSource);
        if (SqlConstants.DBTYPE_MYSQL.equalsIgnoreCase(dbType)) {
            DdlMysql.alterTableColumn(dataSource, tableName, jsonDbColumns);
        } else if (SqlConstants.DBTYPE_ORACLE.equalsIgnoreCase(dbType)) {
            DdlOracle.alterTableColumn(dataSource, tableName, jsonDbColumns);
        } else if (SqlConstants.DBTYPE_MSSQL.equalsIgnoreCase(dbType)) {
            DdlMssql.alterTableColumn(dataSource, tableName, jsonDbColumns);
        } else if (SqlConstants.DBTYPE_DM.equalsIgnoreCase(dbType)) {
            DdlDm.alterTableColumn(dataSource, tableName, jsonDbColumns);
        }
    }

    public static void dropTable(DataSource dataSource, String tableName) throws Exception {
        String dbType = DbMetaDataUtil.getDatabaseDialectName(dataSource);
        if (SqlConstants.DBTYPE_MYSQL.equalsIgnoreCase(dbType)) {
            DdlMysql.dropTable(dataSource, tableName);
        } else if (SqlConstants.DBTYPE_ORACLE.equalsIgnoreCase(dbType)) {
            DdlOracle.dropTable(dataSource, tableName);
        } else if (SqlConstants.DBTYPE_MSSQL.equalsIgnoreCase(dbType)) {
            DdlMssql.dropTable(dataSource, tableName);
        } else if (SqlConstants.DBTYPE_DM.equalsIgnoreCase(dbType)) {
            DdlDm.dropTable(dataSource, tableName);
        }
    }

    public static void dropTableColumn(DataSource dataSource, String tableName, String columnName) throws Exception {
        String dbType = DbMetaDataUtil.getDatabaseDialectName(dataSource);
        if (SqlConstants.DBTYPE_MYSQL.equalsIgnoreCase(dbType)) {
            DdlMysql.dropTableColumn(dataSource, tableName, columnName);
        } else if (SqlConstants.DBTYPE_ORACLE.equalsIgnoreCase(dbType)) {
            DdlOracle.dropTableColumn(dataSource, tableName, columnName);
        } else if (SqlConstants.DBTYPE_MSSQL.equalsIgnoreCase(dbType)) {
            DdlMssql.dropTableColumn(dataSource, tableName, columnName);
        } else if (SqlConstants.DBTYPE_DM.equalsIgnoreCase(dbType)) {
            DdlDm.dropTableColumn(dataSource, tableName, columnName);
        }
    }

    public static void renameTable(DataSource dataSource, String tableNameOld, String tableNameNew) throws Exception {
        String dbType = DbMetaDataUtil.getDatabaseDialectName(dataSource);
        if (SqlConstants.DBTYPE_MYSQL.equalsIgnoreCase(dbType)) {
            DdlMysql.renameTable(dataSource, tableNameOld, tableNameNew);
        } else if (SqlConstants.DBTYPE_ORACLE.equalsIgnoreCase(dbType)) {
            DdlOracle.renameTable(dataSource, tableNameOld, tableNameNew);
        } else if (SqlConstants.DBTYPE_MSSQL.equalsIgnoreCase(dbType)) {
            DdlMssql.renameTable(dataSource, tableNameOld, tableNameNew);
        } else if (SqlConstants.DBTYPE_DM.equalsIgnoreCase(dbType)) {
            DdlDm.renameTable(dataSource, tableNameOld, tableNameNew);
        }
    }

}
