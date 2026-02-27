package net.risesoft.y9.sqlddl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.RowSetDynaClass;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.SqlConstants;
import net.risesoft.y9.sqlddl.pojo.DbColumn;

/**
 * 数据库相关操作工具类
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @author zhangchongjie
 */
@Slf4j
@NoArgsConstructor
public class DbMetaDataUtil {

    /**
     * 批量执行sql语句
     *
     * @param dataSource 数据源
     * @param sqlList sql语句列表
     * @return int[] 执行
     * @throws SQLException sql异常信息
     */
    public static int[] batchExecuteDdl(DataSource dataSource, List<String> sqlList) throws SQLException {
        java.sql.Statement stmt = null;
        String dialectName = getDatabaseDialectName(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            stmt = connection.createStatement();
            if (SqlConstants.DBTYPE_MYSQL.equals(dialectName)) {
                stmt.addBatch("SET FOREIGN_KEY_CHECKS=0");
            }
            for (String sql : sqlList) {
                stmt.addBatch(sql);
            }
            if (SqlConstants.DBTYPE_MYSQL.equals(dialectName)) {
                stmt.addBatch("SET FOREIGN_KEY_CHECKS=1");
            }
            return stmt.executeBatch();
        } catch (SQLException e) {
            LOGGER.error("执行SQL语句失败！", e);
            throw e;
        } finally {
            if (null != stmt && SqlConstants.DBTYPE_MYSQL.equals(dialectName) && !stmt.isClosed()) {
                stmt.execute("SET FOREIGN_KEY_CHECKS=1");
            }
            try {
                if (null != stmt) {
                    stmt.close();
                }
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
    }

    /**
     * KINGBASE批量执行SQL
     *
     * @param dataSource 数据源
     * @param sqlList sql语句列表
     * @return int[] 执行
     * @throws SQLException sql异常信息
     */
    public static int[] batchExecuteDdl4Kingbase(DataSource dataSource, List<String> sqlList) throws SQLException {
        java.sql.Statement stmt = null;
        try (Connection connection = dataSource.getConnection()) {

            stmt = connection.createStatement();
            for (String sql : sqlList) {
                stmt.addBatch(sql);
            }
            return stmt.executeBatch();
        } catch (SQLException e) {
            LOGGER.error("KINGBASE执行SQL语句失败！", e);
            throw e;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    /**
     * 验证表是否存在
     *
     * @param dataSource 数据源
     * @param tableName 表名称
     * @return boolean 表是否存在
     * @throws Exception 异常信息
     */
    public static boolean checkTableExist(DataSource dataSource, String tableName) throws Exception {
        String databaseName;
        String tableSchema;
        ResultSet rs = null;
        try (Connection connection = dataSource.getConnection()) {
            databaseName = connection.getCatalog();
            DatabaseMetaData dbmd = connection.getMetaData();
            String dialect = getDatabaseDialectNameByConnection(connection);
            switch (dialect) {
                case SqlConstants.DBTYPE_MYSQL:
                    rs = dbmd.getTables(null, databaseName, tableName, new String[] {"TABLE"});
                    break;
                case SqlConstants.DBTYPE_MSSQL:
                    rs = dbmd.getTables(databaseName, null, tableName, new String[] {"TABLE"});
                    break;
                case SqlConstants.DBTYPE_DM:
                case SqlConstants.DBTYPE_ORACLE:
                    tableSchema = dbmd.getUserName().toUpperCase();
                    rs = dbmd.getTables(null, tableSchema, tableName, new String[] {"TABLE"});
                    break;
                case SqlConstants.DBTYPE_KINGBASE:
                    tableSchema = connection.getSchema();
                    rs = dbmd.getTables(null, tableSchema, tableName, new String[] {"TABLE"});
                    break;
                default:
                    rs = dbmd.getTables(null, databaseName, tableName, new String[] {"TABLE"});
            }
            return rs != null && rs.next();
        } catch (Exception e) {
            LOGGER.error("验证表是否存在失败！", e);
            throw e;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    /**
     * 执行单条SQL语句
     *
     * @param dataSource 数据源
     * @param sql any SQL statement
     * @return Boolean 判断结果
     * @throws SQLException sql异常信息
     */
    public static Boolean executeDdl(DataSource dataSource, String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection(); Statement stmt = connection.createStatement()) {
            return stmt.execute(sql);
        } catch (SQLException e) {
            LOGGER.error("执行单条SQL语句失败！", e);
            throw e;
        }
    }

    /**
     * 获得表的所有列的属性
     *
     * @param dialect 方言
     * @param dbmd DatabaseMetaData实例
     * @param databaseName 数据源名称
     * @param tableName 表名称
     * @param tableSchema 数据库的名称
     * @param columnNamePatten 列名模式
     * @return ResultSet 结果集
     * @throws SQLException sql异常信息
     */
    private static ResultSet getColumns(String dialect, DatabaseMetaData dbmd, String databaseName, String tableName,
        String tableSchema, String columnNamePatten) throws SQLException {
        ResultSet rs = null;
        // 获得所有列的属性
        switch (dialect) {
            case SqlConstants.DBTYPE_MYSQL:
                rs = dbmd.getColumns(null, databaseName, tableName, columnNamePatten);
                break;
            case SqlConstants.DBTYPE_MSSQL:
                rs = dbmd.getColumns(databaseName, null, tableName, columnNamePatten);
                break;
            case SqlConstants.DBTYPE_ORACLE:
                rs = dbmd.getColumns(null, tableSchema, tableName, columnNamePatten);
                break;
            case SqlConstants.DBTYPE_DM:
                rs = dbmd.getColumns(null, tableSchema, tableName, columnNamePatten);
                break;
            case SqlConstants.DBTYPE_KINGBASE:
                rs = dbmd.getColumns(null, tableSchema, tableName, columnNamePatten);
                break;
            default:
                rs = dbmd.getColumns(null, databaseName, tableName, columnNamePatten);
                break;
        }
        return rs;
    }

    /**
     * 获取数据库方言
     * 
     * @param dataSource 数据源
     * @return String 数据库方言
     */
    public static String getDatabaseDialectName(DataSource dataSource) {
        String databaseName = "";
        try {
            databaseName = getDatabaseProductName(dataSource).toLowerCase();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }

        if (databaseName.indexOf(SqlConstants.DBTYPE_MYSQL) > -1) {
            return "mysql";
        } else if (databaseName.indexOf(SqlConstants.DBTYPE_ORACLE) > -1) {
            return "oracle";
        } else if (databaseName.indexOf(SqlConstants.DBTYPE_DM) > -1) {
            return "dm";
        } else if (databaseName.indexOf(SqlConstants.DBTYPE_MICROSOFT) > -1) {
            return "mssql";
        } else if (databaseName.indexOf(SqlConstants.DBTYPE_KINGBASE) > -1) {
            return "kingbase";
        }
        return "";
    }

    /**
     * 获取数据库方言
     * 
     * @param connection 数据库的连接
     * @return String 数据库方言
     */
    public static String getDatabaseDialectNameByConnection(Connection connection) {
        String databaseName = "";
        try {
            databaseName = getDatabaseProductNameByConnection(connection).toLowerCase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (databaseName.indexOf(SqlConstants.DBTYPE_MYSQL) > -1) {
            return "mysql";
        } else if (databaseName.indexOf(SqlConstants.DBTYPE_ORACLE) > -1) {
            return "oracle";
        } else if (databaseName.indexOf(SqlConstants.DBTYPE_DM) > -1) {
            return "dm";
        } else if (databaseName.indexOf(SqlConstants.DBTYPE_MICROSOFT) > -1) {
            return "mssql";
        } else if (databaseName.indexOf(SqlConstants.DBTYPE_KINGBASE) > -1) {
            return "kingbase";
        }

        return "";
    }

    public static int getDatabaseMajorVersion(DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData dbmd = connection.getMetaData();
            return dbmd.getDatabaseMajorVersion();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }

    public static int getDatabaseMinorVersion(DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData dbmd = connection.getMetaData();
            return dbmd.getDatabaseMinorVersion();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }

    public static String getDatabaseProductName(DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData dbmd = connection.getMetaData();
            return dbmd.getDatabaseProductName();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }

    public static String getDatabaseProductNameByConnection(Connection connection) throws SQLException {
        try {
            DatabaseMetaData dbmd = connection.getMetaData();
            return dbmd.getDatabaseProductName();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }

    public static String getDatabaseProductVersion(DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData dbmd = connection.getMetaData();
            return dbmd.getDatabaseProductVersion();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }

    /**
     * 获取主键
     *
     * @param dialect 方言
     * @param dbmd DatabaseMetaData实例
     * @param databaseName 数据源名称
     * @param tableName 表名称
     * @param tableSchema 数据库的名称
     * @return ResultSet 结果集
     * @throws SQLException sql异常信息
     */
    private static ResultSet getPrimaryKeys(String dialect, DatabaseMetaData dbmd, String databaseName,
        String tableName, String tableSchema) throws SQLException {
        ResultSet rs = null;
        // 获得主键
        switch (dialect) {
            case SqlConstants.DBTYPE_MYSQL:
                rs = dbmd.getPrimaryKeys(null, databaseName, tableName);
                break;
            case SqlConstants.DBTYPE_MSSQL:
                rs = dbmd.getPrimaryKeys(databaseName, null, tableName);
                break;
            case SqlConstants.DBTYPE_ORACLE:
                rs = dbmd.getPrimaryKeys(null, tableSchema, tableName);
                break;
            case SqlConstants.DBTYPE_DM:
                rs = dbmd.getPrimaryKeys(null, tableSchema, tableName);
                break;
            case SqlConstants.DBTYPE_KINGBASE:
                rs = dbmd.getPrimaryKeys(null, tableSchema, tableName);
                break;
            default:
                rs = dbmd.getPrimaryKeys(null, databaseName, tableName);
                break;
        }
        return rs;
    }

    @SuppressWarnings({"resource"})
    public static List<DbColumn> listAllColumns(DataSource dataSource, String tableName, String columnNamePatten)
        throws Exception {
        String tableSchema = null;
        String databaseName = null;

        Statement stmt = null;
        ResultSet rs = null;
        List<DbColumn> dbColumnList = new ArrayList<>();
        if (checkTableExist(dataSource, tableName)) {
            try (Connection connection = dataSource.getConnection()) {
                databaseName = connection.getCatalog();
                DatabaseMetaData dbmd = connection.getMetaData();
                tableSchema = dbmd.getUserName().toUpperCase();
                String dialect = getDatabaseDialectNameByConnection(connection);
                rs = getPrimaryKeys(dialect, dbmd, databaseName, tableName, tableSchema);
                List<String> pkList = new ArrayList<>();
                while (rs.next()) {
                    pkList.add(rs.getString("COLUMN_NAME"));
                }
                if (pkList.isEmpty()) {
                    LOGGER.error("***********没有主键？*************");
                }
                rs = getColumns(dialect, dbmd, databaseName, tableName, tableSchema, columnNamePatten);
                while (rs.next()) {
                    DbColumn dbColumn = new DbColumn();

                    dbColumn.setTableName(rs.getString("table_name").toUpperCase());

                    String columnName = rs.getString("column_name".toLowerCase());
                    dbColumn.setColumnName(columnName);
                    dbColumn.setColumnNameOld(columnName);

                    dbColumn.setPrimaryKey(pkList.contains(columnName));
                    String remarks = rs.getString("remarks");
                    if (StringUtils.isBlank(remarks)) {
                        dbColumn.setComment(columnName.toUpperCase());
                    } else {
                        dbColumn.setComment(remarks);
                    }
                    int columnSize = rs.getInt("column_size");
                    dbColumn.setDataLength(columnSize);
                    int dataType = rs.getInt("data_type");
                    dbColumn.setDataType(dataType);
                    dbColumn.setTypeName(rs.getString("type_name").toLowerCase());
                    int decimalDigits = rs.getInt("decimal_digits");
                    dbColumn.setDataScale(decimalDigits);
                    String nullable = rs.getString("is_nullable");
                    Boolean bNullable = false;
                    if ("yes".equalsIgnoreCase(nullable)) {
                        bNullable = true;
                    }
                    dbColumn.setNullable(bNullable);
                    boolean exist = false;
                    for (DbColumn field : dbColumnList) {
                        if (field.getColumnName().equalsIgnoreCase(columnName)) {
                            exist = true;
                            break;
                        }
                    }
                    if (!exist) {
                        dbColumnList.add(dbColumn);
                    }
                }

                /***********
                 * RowSetDynaClass rsdc2 = new RowSetDynaClass(rs, true); List columnList = rsdc2.getRows(); for (int i
                 * = 0; i < columnList.size(); i++) { BasicDynaBean bdb = (BasicDynaBean) columnList.get(i); DbColumn
                 * dbColumn = new DbColumn();
                 *
                 * dbColumn.setTable_name((String) bdb.get("table_name"));
                 *
                 * String columnName = (String) bdb.get("column_name"); dbColumn.setColumn_name(columnName);
                 * dbColumn.setColumn_name_old(columnName);
                 *
                 * if (pkList.contains(columnName)) { dbColumn.setPrimaryKey(true); } else {
                 * dbColumn.setPrimaryKey(false); }
                 *
                 * String remarks = (String) bdb.get("remarks"); if (StringUtils.isBlank(remarks)) {
                 * dbColumn.setComment(columnName.toUpperCase()); } else { dbColumn.setComment(remarks); }
                 *
                 * Object columnSize = bdb.get("column_size"); if (columnSize != null) { int cs = 10; if (columnSize
                 * instanceof BigDecimal) { cs = ((BigDecimal) columnSize).intValue(); } else { cs = ((Integer)
                 * columnSize).intValue(); } dbColumn.setData_length(cs); dbColumn.setData_precision(cs); } else {
                 * dbColumn.setData_length(10); }
                 *
                 * Object dtObj = bdb.get("data_type"); int dt = 3; if (dtObj instanceof BigDecimal) { dt =
                 * ((BigDecimal) dtObj).intValue(); } else { dt = ((Integer) dtObj).intValue(); }
                 * dbColumn.setData_type(dt); dbColumn.setType_name(((String) bdb.get("type_name")).toLowerCase());
                 *
                 * Object ddObj = bdb.get("decimal_digits"); if (ddObj != null) { int dd = 3; if (ddObj instanceof
                 * BigDecimal) { dd = ((BigDecimal) ddObj).intValue(); } else { dd = ((Integer) ddObj).intValue(); }
                 * dbColumn.setData_scale(dd);
                 *
                 * // 在oracle中，整型和浮点型都是一致的 if (dd == 0 && (dt == Types.FLOAT || dt == Types.REAL || dt == Types.DOUBLE
                 * || dt == Types.NUMERIC || dt == Types.DECIMAL)) { dbColumn.setData_type(Types.INTEGER); } }
                 *
                 * String nullable = (String) bdb.get("is_nullable"); Boolean bNullable = false; if
                 * ("yes".equalsIgnoreCase(nullable)) { bNullable = true; } dbColumn.setNullable(bNullable);
                 *
                 * boolean exist = false; for (DbColumn field : dbColumnList) { if
                 * (field.getColumn_name().equalsIgnoreCase(dbColumn.getColumn_name())) { exist = true; break; } } if
                 * (!exist) { dbColumnList.add(dbColumn); } }
                 **************/
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                throw e;
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            }
        }
        return dbColumnList;
    }

    public static List<DynaBean> listAllExportedKeys(DataSource dataSource, String tableName) throws Exception {
        ResultSet rs = null;
        List<DynaBean> rList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData dbmd = connection.getMetaData();
            rs = dbmd.getExportedKeys(null, null, tableName);
            RowSetDynaClass rsdc = new RowSetDynaClass(rs, true);
            rs.close();

            rList.addAll(rsdc.getRows());
            return rList;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw e;
        } finally {
            try {
                if (null != rs) {
                    rs.close();
                }
            } catch (Exception e2) {
                LOGGER.warn(e2.getMessage(), e2);
            }
        }
    }

    public static List<DynaBean> listAllImportedKeys(DataSource dataSource, String tableName) throws Exception {
        ResultSet rs = null;
        List<DynaBean> rList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData dbmd = connection.getMetaData();
            rs = dbmd.getImportedKeys(null, null, tableName);
            RowSetDynaClass rsdc = new RowSetDynaClass(rs, true);
            rs.close();

            rList.addAll(rsdc.getRows());

            return rList;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw e;
        } finally {
            if (null != rs) {
                rs.close();
            }
        }
    }

    public static List<DynaBean> listAllIndexs(DataSource dataSource, String tableName) throws Exception {
        ResultSet rs = null;
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData dbmd = connection.getMetaData();
            rs = dbmd.getIndexInfo(null, null, tableName, false, false);
            RowSetDynaClass rsdc = new RowSetDynaClass(rs, true);
            return rsdc.getRows();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw e;
        } finally {
            if (null != rs) {
                rs.close();
            }
        }
    }

    public static List<DynaBean> listAllRelations(DataSource dataSource, String tableName) throws Exception {
        ResultSet rs = null;
        List<DynaBean> rList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData dbmd = connection.getMetaData();
            rs = dbmd.getImportedKeys(null, null, tableName);
            RowSetDynaClass rsdc = new RowSetDynaClass(rs, true);
            rs.close();

            rs = dbmd.getExportedKeys(null, null, tableName);
            RowSetDynaClass rsdc2 = new RowSetDynaClass(rs, true);
            rs.close();

            rList.addAll(rsdc.getRows());
            rList.addAll(rsdc2.getRows());

            return rList;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw e;
        } finally {
            if (null != rs) {
                rs.close();
            }
        }
    }

    public static List<Map<String, String>> listAllTables(DataSource dataSource, String tableNamePattern)
        throws Exception {
        List<Map<String, String>> list = new ArrayList<>();
        ResultSet rs = null;
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData dbmd = connection.getMetaData();
            String username = dbmd.getUserName().toUpperCase();
            String dialect = getDatabaseDialectNameByConnection(connection);
            switch (dialect) {
                case SqlConstants.DBTYPE_MYSQL:
                    rs = dbmd.getTables(null, connection.getCatalog(), tableNamePattern, new String[] {"TABLE"});
                    break;
                case SqlConstants.DBTYPE_MSSQL:
                    rs = dbmd.getTables(connection.getCatalog(), null, tableNamePattern, new String[] {"TABLE"});
                    break;
                case SqlConstants.DBTYPE_ORACLE:
                    rs = dbmd.getTables(null, username, tableNamePattern, new String[] {"TABLE"});
                    break;
                case SqlConstants.DBTYPE_DM:
                    rs = dbmd.getTables(null, username, tableNamePattern, new String[] {"TABLE"});
                    break;
                case SqlConstants.DBTYPE_KINGBASE:
                    rs = dbmd.getTables(null, username, tableNamePattern, new String[] {"TABLE"});
                    break;
                default:
                    rs = dbmd.getTables(null, connection.getCatalog(), tableNamePattern, new String[] {"TABLE"});
                    break;
            }

            while (rs.next()) {
                if (!rs.getString("TABLE_NAME").contains("$")) {
                    HashMap<String, String> map = new HashMap<>(16);
                    map.put("catalog", rs.getString("TABLE_CAT"));
                    map.put("schema", rs.getString("TABLE_SCHEM"));
                    map.put("name", rs.getString("TABLE_NAME"));
                    list.add(map);
                }
            }
            return list;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw e;
        } finally {
            if (null != rs) {
                rs.close();
            }
        }
    }

    public static List<DynaBean> listAllTables(DataSource dataSource, String catalog, String schemaPattern,
        String tableNamePattern, String[] types) throws Exception {
        ResultSet rs = null;
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData dbmd = connection.getMetaData();
            rs = dbmd.getTables(catalog, schemaPattern, tableNamePattern, types);
            RowSetDynaClass rsdc = new RowSetDynaClass(rs, true);
            return rsdc.getRows();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw e;
        } finally {
            if (null != rs) {
                rs.close();
            }
        }
    }

    /**
     * 获全部表的树
     *
     * 供应商 Catalog支持 Schema支持 Oracle 不支持 Oracle User ID MySQL 不支持 数据库名 MSSQL 数据库名 对象属主名 Sybase 数据库名 数据库属主名 Informix 不支持
     * 不需要 PointBase 不支持 数据库名
     * 
     * @param dataSource 数据源
     * @param tableNamePattern 表名
     * @return String 表树JSON信息
     * @throws Exception 异常
     */
    public static String listAllTablesTree(DataSource dataSource, String tableNamePattern) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        ResultSet rs = null;
        ObjectMapper mapper = new ObjectMapper();
        String json = "[]";
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData dbmd = connection.getMetaData();

            String username = dbmd.getUserName().toUpperCase();
            String dialect = getDatabaseDialectName(dataSource);
            switch (dialect) {
                case SqlConstants.DBTYPE_MYSQL:
                    rs = dbmd.getTables(null, connection.getCatalog(), tableNamePattern, new String[] {"TABLE"});
                    break;
                case SqlConstants.DBTYPE_MSSQL:
                    rs = dbmd.getTables(connection.getCatalog(), null, tableNamePattern, new String[] {"TABLE"});
                    break;
                case SqlConstants.DBTYPE_ORACLE:
                    rs = dbmd.getTables(null, username, tableNamePattern, new String[] {"TABLE"});
                    break;
                case SqlConstants.DBTYPE_DM:
                    rs = dbmd.getTables(null, username, tableNamePattern, new String[] {"TABLE"});
                    break;
                case SqlConstants.DBTYPE_KINGBASE:
                    rs = dbmd.getTables(null, username, tableNamePattern, new String[] {"TABLE"});
                    break;
                default:
                    rs = dbmd.getTables(null, connection.getCatalog(), tableNamePattern, new String[] {"TABLE"});
                    break;
            }
            String dbName = "";
            while (rs.next()) {
                if (!rs.getString("TABLE_NAME").contains("$")) {
                    if ("mysql".equals(dialect)) {
                        dbName = rs.getString(1);
                    } else if ("oracle".equals(dialect)) {
                        dbName = dbmd.getUserName();
                    } else if ("dm".equals(dialect)) {
                        dbName = dbmd.getUserName();
                    } else if ("kingbase".equals(dialect)) {
                        dbName = dbmd.getUserName();
                    }
                    HashMap<String, Object> map = new HashMap<>(16);
                    map.put("text", rs.getString("TABLE_NAME"));
                    HashMap<String, Object> attributes = new HashMap<>(16);
                    attributes.put("catalog", rs.getString("TABLE_CAT"));
                    attributes.put("schema", rs.getString("TABLE_SCHEM"));
                    map.put("attributes", attributes);
                    list.add(map);
                }
            }
            HashMap<String, Object> pNode = new HashMap<>(16);
            pNode.put("id", 0);
            pNode.put("text", dbName + "库表列表");
            pNode.put("iconCls", "icon-folder");
            pNode.put("children", list);
            List<Map<String, Object>> tree = new ArrayList<>();
            tree.add(pNode);
            json = mapper.writeValueAsString(tree);

            return json;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw e;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    public static List<Map<String, Object>> listAllTypes(DataSource dataSource) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        ResultSet rs = null;
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData dbmd = connection.getMetaData();
            rs = dbmd.getTypeInfo();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>(16);
                map.put("TYPE_NAME", rs.getString("TYPE_NAME"));
                map.put("DATA_TYPE", rs.getInt("DATA_TYPE"));
                list.add(map);
            }
            return list;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw e;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

}
