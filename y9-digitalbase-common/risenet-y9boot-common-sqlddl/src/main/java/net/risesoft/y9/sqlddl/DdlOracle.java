package net.risesoft.y9.sqlddl;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.type.TypeFactory;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.json.Y9JsonUtil;

/**
 *
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 */
@Slf4j
public class DdlOracle {

    public static void addTableColumn(DataSource dataSource, String tableName, String jsonDbColumns) throws Exception {
        StringBuilder sb = new StringBuilder();
        DbColumn[] dbcs = Y9JsonUtil.objectMapper.readValue(jsonDbColumns,
            TypeFactory.defaultInstance().constructArrayType(DbColumn.class));
        if (DbMetaDataUtil.checkTableExist(dataSource, tableName)) {
            for (DbColumn dbc : dbcs) {
                sb.append("ALTER TABLE " + tableName + " ADD " + dbc.getColumnName() + " ");
                String sType = dbc.getTypeName().toUpperCase();
                if (SqlConstantUtil.CHAR_TYPE.equals(sType) || SqlConstantUtil.NCHAR_TYPE.equals(sType)
                    || SqlConstantUtil.VARCHAR2_TYPE.equals(sType) || SqlConstantUtil.NVARCHAR2_TYPE.equals(sType)
                    || SqlConstantUtil.RAW_TYPE.equals(sType)) {
                    sb.append(sType + "(" + dbc.getDataLength() + ")");
                } else if (SqlConstantUtil.DECIMAL_TYPE.equalsIgnoreCase(sType)
                    || SqlConstantUtil.NUMERIC_TYPE.equalsIgnoreCase(sType)
                    || SqlConstantUtil.NUMBER_TYPE.equalsIgnoreCase(sType)) {
                    if (dbc.getDataScale() == null) {
                        sb.append(sType + "(" + dbc.getDataLength() + ")");
                    } else {
                        sb.append(sType + "(" + dbc.getDataLength() + "," + dbc.getDataScale() + ")");
                    }
                } else {
                    sb.append(sType);
                }

                if (dbc.getNullable()) {
                    sb.append(" NULL");
                } else {
                    sb.append(" NOT NULL");
                }

                DbMetaDataUtil.executeDdl(dataSource, sb.toString());
                if (StringUtils.hasText(dbc.getComment())) {
                    DbMetaDataUtil.executeDdl(dataSource, "COMMENT ON COLUMN " + tableName.trim().toUpperCase() + "."
                        + dbc.getColumnName().trim().toUpperCase() + " IS '" + dbc.getComment() + "'");
                }
            }
        } else { // table不存在。
            //@formatter:off
			sb.append("CREATE TABLE " + tableName + " (\r\n").append("ID varchar2(38) NOT NULL, \r\n").append("PROCESSINSTANCEID nvarchar2(64) NOT NULL, \r\n").append("CREATETIME timestamp NOT NULL, \r\n").append("UPDATETIME timestamp NOT NULL, \r\n").append(
					"TABINDEX NUMERIC(19,3) DEFAULT 1, \r\n");
			//@formatter:off
			for (DbColumn dbc : dbcs) {
				String columnName = dbc.getColumnName();
				if ("ID".equalsIgnoreCase(columnName) || "PROCESSINSTANCEID".equalsIgnoreCase(columnName) || "CREATETIME".equalsIgnoreCase(columnName) || "UPDATETIME".equalsIgnoreCase(columnName) || "TABINDEX".equalsIgnoreCase(columnName)) {
					continue;
				}

				sb.append(columnName).append(" ");
				String sType = dbc.getTypeName().toUpperCase();
				if (SqlConstantUtil.CHAR_TYPE.equals(sType) || SqlConstantUtil.NCHAR_TYPE.equals(sType) || SqlConstantUtil.VARCHAR2_TYPE.equals(sType) || SqlConstantUtil.NVARCHAR2_TYPE.equals(sType) || SqlConstantUtil.RAW_TYPE.equals(sType)) {
					sb.append(sType + "(" + dbc.getDataLength() + ")");
				} else if (SqlConstantUtil.DECIMAL_TYPE.equalsIgnoreCase(sType) || SqlConstantUtil.NUMERIC_TYPE.equalsIgnoreCase(sType) || SqlConstantUtil.NUMBER_TYPE.equalsIgnoreCase(sType)) {
					if (dbc.getDataScale() == null) {
						sb.append(sType + "(" + dbc.getDataLength() + ")");
					} else {
						sb.append(sType + "(" + dbc.getDataLength() + "," + dbc.getDataScale() + ")");
					}
				} else {
					sb.append(sType);
				}

				if (dbc.getNullable() == false) {
					sb.append(" NOT NULL");
				}
				sb.append(",\r\n");
			}
			sb.append("PRIMARY KEY (ID) \r\n").append(")");
			DbMetaDataUtil.executeDdl(dataSource, sb.toString());

			for (DbColumn dbc : dbcs) {
				if (StringUtils.hasText(dbc.getComment())) {
					DbMetaDataUtil.executeDdl(dataSource, "COMMENT ON COLUMN " + tableName.trim().toUpperCase() + "." + dbc.getColumnName().trim().toUpperCase() + " IS '" + dbc.getComment() + "'");
				}
			}
		}
	}

