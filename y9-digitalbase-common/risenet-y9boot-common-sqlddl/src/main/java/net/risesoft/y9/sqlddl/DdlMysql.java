package net.risesoft.y9.sqlddl;

import javax.sql.DataSource;

import com.fasterxml.jackson.databind.type.TypeFactory;

import net.risesoft.y9.json.Y9JsonUtil;

/**
 *
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 */
public class DdlMysql {

    public static void addTableColumn(DataSource dataSource, String tableName, String jsonDbColumns) throws Exception {
        DbColumn[] dbcs = Y9JsonUtil.objectMapper.readValue(jsonDbColumns,
            TypeFactory.defaultInstance().constructArrayType(DbColumn.class));
        if (DbMetaDataUtil.checkTableExist(dataSource, tableName)) {
            for (DbColumn dbc : dbcs) {
                String ddl = "ALTER TABLE " + tableName + " ADD COLUMN " + dbc.getColumnName() + " ";
                String sType = dbc.getTypeName().toUpperCase();
                if (SqlConstantUtil.CHAR_TYPE.equals(sType) || SqlConstantUtil.VARCHAR_TYPE.equals(sType)) {
                    ddl += sType + "(" + dbc.getDataLength() + ")";
                } else if (SqlConstantUtil.DECIMAL_TYPE.equals(sType) || SqlConstantUtil.NUMERIC_TYPE.equals(sType)) {
                    if (dbc.getDataScale() == null) {
                        ddl += sType + "(" + dbc.getDataLength() + ")";
                    } else {
                        ddl += sType + "(" + dbc.getDataLength() + "," + dbc.getDataScale() + ")";
                    }
                } else {
                    ddl += sType;
                }

                if (dbc.getNullable() == true) {
                    ddl += " DEFAULT NULL";
                } else {
                    ddl += " NOT NULL";
                }
                if (dbc.getComment().length() > 0) {
                    ddl += " COMMENT '" + dbc.getComment() + "'";
                }
                DbMetaDataUtil.executeDdl(dataSource, ddl);
            }
        } else {// table不存在。
            StringBuilder sb = new StringBuilder();
            //@formatter:off
			sb.append("CREATE TABLE " + tableName + " (\r\n").append("id varchar(38) NOT NULL, \r\n").append("processInstanceId varchar(64) NOT NULL, \r\n").append("createTime datetime NOT NULL, \r\n").append("updateTime datetime NOT NULL, \r\n").append("tabindex double DEFAULT 1, \r\n");
			//@formatter:off
			for (DbColumn dbc : dbcs) {
				String columnName = dbc.getColumnName();
				if ("id".equalsIgnoreCase(columnName) || "processInstanceId".equalsIgnoreCase(columnName) || "createTime".equalsIgnoreCase(columnName) || "updateTime".equalsIgnoreCase(columnName) || "tabindex".equalsIgnoreCase(columnName)) {
					continue;
				}

				sb.append(columnName).append(" ");
				String sType = dbc.getTypeName().toUpperCase();
				if (SqlConstantUtil.CHAR_TYPE.equals(sType) || SqlConstantUtil.VARCHAR_TYPE.equals(sType)) {
					sb.append(sType + "(" + dbc.getDataLength() + ")");
				} else if (SqlConstantUtil.DECIMAL_TYPE.equals(sType) || SqlConstantUtil.NUMERIC_TYPE.equals(sType)) {
					if (dbc.getDataScale() == null) {
						sb.append(sType + "(" + dbc.getDataLength() + ")");
					} else {
						sb.append(sType + "(" + dbc.getDataLength() + "," + dbc.getDataScale() + ")");
					}
				} else {
					sb.append(sType);
				}

				if (!dbc.getNullable()) {
					sb.append(" NOT NULL");
				}
				if (dbc.getComment().length() > 0) {
					sb.append(" COMMENT '" + dbc.getComment() + "'");
				}
				sb.append(",\r\n");
			}

			sb.append("PRIMARY KEY (id) \r\n").append(")");
			DbMetaDataUtil.executeDdl(dataSource, sb.toString());
		}
	}


    public static void alterTableColumn(DataSource dataSource, String tableName, String jsonDbColumns) throws Exception {
        if (!DbMetaDataUtil.checkTableExist(dataSource, tableName)) {
            throw new Exception("数据库中不存在这个表：" + tableName);
        }

        DbColumn[] dbcs = Y9JsonUtil.objectMapper.readValue(jsonDbColumns, TypeFactory.defaultInstance().constructArrayType(DbColumn.class));
        for (DbColumn dbc : dbcs) {
            String ddl = "ALTER TABLE " + tableName;
            // 字段名称没有改变
            if (dbc.getColumnName().equalsIgnoreCase(dbc.getColumnNameOld())) {
                ddl += " MODIFY COLUMN " + dbc.getColumnName() + " ";
            } else {
                ddl += " CHANGE COLUMN " + dbc.getColumnNameOld() + " " + dbc.getColumnName() + " ";
            }

            String sType = dbc.getTypeName().toUpperCase();
            if (SqlConstantUtil.CHAR_TYPE.equals(sType) || SqlConstantUtil.VARCHAR_TYPE.equals(sType)) {
                ddl += sType + "(" + dbc.getDataLength() + ")";
            } else if (SqlConstantUtil.DECIMAL_TYPE.equals(sType) || SqlConstantUtil.NUMERIC_TYPE.equals(sType)) {
                if (dbc.getDataScale() == null) {
                    ddl += sType + "(" + dbc.getDataLength() + ")";
                } else {
                    ddl += sType + "(" + dbc.getDataLength() + "," + dbc.getDataScale() + ")";
                }
            } else {
                ddl += sType;
            }

            if (dbc.getNullable() == true) {
                ddl += " DEFAULT NULL";
            } else {
                ddl += " NOT NULL";
            }
            if (dbc.getComment().length() > 0) {
                ddl += " COMMENT '" + dbc.getComment() + "'";
            }

            DbMetaDataUtil.executeDdl(dataSource, ddl);
        }
    }

    public static void dropTable(DataSource dataSource, String tableName) throws Exception {
		DbMetaDataUtil.executeDdl(dataSource, "DROP TABLE " + tableName);
	}

	public static void dropTableColumn(DataSource dataSource, String tableName, String columnName) throws Exception {
		DbMetaDataUtil.executeDdl(dataSource, "ALTER TABLE " + tableName + " DROP COLUMN " + columnName);
	}

	public static void renameTable(DataSource dataSource, String tableNameOld, String tableNameNew) throws Exception {
		DbMetaDataUtil.executeDdl(dataSource, "ALTER TABLE " + tableNameOld + " RENAME " + tableNameNew);
	}

	private DdlMysql() {
        throw new IllegalStateException("DdlMysql Utility class");
      }

}
