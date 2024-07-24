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
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

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
public class DbMetaDataUtil {

    public static int[] batchExecuteDdl(DataSource dataSource, List<String> sqlList) throws SQLException {
        java.sql.Statement stmt = null;
        String dialectName = getDatabaseDialectName(dataSource);
        try (Connection connection = dataSource.getConnection();) {
            stmt = connection.createStatement();
            if (SqlConstantUtil.DBTYPE_MYSQL.equals(dialectName)) {
                stmt.addBatch("SET FOREIGN_KEY_CHECKS=0");
            }
            for (String sql : sqlList) {
                stmt.addBatch(sql);
            }
            if (SqlConstantUtil.DBTYPE_MYSQL.equals(dialectName)) {
                stmt.addBatch("SET FOREIGN_KEY_CHECKS=1");
            }
            return stmt.executeBatch();
        } catch (SQLException e) {
            throw e;
        } finally {
            if (null != stmt && SqlConstantUtil.DBTYPE_MYSQL.equals(dialectName) && !stmt.isClosed()) {
                stmt.execute("SET FOREIGN_KEY_CHECKS=1");
                try {
                    stmt.close();
                } catch (Exception e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }
        }
    }

    public static boolean checkTableExist(DataSource dataSource, String tableName) throws Exception {
        String databaseName = null;
        String tableSchema = null;
        ResultSet rs = null;
        try (Connection connection = dataSource.getConnection();) {
            databaseName = connection.getCatalog();
            DatabaseMetaData dbmd = connection.getMetaData();
            String dialect = getDatabaseDialectName(dataSource);
            switch (dialect) {
                case SqlConstantUtil.DBTYPE_MYSQL:
                    rs = dbmd.getTables(null, databaseName, tableName, new String[] {"TABLE"});
                    break;
                case SqlConstantUtil.DBTYPE_MSSQL:
                    rs = dbmd.getTables(databaseName, null, tableName, new String[] {"TABLE"});
                    break;
                case SqlConstantUtil.DBTYPE_DM:
                case SqlConstantUtil.DBTYPE_ORACLE:
                    tableSchema = dbmd.getUserName().toUpperCase();
                    rs = dbmd.getTables(null, tableSchema, tableName, new String[] {"TABLE"});
                    break;
                default:
                    tableSchema = dbmd.getUserName().toUpperCase();
                    rs = dbmd.getTables(null, databaseName, tableName, new String[] {"TABLE"});
            }

            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    public static Boolean executeDdl(DataSource dataSource, String ddl) throws SQLException {
        try (Connection connection = dataSource.getConnection(); Statement stmt = connection.createStatement()) {
            return stmt.execute(ddl);
        } catch (SQLException e) {
            throw e;
        }
    }

    public static String getDatabaseDialectName(DataSource dataSource) {
        String databaseName = "";
        try {
            databaseName = getDatabaseProductName(dataSource).toLowerCase();
        } catch (SQLException e) {
            LOGGER.warn(e.getMessage(), e);
        }

        if (databaseName.indexOf(SqlConstantUtil.DBTYPE_MYSQL) > -1) {
            return "mysql";
        } else if (databaseName.indexOf(SqlConstantUtil.DBTYPE_ORACLE) > -1) {
            return "oracle";
        } else if (databaseName.indexOf(SqlConstantUtil.DBTYPE_MICROSOFT) > -1) {
            return "mssql";
        } else if (databaseName.indexOf(SqlConstantUtil.DBTYPE_DBMS) > -1) {
            return "dm";
        }
        return "";
    }

    public static int getDatabaseMajorVersion(DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection();) {
            DatabaseMetaData dbmd = connection.getMetaData();
            return dbmd.getDatabaseMajorVersion();
        } catch (SQLException e) {
            throw e;
        }
    }

    public static int getDatabaseMinorVersion(DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection();) {
            DatabaseMetaData dbmd = connection.getMetaData();
            return dbmd.getDatabaseMinorVersion();
        } catch (SQLException e) {
            throw e;
        }
    }

    public static String getDatabaseProductName(DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection();) {
            DatabaseMetaData dbmd = connection.getMetaData();
            String s = dbmd.getDatabaseProductName();
            return s;
        } catch (SQLException e) {
            throw e;
        }
    }

