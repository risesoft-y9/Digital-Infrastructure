package net.risesoft.y9.sqlddl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

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
public class SqlPaginationUtil {
    private static Logger log = LoggerFactory.getLogger(SqlPaginationUtil.class);

    private static String dbType;
    private static int dbVersion;

    public static String generatePagedSql(DataSource ds, String sql, int start, int limit) throws Exception {
        String rSql = "";
        if (limit == 0) {
            limit = Integer.MAX_VALUE;
        }

        if (dbType == null) {
            try (Connection connection = ds.getConnection();) {
                DatabaseMetaData dbmd = connection.getMetaData();
                String databaseName = dbmd.getDatabaseProductName().toLowerCase();
                if (databaseName.indexOf(SqlConstantUtil.DBTYPE_MYSQL) > -1) {
                    dbType = "mysql";
                } else if (databaseName.indexOf(SqlConstantUtil.DBTYPE_ORACLE) > -1) {
                    dbType = "oracle";
                } else if (databaseName.indexOf(SqlConstantUtil.DBTYPE_MICROSOFT) > -1) {
                    dbType = "mssql";
                }

                dbVersion = dbmd.getDatabaseMajorVersion();
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }

        rSql = generatePagedSql(dbType, dbVersion, sql, start, limit);
        return rSql;
    }

    public static String generatePagedSql(String databaseType, int databaseVersion, String sql, int start, int limit)
        throws Exception {
        String rSql = "";
        if (limit == 0) {
            limit = Integer.MAX_VALUE;
        }

        if (SqlConstantUtil.DBTYPE_MYSQL.equalsIgnoreCase(databaseType)) {
            rSql = sql + " limit " + start + "," + limit;
        } else if (SqlConstantUtil.DBTYPE_MSSQL.equalsIgnoreCase(databaseType)) {
            if (databaseVersion >= 12) {
                if (sql.toLowerCase().contains(" order by ")) {
                    // 只适用mssql2012版本
                    rSql = sql + " OFFSET " + start + " ROW FETCH NEXT " + limit + " rows only";
                } else {
                    rSql = "SELECT TOP " + limit
                        + " A.* FROM ( SELECT ROW_NUMBER() OVER (ORDER BY (select NULL)) AS RowNumber,B.* FROM ( " + sql
                        + ") B ) A WHERE A.RowNumber > " + start;
                }
            } else {
                rSql = "SELECT TOP " + limit
                    + " A.* FROM ( SELECT ROW_NUMBER() OVER (ORDER BY (select NULL)) AS RowNumber,B.* FROM ( " + sql
                    + ") B ) A WHERE A.RowNumber > " + start;
            }
        } else if ("oracle".equalsIgnoreCase(databaseType)) {
            rSql = "select * from (select mytable.*,rownum as my_rownum from (" + sql + ") mytable) where my_rownum<="
                + (start + limit) + " and my_rownum>" + start;
        }

        return rSql;
    }

    private SqlPaginationUtil() {
        throw new IllegalStateException("SqlPaginationUtil Utility class");
    }

}
