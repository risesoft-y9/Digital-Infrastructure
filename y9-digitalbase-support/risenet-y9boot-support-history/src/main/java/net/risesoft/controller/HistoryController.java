package net.risesoft.controller;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.javers.core.Javers;
import org.javers.core.changelog.SimpleTextChangeLog;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.CdoSnapshotState;
import org.javers.repository.jql.QueryBuilder;
import org.javers.shadow.Shadow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9StringUtil;

/**
 * 实体修改历史
 *
 * @author shidaobang
 * @date 2025/01/06
 */
@RestController
@RequestMapping(value = "/history")
@Slf4j
public class HistoryController {

    @Autowired(required = false)
    private Javers javers;

    /**
     * 获取实体修改
     *
     * @param entity 实体类
     * @param id ID
     * @param author 操作人
     * @return {@code Y9Result<String> }
     */
    @RequestMapping(value = "/changes")
    @Transactional(value = "rsPublicTransactionManager")
    public Y9Result<String> getEntityChanges(@RequestParam String entity, @RequestParam Optional<Object> id,
        @RequestParam Optional<String> author) {
        if (javers == null) {
            return Y9Result.success();
        } else {
            QueryBuilder jqlQuery = null;
            if (id.isPresent()) {
                jqlQuery = QueryBuilder.byInstanceId(id, entity);
            } else {
                try {
                    Class<?> clz = Class.forName(entity);
                    jqlQuery = QueryBuilder.byClass(clz);
                } catch (ClassNotFoundException e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }

            jqlQuery = author.isPresent() ? jqlQuery.byAuthor(author.get()) : jqlQuery;

            List<Change> changes = javers.findChanges(jqlQuery.build());

            String changeLog = javers.processChangeList(changes, new SimpleTextChangeLog());
            changeLog.replaceAll("\n", "<br>");

            return Y9Result.success(changeLog);
        }
    }

    /**
     * 获取实体的历史版本
     *
     * @param entity 实体
     * @param id ID
     * @param author 操作人
     * @return {@code Y9Result<List<Object>> }
     */
    @RequestMapping("/shadows")
    @Transactional(value = "rsPublicTransactionManager")
    public Y9Result<List<Object>> getEntityShadows(@RequestParam String entity, @RequestParam Optional<Object> id,
        @RequestParam Optional<String> author) {
        if (javers == null) {
            return Y9Result.success(List.of());
        } else {
            QueryBuilder jqlQuery = null;
            if (id.isPresent()) {
                jqlQuery = QueryBuilder.byInstanceId(id, entity);
            } else {
                try {
                    Class<?> clz = Class.forName(entity);
                    jqlQuery = QueryBuilder.byClass(clz);
                } catch (ClassNotFoundException e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }

            jqlQuery = author.isPresent() ? jqlQuery.byAuthor(author.get()) : jqlQuery;

            List<Shadow<Object>> shadows = javers.findShadows(jqlQuery.build());
            List<Object> rows = new ArrayList<>();
            for (Shadow<Object> s : shadows) {
                rows.add(s.get());
            }

            return Y9Result.success(rows);
        }
    }

    /**
     * 获取实体历史版本
     *
     * @param entity 实体
     * @param id ID
     * @param author 操作人
     * @return {@code Y9Result<List<Map<String, Object>>> }
     * @throws ClassNotFoundException 找不到类异常
     */
    @RequestMapping("/getShadowRows")
    @Transactional(value = "rsPublicTransactionManager")
    public Y9Result<List<Map<String, Object>>> getEntityShadowsRows(@RequestParam String entity,
        @RequestParam Optional<String> id, @RequestParam Optional<String> author) throws ClassNotFoundException {
        if (javers == null) {
            return Y9Result.success(List.of());
        } else {
            QueryBuilder jqlQuery = null;
            if (id.isPresent()) {
                jqlQuery = QueryBuilder.byInstanceId(id.get(), entity);
            } else {
                jqlQuery = QueryBuilder.byClass(Class.forName(entity));
            }
            DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            jqlQuery = author.isPresent() ? jqlQuery.byAuthor(author.get()) : jqlQuery;
            List<CdoSnapshot> snapshots = javers.findSnapshots(jqlQuery.build());
            snapshots.sort((o1, o2) -> -1
                * o1.getCommitMetadata().getCommitDate().compareTo(o2.getCommitMetadata().getCommitDate()));
            Map<String, Object> map = new HashMap<>();
            JsonConverter jsonConverter = javers.getJsonConverter();
            List<Map<String, Object>> columns = new ArrayList<>();
            for (CdoSnapshot shot : snapshots) {
                CdoSnapshotState state = shot.getState();
                CommitMetadata commit = shot.getCommitMetadata();
                String json = jsonConverter.toJson(state);
                map = Y9JsonUtil.readHashMap(json, String.class, Object.class);
                map.put("commitAuthor", Y9StringUtil.format("时间：{},人员：{},IP：{}", commit.getCommitDate().format(sdf),
                    commit.getAuthor(), commit.getProperties().getOrDefault("hostIp", "无")));
                columns.add(map);
            }
            return Y9Result.success(columns);
        }
    }

    /**
     * 获取实体历史版本（键值对的形式）
     *
     * @param entity 实体
     * @param id ID
     * @param author 作者
     * @return {@code Y9Result<List<CdoSnapshot>> }
     */
    @RequestMapping("/snapshots")
    @Transactional(value = "rsPublicTransactionManager")
    public Y9Result<List<CdoSnapshot>> getEntitySnapshots(@RequestParam String entity,
        @RequestParam Optional<Object> id, @RequestParam Optional<String> author) {
        if (javers == null) {
            return Y9Result.success(List.of());
        } else {
            QueryBuilder jqlQuery = null;
            if (id.isPresent()) {
                jqlQuery = QueryBuilder.byInstanceId(id, entity);
            } else {
                try {
                    Class<?> clz = Class.forName(entity);
                    jqlQuery = QueryBuilder.byClass(clz);
                } catch (ClassNotFoundException e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }

            jqlQuery = author.isPresent() ? jqlQuery.byAuthor(author.get()) : jqlQuery;

            List<CdoSnapshot> snapshots = javers.findSnapshots(jqlQuery.build());

            snapshots.sort((o1, o2) -> -1
                * o1.getCommitMetadata().getCommitDate().compareTo(o2.getCommitMetadata().getCommitDate()));

            return Y9Result.success(snapshots);
        }
    }

    /**
     * 获取实体字段名
     *
     * @param entity 实体
     * @param id ID
     * @return {@code Y9Result<Set<String>> }
     */
    @RequestMapping("/getShadowTitles")
    @Transactional(value = "rsPublicTransactionManager")
    public Y9Result<Set<String>> getShadowTitles(@RequestParam String entity, @RequestParam Optional<Object> id) {
        if (javers == null) {
            return Y9Result.success(Set.of());
        } else {
            Set<String> titles = new HashSet<>();
            try {
                Optional<CdoSnapshot> snapshot = javers.getLatestSnapshot(id.orElse(null), Class.forName(entity));
                if (snapshot.isPresent()) {
                    CdoSnapshotState state = snapshot.get().getState();
                    titles.addAll(state.getPropertyNames());
                }
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
            return Y9Result.success(titles);
        }
    }

}
