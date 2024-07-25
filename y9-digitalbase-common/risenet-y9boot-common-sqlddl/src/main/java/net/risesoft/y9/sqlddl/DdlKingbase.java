package net.risesoft.y9.sqlddl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.type.TypeFactory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.sqlddl.pojo.DbColumn;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/21
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DdlKingbase {

    public static void addTableColumn(DataSource dataSource, String tableName, List<DbColumn> dbcs) throws Exception {
        StringBuilder sb = new StringBuilder();
        if (DbMetaDataUtil.checkTableExist(dataSource, tableName)) {
            for (DbColumn dbc : dbcs) {
                String columnName = dbc.getColumnName();
                if ("GUID".equalsIgnoreCase(columnName) || "PROCESSINSTANCEID".equalsIgnoreCase(columnName)) {
                    continue;
                }
                sb = new StringBuilder();
                sb.append("ALTER TABLE \"" + tableName + "\"");
                String nullable = "";
                String dbColumnName = "";
                ResultSet rs = null;
                try (Connection connection = dataSource.getConnection()) {
                    DatabaseMetaData dbmd = connection.getMetaData();
                    String tableSchema = dbmd.getUserName().toUpperCase();
                    rs = dbmd.getColumns(null, tableSchema, tableName, dbc.getColumnName().toUpperCase());
                    while (rs.next()) {
                        nullable = rs.getString("is_nullable");
                        // 当前列目前是否可为空
                        dbColumnName = rs.getString("column_name".toLowerCase());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (rs != null) {
                        rs.close();
                    }
                }

                boolean add = false;
                // 不存在旧字段则新增
                if ("".equals(dbColumnName) && org.apache.commons.lang3.StringUtils.isBlank(dbc.getColumnNameOld())) {
                    sb.append(" ADD " + dbc.getColumnName() + " ");
                    add = true;
                } else {
                    // 存在旧字段，字段名称没有改变则修改属性
                    if (columnName.equalsIgnoreCase(dbc.getColumnNameOld())
                        || org.apache.commons.lang3.StringUtils.isBlank(dbc.getColumnNameOld())) {
                        sb.append(" ALTER COLUMN " + dbc.getColumnName() + " TYPE ");
                    } else {// 存在旧字段，字段名称改变则修改字段名称及属性
                        try {
                            StringBuilder sb1 = new StringBuilder();
                            sb1.append("ALTER TABLE \"" + tableName + "\"");
                            DbMetaDataUtil.executeDdl(dataSource,
                                sb1.append(" RENAME COLUMN " + dbc.getColumnNameOld() + " TO " + dbc.getColumnName())
                                    .toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        sb.append(" ALTER COLUMN " + dbc.getColumnName() + " TYPE ");
                    }
                }
                String sType = dbc.getTypeName().toUpperCase();
                if ("CHAR".equals(sType) || "NCHAR".equals(sType) || "VARCHAR2".equals(sType)
                    || "NVARCHAR2".equals(sType) || "RAW".equals(sType)) {
                    sb.append(sType + "(" + dbc.getDataLength() + " char)");
                } else if ("DECIMAL".equalsIgnoreCase(sType) || "NUMERIC".equalsIgnoreCase(sType)
                    || "NUMBER".equalsIgnoreCase(sType)) {
                    if (dbc.getDataScale() == null) {
                        sb.append(sType + "(" + dbc.getDataLength() + ")");
                    } else {
                        sb.append(sType + "(" + dbc.getDataLength() + "," + dbc.getDataScale() + ")");
                    }
                } else {
                    sb.append(sType);
                }

                DbMetaDataUtil.executeDdl(dataSource, sb.toString());
                // 新增字段
                if ("".equals(nullable) && add) {
                    if (Boolean.TRUE.equals(dbc.getNullable())) {
                    } else {
                        sb = new StringBuilder();
                        sb.append("ALTER TABLE \"" + tableName + "\"");
                        sb.append(" ALTER COLUMN " + dbc.getColumnName() + " SET NOT NULL");
                        DbMetaDataUtil.executeDdl(dataSource, sb.toString());
                    }
                } else {// 修改字段
                    if (Boolean.TRUE.equals(dbc.getNullable()) && "NO".equals(nullable)) {
                        sb = new StringBuilder();
                        sb.append("ALTER TABLE \"" + tableName + "\"");
                        sb.append(" ALTER COLUMN " + dbc.getColumnName() + " DROP NOT NULL");
                        DbMetaDataUtil.executeDdl(dataSource, sb.toString());
                    }
                    if (!Boolean.TRUE.equals(dbc.getNullable()) && "YES".equals(nullable)) {
                        sb = new StringBuilder();
                        sb.append("ALTER TABLE \"" + tableName + "\"");
                        sb.append(" ALTER COLUMN " + dbc.getColumnName() + " SET NOT NULL");
                        DbMetaDataUtil.executeDdl(dataSource, sb.toString());
                    }
                }

                if (StringUtils.hasText(dbc.getComment())) {
                    DbMetaDataUtil.executeDdl(dataSource, "COMMENT ON COLUMN \"" + tableName + "\"."
                        + dbc.getColumnName().trim().toUpperCase() + " IS '" + dbc.getComment() + "'");
                }
            }
        }
    }

    public static void alterTableColumn(DataSource dataSource, String tableName, String jsonDbColumns)
        throws Exception {
        if (!DbMetaDataUtil.checkTableExist(dataSource, tableName)) {
            throw new Exception("数据库中不存在这个表：" + tableName);
        }

        DbColumn[] dbcs = Y9JsonUtil.objectMapper.readValue(jsonDbColumns,
            TypeFactory.defaultInstance().constructArrayType(DbColumn.class));
        for (DbColumn dbc : dbcs) {
            if (StringUtils.hasText(dbc.getColumnNameOld())) {
                StringBuilder sb = new StringBuilder();
                sb.append("ALTER TABLE \"" + tableName + "\"");
                // 字段名称有改变
                if (!dbc.getColumnName().equalsIgnoreCase(dbc.getColumnNameOld())) {
                    try {
                        DbMetaDataUtil.executeDdl(dataSource,
                            sb.append(" RENAME COLUMN " + dbc.getColumnNameOld() + " TO " + dbc.getColumnName())
                                .toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                sb.append(" MODIFY " + dbc.getColumnName() + " ");
                String sType = dbc.getTypeName().toUpperCase();
                if ("CHAR".equals(sType) || "NCHAR".equals(sType) || "VARCHAR2".equals(sType)
                    || "NVARCHAR2".equals(sType) || "RAW".equals(sType)) {
                    sb.append(sType + "(" + dbc.getDataLength() + " char)");
                } else if ("DECIMAL".equalsIgnoreCase(sType) || "NUMERIC".equalsIgnoreCase(sType)
                    || "NUMBER".equalsIgnoreCase(sType)) {
                    if (dbc.getDataScale() == null) {
                        sb.append(sType + "(" + dbc.getDataLength() + ")");
                    } else {
                        sb.append(sType + "(" + dbc.getDataLength() + "," + dbc.getDataScale() + ")");
                    }
                } else {
                    sb.append(sType);
                }

                List<DbColumn> list = DbMetaDataUtil.listAllColumns(dataSource, tableName, dbc.getColumnNameOld());
                if (list != null && !list.isEmpty()) {
                    DbColumn column = list.get(0);
                    if (Boolean.TRUE.equals(dbc.getNullable())) {
                        if (Boolean.FALSE.equals(column.getNullable())) {
                            sb.append(" NULL");
                        }
                    } else {
                        if (Boolean.TRUE.equals(column.getNullable())) {
                            sb.append(" NOT NULL");
                        }
                    }
                }
                DbMetaDataUtil.executeDdl(dataSource, sb.toString());
                if (StringUtils.hasText(dbc.getComment()) && (list != null && !list.isEmpty())) {
                    DbColumn column = list.get(0);
                    if (!column.getComment().equals(dbc.getComment())) {
                        DbMetaDataUtil.executeDdl(dataSource, "COMMENT ON COLUMN \"" + tableName + "\"."
                            + dbc.getColumnName().trim().toUpperCase() + " IS '" + dbc.getComment() + "'");
                    }
                }
            }
        }
    }

    public static void createTable(DataSource dataSource, String tableName, String jsonDbColumns) throws Exception {
        StringBuilder sb = new StringBuilder();
        DbColumn[] dbcs = Y9JsonUtil.objectMapper.readValue(jsonDbColumns,
            TypeFactory.defaultInstance().constructArrayType(DbColumn.class));
        //@formatter:off
		sb.append("CREATE TABLE \"" + tableName + "\" (\r\n").append("GUID varchar2(38 char) NOT NULL, \r\n").append("PROCESSINSTANCEID varchar2(64 char) NOT NULL, \r\n");
		//@formatter:off
		for (DbColumn dbc : dbcs) {
			String columnName = dbc.getColumnName();
			if ("GUID".equalsIgnoreCase(columnName) || "PROCESSINSTANCEID".equalsIgnoreCase(columnName)) {
				continue;
			}
			sb.append(columnName).append(" ");
			String sType = dbc.getTypeName().toUpperCase();
			if ("CHAR".equals(sType) || "NCHAR".equals(sType) || "VARCHAR2".equals(sType) || "NVARCHAR2".equals(sType) || "RAW".equals(sType)) {
				sb.append(sType + "(" + dbc.getDataLength() + " char)");
			} else if ("DECIMAL".equalsIgnoreCase(sType) || "NUMERIC".equalsIgnoreCase(sType) || "NUMBER".equalsIgnoreCase(sType)) {
				if (dbc.getDataScale() == null) {
					sb.append(sType + "(" + dbc.getDataLength() + ")");
				} else {
					sb.append(sType + "(" + dbc.getDataLength() + "," + dbc.getDataScale() + ")");
				}
			} else {
				sb.append(sType);
			}

			if (Boolean.FALSE.equals(dbc.getNullable())) {
				sb.append(" NOT NULL");
			}
			sb.append(",\r\n");
		}
		sb.append("PRIMARY KEY (GUID) \r\n").append(")");
		DbMetaDataUtil.executeDdl(dataSource, sb.toString());

		for (DbColumn dbc : dbcs) {
			if (StringUtils.hasText(dbc.getComment())) {
				DbMetaDataUtil.executeDdl(dataSource, "COMMENT ON COLUMN \"" + tableName + "\"." + dbc.getColumnName().trim().toUpperCase() + " IS '" + dbc.getComment() + "'");
			}
		}
	}

	public static void dropTable(DataSource dataSource, String tableName) throws Exception {
		if (DbMetaDataUtil.checkTableExist(dataSource, tableName)) {
			DbMetaDataUtil.executeDdl(dataSource, "DROP TABLE \"" + tableName + "\"");
		}
	}

	public static void dropTableColumn(DataSource dataSource, String tableName, String columnName) throws Exception {
		DbMetaDataUtil.executeDdl(dataSource, "ALTER TABLE \"" + tableName + "\" DROP COLUMN " + columnName);
	}

	public static void renameTable(DataSource dataSource, String tableNameOld, String tableNameNew) throws Exception {
		DbMetaDataUtil.executeDdl(dataSource, "RENAME \"" + tableNameOld + "\" TO \"" + tableNameNew + "\"");
	}
}