    public static String getDatabaseProductVersion(DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection();) {
            DatabaseMetaData dbmd = connection.getMetaData();
            return dbmd.getDatabaseProductVersion();
        } catch (SQLException e) {
            throw e;
        }
    }

    @SuppressWarnings({"resource"})
    public static List<DbColumn> listAllColumns(DataSource dataSource, String tableName, String columnNamePatten)
        throws Exception {
        String tableSchema = null;
        String databaseName = null;

        Statement stmt = null;
        ResultSet rs = null;
        List<DbColumn> dbColumnList = new ArrayList<>();
        if (DbMetaDataUtil.checkTableExist(dataSource, tableName)) {
            try (Connection connection = dataSource.getConnection();) {
                databaseName = connection.getCatalog();

                DatabaseMetaData dbmd = connection.getMetaData();
                tableSchema = dbmd.getUserName().toUpperCase();
                String dialect = getDatabaseDialectName(dataSource);

                // 获得主键
                switch (dialect) {
                    case SqlConstantUtil.DBTYPE_MYSQL:
                        rs = dbmd.getPrimaryKeys(null, databaseName, tableName);
                        break;
                    case SqlConstantUtil.DBTYPE_MSSQL:
                        rs = dbmd.getPrimaryKeys(databaseName, null, tableName);
                        break;
                    case SqlConstantUtil.DBTYPE_ORACLE:
                        rs = dbmd.getPrimaryKeys(null, tableSchema, tableName);
                        break;
                    case SqlConstantUtil.DBTYPE_DM:
                        rs = dbmd.getPrimaryKeys(null, tableSchema, tableName);
                        break;
                    default:
                        rs = dbmd.getPrimaryKeys(null, tableSchema, tableName);
                        break;
                }

                List<String> pkList = new ArrayList<>();
                while (rs.next()) {
                    pkList.add(rs.getString("COLUMN_NAME"));
                }

                if (pkList.isEmpty()) {
                    throw new Exception("***********没有主键？*************");
                }

                // 获得所有列的属性
                switch (dialect) {
                    case SqlConstantUtil.DBTYPE_MYSQL:
                        rs = dbmd.getColumns(null, databaseName, tableName, columnNamePatten);
                        break;
                    case SqlConstantUtil.DBTYPE_MSSQL:
                        rs = dbmd.getColumns(databaseName, null, tableName, columnNamePatten);
                        break;
                    case SqlConstantUtil.DBTYPE_ORACLE:
                        rs = dbmd.getColumns(null, tableSchema, tableName, columnNamePatten);
                        break;
                    case SqlConstantUtil.DBTYPE_DM:
                        rs = dbmd.getColumns(null, tableSchema, tableName, columnNamePatten);
                        break;
                    default:
                        rs = dbmd.getColumns(null, tableSchema, tableName, columnNamePatten);
                        break;
                }
                while (rs.next()) {
                    DbColumn dbColumn = new DbColumn();

                    dbColumn.setTableName(rs.getString("table_name").toUpperCase());

                    String columnName = rs.getString("column_name".toLowerCase());
                    dbColumn.setColumnName(columnName);
                    dbColumn.setColumnNameOld(columnName);

                    if (pkList.contains(columnName)) {
                        dbColumn.setPrimaryKey(true);
                    } else {
                        dbColumn.setPrimaryKey(false);
                    }

                    String remarks = rs.getString("remarks");
                    if (StringUtils.hasText(remarks)) {
                        dbColumn.setComment(remarks);
                    } else {
                        dbColumn.setComment(columnName.toUpperCase());
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
        try (Connection connection = dataSource.getConnection();) {
            DatabaseMetaData dbmd = connection.getMetaData();
            rs = dbmd.getExportedKeys(null, null, tableName);
            RowSetDynaClass rsdc = new RowSetDynaClass(rs, true);
            rs.close();

            rList.addAll(rsdc.getRows());
            return rList;
        } catch (Exception e) {
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
        try (Connection connection = dataSource.getConnection();) {
            DatabaseMetaData dbmd = connection.getMetaData();
            rs = dbmd.getImportedKeys(null, null, tableName);
            RowSetDynaClass rsdc = new RowSetDynaClass(rs, true);
            rs.close();

            rList.addAll(rsdc.getRows());

            return rList;
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != rs) {
                rs.close();
            }
        }
    }

    public static List<DynaBean> listAllIndexs(DataSource dataSource, String tableName) throws Exception {
        ResultSet rs = null;
        try (Connection connection = dataSource.getConnection();) {
            DatabaseMetaData dbmd = connection.getMetaData();
            rs = dbmd.getIndexInfo(null, null, tableName, false, false);
            RowSetDynaClass rsdc = new RowSetDynaClass(rs, true);
            return rsdc.getRows();
        } catch (Exception e) {
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

        try (Connection connection = dataSource.getConnection();) {
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
        try (Connection connection = dataSource.getConnection();) {
            DatabaseMetaData dbmd = connection.getMetaData();
            String username = dbmd.getUserName().toUpperCase();
            String dialect = getDatabaseDialectName(dataSource);
            switch (dialect) {
                case SqlConstantUtil.DBTYPE_MYSQL:
                    rs = dbmd.getTables(null, connection.getCatalog(), tableNamePattern, new String[] {"TABLE"});
                    break;
                case SqlConstantUtil.DBTYPE_MSSQL:
                    rs = dbmd.getTables(connection.getCatalog(), null, tableNamePattern, new String[] {"TABLE"});
                    break;
                case SqlConstantUtil.DBTYPE_ORACLE:
                    rs = dbmd.getTables(null, username, tableNamePattern, new String[] {"TABLE"});
                    break;
                case SqlConstantUtil.DBTYPE_DM:
                    rs = dbmd.getTables(null, username, tableNamePattern, new String[] {"TABLE"});
                    break;
                default:
                    rs = dbmd.getTables(null, connection.getCatalog(), tableNamePattern, new String[] {"TABLE"});
                    break;
            }

            while (rs.next()) {
                if (!rs.getString("TABLE_NAME").contains("$")) {
                    HashMap<String, String> map = new HashMap<>(6);
                    map.put("catalog", rs.getString("TABLE_CAT"));
                    map.put("schema", rs.getString("TABLE_SCHEM"));
                    map.put("name", rs.getString("TABLE_NAME"));
                    list.add(map);
                }
            }
            return list;
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != rs) {
                rs.close();
            }
        }
    }

    public static List<DynaBean> listAllTables(DataSource dataSource, String catalog, String schemaPattern,
        String tableNamePattern, String types[]) throws Exception {
        ResultSet rs = null;
        try (Connection connection = dataSource.getConnection();) {
            DatabaseMetaData dbmd = connection.getMetaData();
            rs = dbmd.getTables(catalog, schemaPattern, tableNamePattern, types);
            RowSetDynaClass rsdc = new RowSetDynaClass(rs, true);
            return rsdc.getRows();
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != rs) {
                rs.close();
            }
        }
    }

    /***********
     * 供应商 Catalog支持 Schema支持 Oracle 不支持 Oracle User ID MySQL 不支持 数据库名 MSSQL 数据库名 对象属主名 Sybase 数据库名 数据库属主名 Informix 不支持
     * 不需要 PointBase 不支持 数据库名
     ***********/
    public static String listAllTablesTree(DataSource dataSource, String tableNamePattern) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        ResultSet rs = null;
        ObjectMapper mapper = new ObjectMapper();
        String json = "[]";
        try (Connection connection = dataSource.getConnection();) {
            DatabaseMetaData dbmd = connection.getMetaData();

            String username = dbmd.getUserName().toUpperCase();
            String dialect = getDatabaseDialectName(dataSource);
            switch (dialect) {
                case SqlConstantUtil.DBTYPE_MYSQL:
                    rs = dbmd.getTables(null, connection.getCatalog(), tableNamePattern, new String[] {"TABLE"});
                    break;
                case SqlConstantUtil.DBTYPE_MSSQL:
                    rs = dbmd.getTables(connection.getCatalog(), null, tableNamePattern, new String[] {"TABLE"});
                    break;
                case SqlConstantUtil.DBTYPE_ORACLE:
                    rs = dbmd.getTables(null, username, tableNamePattern, new String[] {"TABLE"});
                    break;
                case SqlConstantUtil.DBTYPE_DM:
                    rs = dbmd.getTables(null, username, tableNamePattern, new String[] {"TABLE"});
                    break;
                default:
                    rs = dbmd.getTables(null, connection.getCatalog(), tableNamePattern, new String[] {"TABLE"});
                    break;
            }
            String dbName = "";
            while (rs.next()) {
                if (!rs.getString("TABLE_NAME").contains("$")) {
                    if (SqlConstantUtil.DBTYPE_MYSQL.equals(dialect)) {
                        dbName = rs.getString(1);
                    } else if (SqlConstantUtil.DBTYPE_ORACLE.equals(dialect)) {
                        dbName = dbmd.getUserName();
                    } else if (SqlConstantUtil.DBTYPE_DM.equals(dialect)) {
                        dbName = dbmd.getUserName();
                    }
                    HashMap<String, Object> map = new HashMap<>(4);
                    map.put("text", rs.getString("TABLE_NAME"));
                    HashMap<String, Object> attributes = new HashMap<>(2);
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
            throw e;
        } finally {
            if (null != rs) {
                rs.close();
            }
        }
    }

    public static List<Map<String, Object>> listAllTypes(DataSource dataSource) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        ResultSet rs = null;
        try (Connection connection = dataSource.getConnection();) {
            DatabaseMetaData dbmd = connection.getMetaData();
            rs = dbmd.getTypeInfo();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>(2);
                map.put("TYPE_NAME", rs.getString("TYPE_NAME"));
                map.put("DATA_TYPE", rs.getInt("DATA_TYPE"));
                list.add(map);
            }
            return list;
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != rs) {
                rs.close();
            }
        }
    }

    private DbMetaDataUtil() {
        throw new IllegalStateException("DbMetaDataUtil Utility class");
    }

}
