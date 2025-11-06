package net.risesoft.y9.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;

/**
 * 拷贝阿里druid代码
 *
 * @author DZJ
 * @date 2022/02/26
 */
public class DbUtil {

    private DbUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String getCatalog(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection();) {
            String catalog = connection.getCatalog();
            if (StringUtils.isBlank(catalog)) {
                return null;
            }
            return catalog;
        } catch (SQLException e) {
            return null;
        }
    }

    public static DbType getDbType(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection();) {
            return getDbType(connection.getMetaData().getURL());
        } catch (SQLException e) {
            return null;
        }
    }

    public static DbType getDbType(String rawUrl) {
        if (rawUrl == null) {
            return null;
        }

        if (rawUrl.startsWith("jdbc:derby:") || rawUrl.startsWith("jdbc:log4jdbc:derby:")) {
            return DbType.derby;
        } else if (rawUrl.startsWith("jdbc:mysql:") || rawUrl.startsWith("jdbc:cobar:")
            || rawUrl.startsWith("jdbc:log4jdbc:mysql:")) {
            return DbType.mysql;
        } else if (rawUrl.startsWith("jdbc:mariadb:")) {
            return DbType.mariadb;
        } else if (rawUrl.startsWith("jdbc:oracle:") || rawUrl.startsWith("jdbc:log4jdbc:oracle:")) {
            return DbType.oracle;
        } else if (rawUrl.startsWith("jdbc:alibaba:oracle:")) {
            return DbType.ali_oracle;
        } else if (rawUrl.startsWith("jdbc:oceanbase:oracle:")) {
            return DbType.oceanbase_oracle;
        } else if (rawUrl.startsWith("jdbc:oceanbase:")) {
            return DbType.oceanbase;
        } else if (rawUrl.startsWith("jdbc:microsoft:") || rawUrl.startsWith("jdbc:log4jdbc:microsoft:")) {
            return DbType.sqlserver;
        } else if (rawUrl.startsWith("jdbc:sqlserver:") || rawUrl.startsWith("jdbc:log4jdbc:sqlserver:")) {
            return DbType.sqlserver;
        } else if (rawUrl.startsWith("jdbc:sybase:Tds:") || rawUrl.startsWith("jdbc:log4jdbc:sybase:")) {
            return DbType.sybase;
        } else if (rawUrl.startsWith("jdbc:jtds:") || rawUrl.startsWith("jdbc:log4jdbc:jtds:")) {
            return DbType.jtds;
        } else if (rawUrl.startsWith("jdbc:fake:") || rawUrl.startsWith("jdbc:mock:")) {
            return DbType.mock;
        } else if (rawUrl.startsWith("jdbc:postgresql:") || rawUrl.startsWith("jdbc:log4jdbc:postgresql:")) {
            return DbType.postgresql;
        } else if (rawUrl.startsWith("jdbc:edb:")) {
            return DbType.edb;
        } else if (rawUrl.startsWith("jdbc:hsqldb:") || rawUrl.startsWith("jdbc:log4jdbc:hsqldb:")) {
            return DbType.hsql;
        } else if (rawUrl.startsWith("jdbc:odps:")) {
            return DbType.odps;
        } else if (rawUrl.startsWith("jdbc:db2:")) {
            return DbType.db2;
        } else if (rawUrl.startsWith("jdbc:sqlite:")) {
            return DbType.sqlite;
        } else if (rawUrl.startsWith("jdbc:ingres:")) {
            return DbType.ingres;
        } else if (rawUrl.startsWith("jdbc:h2:") || rawUrl.startsWith("jdbc:log4jdbc:h2:")) {
            return DbType.h2;
        } else if (rawUrl.startsWith("jdbc:mckoi:")) {
            return DbType.mock;
        } else if (rawUrl.startsWith("jdbc:cloudscape:")) {
            return DbType.cloudscape;
        } else if (rawUrl.startsWith("jdbc:informix-sqli:") || rawUrl.startsWith("jdbc:log4jdbc:informix-sqli:")) {
            return DbType.informix;
        } else if (rawUrl.startsWith("jdbc:timesten:")) {
            return DbType.timesten;
        } else if (rawUrl.startsWith("jdbc:as400:")) {
            return DbType.as400;
        } else if (rawUrl.startsWith("jdbc:sapdb:")) {
            return DbType.sapdb;
        } else if (rawUrl.startsWith("jdbc:JSQLConnect:")) {
            return DbType.JSQLConnect;
        } else if (rawUrl.startsWith("jdbc:JTurbo:")) {
            return DbType.JTurbo;
        } else if (rawUrl.startsWith("jdbc:firebirdsql:")) {
            return DbType.firebirdsql;
        } else if (rawUrl.startsWith("jdbc:interbase:")) {
            return DbType.interbase;
        } else if (rawUrl.startsWith("jdbc:pointbase:")) {
            return DbType.pointbase;
        } else if (rawUrl.startsWith("jdbc:edbc:")) {
            return DbType.edbc;
        } else if (rawUrl.startsWith("jdbc:mimer:multi1:")) {
            return DbType.mimer;
        } else if (rawUrl.startsWith("jdbc:dm:")) {
            return JdbcConstants.DM;
        } else if (rawUrl.startsWith("jdbc:kingbase:") || rawUrl.startsWith("jdbc:kingbase8:")) {
            return JdbcConstants.KINGBASE;
        } else if (rawUrl.startsWith("jdbc:gbase:")) {
            return JdbcConstants.GBASE;
        } else if (rawUrl.startsWith("jdbc:xugu:")) {
            return JdbcConstants.XUGU;
        } else if (rawUrl.startsWith("jdbc:log4jdbc:")) {
            return DbType.log4jdbc;
        } else if (rawUrl.startsWith("jdbc:hive:")) {
            return DbType.hive;
        } else if (rawUrl.startsWith("jdbc:hive2:")) {
            return DbType.hive;
        } else if (rawUrl.startsWith("jdbc:phoenix:")) {
            return DbType.phoenix;
        } else if (rawUrl.startsWith("jdbc:kylin:")) {
            return DbType.kylin;
        } else if (rawUrl.startsWith("jdbc:elastic:")) {
            return DbType.elastic_search;
        } else if (rawUrl.startsWith("jdbc:clickhouse:")) {
            return DbType.clickhouse;
        } else if (rawUrl.startsWith("jdbc:presto:")) {
            return DbType.presto;
        } else if (rawUrl.startsWith("jdbc:trino:")) {
            return DbType.trino;
        } else if (rawUrl.startsWith("jdbc:inspur:")) {
            return DbType.kdb;
        } else if (rawUrl.startsWith("jdbc:polardb")) {
            return DbType.polardb;
        } else if (rawUrl.startsWith("jdbc:highgo:")) {
            return DbType.highgo;
        } else if (rawUrl.startsWith("jdbc:pivotal:greenplum:") || rawUrl.startsWith("jdbc:datadirect:greenplum:")) {
            return DbType.greenplum;
        } else {
            return null;
        }
    }

    public static String getDbTypeString(DataSource dataSource) {
        DbType dbType = getDbType(dataSource);
        if (dbType == null) {
            return null;
        }
        return dbType.name();
    }

    public static String getDbTypeString(String rawUrl) {
        DbType dbType = getDbType(rawUrl);

        if (dbType == null) {
            return null;
        }

        return dbType.name();
    }

    public static String getSchema(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection();) {
            String schema;
            schema = connection.getSchema();
            if (StringUtils.isBlank(schema)) {
                return null;
            }
            return schema;
        } catch (SQLException e) {
            return null;
        }
    }
}