    public static void alterTableColumn(DataSource dataSource, String tableName, String jsonDbColumns) throws Exception {
        if (!DbMetaDataUtil.checkTableExist(dataSource, tableName)) {
            throw new Exception("数据库中不存在这个表：" + tableName);
        }

        DbColumn[] dbcs = Y9JsonUtil.objectMapper.readValue(jsonDbColumns, TypeFactory.defaultInstance().constructArrayType(DbColumn.class));
        for (DbColumn dbc : dbcs) {
            if (StringUtils.hasText(dbc.getColumnNameOld())) {
                StringBuilder sb = new StringBuilder();
                sb.append("ALTER TABLE " + tableName);
                // 字段名称有改变
                if (!dbc.getColumnName().equalsIgnoreCase(dbc.getColumnNameOld())) {
                    try {
                        DbMetaDataUtil.executeDdl(dataSource, sb.append(" RENAME COLUMN " + dbc.getColumnNameOld() + " TO " + dbc.getColumnName()).toString());
                    } catch (Exception e) {
                        LOGGER.warn(e.getMessage(), e);
                    }
                }
                sb.append(" MODIFY " + dbc.getColumnName() + " ");

                String sType = dbc.getTypeName().toUpperCase();
                if (SqlConstantUtil.CHAR_TYPE.equals(sType) || SqlConstantUtil.NCHAR_TYPE.equals(sType) || SqlConstantUtil.VARCHAR2_TYPE.equals(sType) || SqlConstantUtil.NVARCHAR2_TYPE.equals(sType) || SqlConstantUtil.RAW_TYPE.equals(sType)) {
                    sb.append(sType + "(" + dbc.getDataLength() + ")");
                } else if (SqlConstantUtil.DECIMAL_TYPE.equalsIgnoreCase(sType) || SqlConstantUtil.NUMERIC_TYPE.equalsIgnoreCase(sType) || SqlConstantUtil.NUMBER_TYPE.equalsIgnoreCase(sType)) {
                    if (dbc.getDataScale() == null) {
                        sb.append(sType + "(" + dbc.getDataLength() + ")");
                    } else {
                        sb.append(sType + "(" + dbc.getDataLength() + "," + dbc.getDataScale() + ")");
                    }
                } else {
                    sb.append(sType);
                }

                List<DbColumn> list = DbMetaDataUtil.listAllColumns(dataSource, tableName, dbc.getColumnNameOld());
                if(!list.isEmpty()) {
                    Boolean dbNullable =list.get(0).getNullable();
                    if (dbc.getNullable()) {
                        if (dbNullable) {
                            sb.append(" NULL");
                        }
                    } else {
                        if (!dbNullable) {
                            sb.append(" NOT NULL");
                        }
                    }
                    DbMetaDataUtil.executeDdl(dataSource, sb.toString());
                    if (StringUtils.hasText(dbc.getComment()) && !list.get(0).getComment().equals(dbc.getComment())) {
                        DbMetaDataUtil.executeDdl(dataSource, "COMMENT ON COLUMN " + tableName.trim().toUpperCase() + "." + dbc.getColumnName().trim().toUpperCase() + " IS '" + dbc.getComment() + "'");
                    }
                }
            }
        }
    }

    public static void dropTable(DataSource dataSource, String tableName) throws Exception {
		DbMetaDataUtil.executeDdl(dataSource, "DROP TABLE " + tableName);
	}

	public static void dropTableColumn(DataSource dataSource, String tableName, String columnName) throws Exception {
		DbMetaDataUtil.executeDdl(dataSource, "ALTER TABLE " + tableName + " DROP COLUMN " + columnName);
	}

	public static void renameTable(DataSource dataSource, String tableNameOld, String tableNameNew) throws Exception {
		DbMetaDataUtil.executeDdl(dataSource, "RENAME " + tableNameOld + " TO " + tableNameNew);
	}

	private DdlOracle() {
        throw new IllegalStateException("DdlOracle Utility class");
    }
}
