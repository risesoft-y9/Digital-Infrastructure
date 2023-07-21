package y9.dbcomment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.db.DbUtil;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationInfoList;
import io.github.classgraph.AnnotationParameterValue;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.FieldInfo;
import io.github.classgraph.FieldInfoList;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.MethodInfoList;
import io.github.classgraph.ScanResult;

/**
 *
 * @author DZJ
 */
@Slf4j
public class Y9CommentUtil {
    public static void scanClassInfo(ClassInfo classInfo, Map<String, String> tableCommitMap, Map<String, String> fieldCommitMap) {
        AnnotationInfoList annotationInfoList = classInfo.getAnnotationInfo();
        List<String> annotationNames = annotationInfoList.getNames();
        for (String annotationName : annotationNames) {
            AnnotationInfo annotationInfo = annotationInfoList.get(annotationName);

            if ("javax.persistence.Table".equals(annotationName)) {
                try {
                    List<AnnotationParameterValue> pvs = annotationInfo.getParameterValues();
                    if (pvs != null) {
                        for (AnnotationParameterValue pv : pvs) {
                            if ("name".equals(pv.getName())) {
                                String tableName = (String)pv.getValue();
                                tableCommitMap.put("tableName", tableName);
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.warn(e.getMessage(), e);
                }

            }

            if ("org.hibernate.annotations.Table".equals(annotationName)) {
                List<AnnotationParameterValue> pvs = annotationInfo.getParameterValues();
                if (pvs != null) {
                    for (AnnotationParameterValue pv : pvs) {
                        if ("comment".equals(pv.getName())) {
                            String tableCommit = (String)pv.getValue();
                            tableCommitMap.put("tableCommit", tableCommit);
                            break;
                        }
                    }
                }
            }

            if ("y9.dbComment.annotation.TableComment".equals(annotationName)) {
                List<AnnotationParameterValue> pvs = annotationInfo.getParameterValues();
                if (pvs != null) {
                    for (AnnotationParameterValue pv : pvs) {
                        if ("value".equals(pv.getName())) {
                            String tableCommit = (String)pv.getValue();
                            tableCommitMap.put("tableCommit", tableCommit);
                            break;
                        }
                    }
                }
            }
        }

        FieldInfoList fieldInfoList = classInfo.getFieldInfo();
        List<String> fieldInfoNames = fieldInfoList.getNames();
        for (String fieldInfoName : fieldInfoNames) {
            FieldInfo fieldInfo = fieldInfoList.get(fieldInfoName);

            String fieldName = "";
            String fieldCommit = "";

            annotationInfoList = fieldInfo.getAnnotationInfo();
            annotationNames = annotationInfoList.getNames();
            for (String annotationName : annotationNames) {
                AnnotationInfo annotationInfo = annotationInfoList.get(annotationName);

                if ("javax.persistence.Column".equals(annotationName)) {
                    List<AnnotationParameterValue> pvs = annotationInfo.getParameterValues();
                    if (pvs != null) {
                        for (AnnotationParameterValue pv : pvs) {
                            if ("name".equals(pv.getName())) {
                                fieldName = (String)pv.getValue();
                                break;
                            }
                        }
                    }
                }

                if ("javax.persistence.JoinColumn".equals(annotationName)) {
                    List<AnnotationParameterValue> pvs = annotationInfo.getParameterValues();
                    if (pvs != null) {
                        for (AnnotationParameterValue pv : pvs) {
                            if ("name".equals(pv.getName())) {
                                fieldName = (String)pv.getValue();
                                break;
                            }
                        }
                    }
                }

                if ("org.hibernate.annotations.Comment".equals(annotationName)) {
                    List<AnnotationParameterValue> pvs = annotationInfo.getParameterValues();
                    if (pvs != null) {
                        for (AnnotationParameterValue pv : pvs) {
                            if ("value".equals(pv.getName())) {
                                fieldCommit = (String)pv.getValue();
                                break;
                            }
                        }
                    }
                }

                if ("y9.dbComment.annotation.FieldComment".equals(annotationName)) {
                    List<AnnotationParameterValue> pvs = annotationInfo.getParameterValues();
                    if (pvs != null) {
                        for (AnnotationParameterValue pv : pvs) {
                            if ("value".equals(pv.getName())) {
                                fieldCommit = (String)pv.getValue();
                                break;
                            }
                        }
                    }
                }
            }
            if (StringUtils.hasText(fieldName)) {
                fieldCommitMap.put(fieldName, fieldCommit);
            }
        }

        MethodInfoList methodInfoList = classInfo.getMethodInfo();
        List<String> methodInfoNames = methodInfoList.getNames();
        for (String methodInfoName : methodInfoNames) {
            MethodInfoList methodInfoList2 = methodInfoList.get(methodInfoName);
            MethodInfo methodInfo = methodInfoList2.get(0);
            String fieldName = "";
            String fieldCommit = "";

            annotationInfoList = methodInfo.getAnnotationInfo();
            annotationNames = annotationInfoList.getNames();
            for (String annotationName : annotationNames) {
                AnnotationInfo annotationInfo = annotationInfoList.get(annotationName);

                if ("javax.persistence.Column".equals(annotationName)) {
                    List<AnnotationParameterValue> pvs = annotationInfo.getParameterValues();
                    if (pvs != null) {
                        for (AnnotationParameterValue pv : pvs) {
                            if ("name".equals(pv.getName())) {
                                fieldName = (String)pv.getValue();
                                break;
                            }
                        }
                    }
                }

                if ("javax.persistence.JoinColumn".equals(annotationName)) {
                    List<AnnotationParameterValue> pvs = annotationInfo.getParameterValues();
                    if (pvs != null) {
                        for (AnnotationParameterValue pv : pvs) {
                            if ("name".equals(pv.getName())) {
                                fieldName = (String)pv.getValue();
                                break;
                            }
                        }
                    }
                }

                if ("org.hibernate.annotations.Comment".equals(annotationName)) {
                    List<AnnotationParameterValue> pvs = annotationInfo.getParameterValues();
                    if (pvs != null) {
                        for (AnnotationParameterValue pv : pvs) {
                            if ("value".equals(pv.getName())) {
                                fieldCommit = (String)pv.getValue();
                                break;
                            }
                        }
                    }
                }

                if ("y9.dbComment.annotation.FieldComment".equals(annotationName)) {
                    List<AnnotationParameterValue> pvs = annotationInfo.getParameterValues();
                    if (pvs != null) {
                        for (AnnotationParameterValue pv : pvs) {
                            if ("value".equals(pv.getName())) {
                                fieldCommit = (String)pv.getValue();
                                break;
                            }
                        }
                    }
                }
            }
            if (StringUtils.hasText(fieldName)) {
                fieldCommitMap.put(fieldName, fieldCommit);
            }
        }
    }

    public static void scanner4Mysql(JdbcTemplate jdbcTemplate, String... scanPackage) {
        String schema = DbUtil.getSchema(jdbcTemplate.getDataSource());

        String sql = "SELECT " + "table_name," + "column_name," + "CONCAT('ALTER TABLE `'," + "        table_name," + "        '` CHANGE `'," + "        column_name," + "        '` `'," + "        column_name," + "        '` '," + "        column_type," + "        ' ',"
            + "        IF(is_nullable = 'YES', '' , 'NOT NULL ')," + "        IF(column_default IS NOT NULL, CONCAT('DEFAULT ', IF(column_default = 'CURRENT_TIMESTAMP', column_default, CONCAT('\\'',column_default,'\\'') ), ' '), ''),"
            + "        IF(column_default IS NULL AND is_nullable = 'YES' AND column_key = '' AND column_type = 'timestamp','NULL ', '')," + "        IF(column_default IS NULL AND is_nullable = 'YES' AND column_key = '','DEFAULT NULL ', '')," + "        extra,"
            + "        ' COMMENT \\'column_comment\\' ;') AS script " + "FROM information_schema.columns " + "WHERE table_schema=? AND table_name=? " + "ORDER BY table_name, column_name";

        try (ScanResult scanResult = new ClassGraph().enableAllInfo().acceptPackages(scanPackage).scan()) {
            ClassInfoList classInfoList = scanResult.getClassesWithAnnotation("javax.persistence.Table");
            List<String> classInfoNames = classInfoList.getNames();
            for (String className : classInfoNames) {
                LOGGER.debug(className);
                Map<String, String> tableCommitMap = new HashMap<>(16);
                Map<String, String> fieldCommitMap = new HashMap<>(16);

                ClassInfo classInfo = classInfoList.get(className);
                ClassInfo superClassInfo = classInfo.getSuperclass();

                scanClassInfo(classInfo, tableCommitMap, fieldCommitMap);
                if (superClassInfo != null) {
                    scanClassInfo(superClassInfo, tableCommitMap, fieldCommitMap);
                }

                String tableName = tableCommitMap.get("tableName");
                String tableCommit = tableCommitMap.get("tableCommit");
                List<Map<String, Object>> records = jdbcTemplate.queryForList(sql, schema, tableName);
                if (StringUtils.hasText(tableCommit)) {
                    try {
                        String alterSql = "ALTER TABLE " + tableName + " COMMENT '" + tableCommit + "';";
                        LOGGER.debug(alterSql);
                        jdbcTemplate.execute(alterSql);
                    } catch (Exception e) {
                        LOGGER.warn(e.getMessage(), e);
                    }
                }
                for (Map.Entry<String, String> entry2 : fieldCommitMap.entrySet()) {
                    String fieldName = entry2.getKey();
                    String fieldCommit = entry2.getValue();
                    String script = "";
                    if (StringUtils.hasText(fieldCommit)) {
                        for (Map<String, Object> record : records) {
                            if (fieldName.equalsIgnoreCase((String)record.get("column_name"))) {
                                script = (String)record.get("script");
                                break;
                            }
                        }
                        if (StringUtils.hasText(script)) {
                            script = script.replace("column_comment", fieldCommit);
                            try {
                                LOGGER.debug(script);
                                jdbcTemplate.execute(script);
                            } catch (Exception e) {
                                LOGGER.warn(e.getMessage(), e);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void scanner4Oracle(JdbcTemplate jdbcTemplate, String... scanPackage) {
        try (ScanResult scanResult = new ClassGraph().enableAllInfo().acceptPackages(scanPackage).scan()) {
            ClassInfoList classInfoList = scanResult.getClassesWithAnnotation("javax.persistence.Table");
            List<String> classInfoNames = classInfoList.getNames();
            for (String className : classInfoNames) {
                LOGGER.debug(className);
                Map<String, String> tableCommitMap = new HashMap<>(16);
                Map<String, String> fieldCommitMap = new HashMap<>(16);

                ClassInfo classInfo = classInfoList.get(className);
                ClassInfo superClassInfo = classInfo.getSuperclass();

                scanClassInfo(classInfo, tableCommitMap, fieldCommitMap);
                if (superClassInfo != null) {
                    scanClassInfo(superClassInfo, tableCommitMap, fieldCommitMap);
                }

                String tableName = tableCommitMap.get("tableName");
                String tableCommit = tableCommitMap.get("tableCommit");
                if (StringUtils.hasText(tableCommit)) {
                    try {
                        String sql = "COMMENT ON TABLE " + tableName + " IS '" + tableCommit + "'";
                        LOGGER.debug(sql);
                        jdbcTemplate.execute(sql);
                    } catch (Exception e) {
                        LOGGER.warn(e.getMessage(), e);
                    }
                }
                for (Map.Entry<String, String> entry2 : fieldCommitMap.entrySet()) {
                    String fieldName = entry2.getKey();
                    String fieldCommit = entry2.getValue();
                    if (StringUtils.hasText(fieldCommit)) {
                        String sql = "comment on column " + tableName + "." + fieldName + " is '" + fieldCommit + "'";
                        LOGGER.debug(sql);
                        jdbcTemplate.execute(sql);
                    }
                }
            }
        }
    }
}
