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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.json.Y9JsonUtil;

@RestController
@RequestMapping(value = "/history")
@Slf4j
@RequiredArgsConstructor
public class HistoryController {

    private final Javers javers;

    @RequestMapping(value = "/changes", produces = "text/plain")
    public Y9Result<String> getEntityChanges(@RequestParam String entity, @RequestParam Optional<Object> id,
        @RequestParam Optional<String> author) {
        QueryBuilder jqlQuery = null;
        if (id.isPresent()) {
            jqlQuery = QueryBuilder.byInstanceId(id, entity);
        } else {
            try {
                this.getClass();
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

    @RequestMapping("/shadows")
    public Y9Result<List<Object>> getEntityShadows(@RequestParam String entity, @RequestParam Optional<Object> id,
        @RequestParam Optional<String> author) {
        QueryBuilder jqlQuery = null;
        if (id.isPresent()) {
            jqlQuery = QueryBuilder.byInstanceId(id, entity);
        } else {
            try {
                this.getClass();
                Class<?> clz = Class.forName(entity);
                jqlQuery = QueryBuilder.byClass(clz);
            } catch (ClassNotFoundException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }

        jqlQuery = author.isPresent() ? jqlQuery.byAuthor(author.get()) : jqlQuery;

        List<Shadow<Object>> shadows = javers.findShadows(jqlQuery.build());
        List<Object> rows = new ArrayList<Object>();
        for (Shadow<Object> s : shadows) {
            rows.add(s.get());
        }

        return Y9Result.success(rows);
    }

    @RequestMapping("/getShadowRows")
    public Y9Result<List<Map<String, Object>>> getEntityShadowsRows(@RequestParam String entity,
        @RequestParam Optional<String> id, @RequestParam Optional<String> author) throws ClassNotFoundException {
        QueryBuilder jqlQuery = null;
        this.getClass();
        if (id.isPresent()) {
            jqlQuery = QueryBuilder.byInstanceId(id.get(), entity);
        } else {
            jqlQuery = QueryBuilder.byClass(Class.forName(entity));
        }
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        jqlQuery = author.isPresent() ? jqlQuery.byAuthor(author.get()) : jqlQuery;
        List<CdoSnapshot> snapshots = javers.findSnapshots(jqlQuery.build());
        snapshots.sort(
            (o1, o2) -> -1 * o1.getCommitMetadata().getCommitDate().compareTo(o2.getCommitMetadata().getCommitDate()));
        Map<String, Object> map = new HashMap<>();
        JsonConverter jsonConverter = javers.getJsonConverter();
        List<Map<String, Object>> columns = new ArrayList<>();
        for (CdoSnapshot shot : snapshots) {
            CdoSnapshotState state = shot.getState();
            CommitMetadata commit = shot.getCommitMetadata();
            String json = jsonConverter.toJson(state);
            map = Y9JsonUtil.readHashMap(json, String.class, Object.class);
            map.put("commitAuthor",
                "修改时间：" + commit.getCommitDate().format(sdf) + "  修改人员："
                    + commit.getProperties().getOrDefault("authorName", " ") + "(IP:"
                    + commit.getProperties().getOrDefault("hostIp", "无") + ")");
            columns.add(map);
        }
        return Y9Result.success(columns);
    }

    @RequestMapping("/snapshots")
    public Y9Result<List<CdoSnapshot>> getEntitySnapshots(@RequestParam String entity,
        @RequestParam Optional<Object> id, @RequestParam Optional<String> author) {
        QueryBuilder jqlQuery = null;
        if (id.isPresent()) {
            jqlQuery = QueryBuilder.byInstanceId(id, entity);
        } else {
            try {
                this.getClass();
                Class<?> clz = Class.forName(entity);
                jqlQuery = QueryBuilder.byClass(clz);
            } catch (ClassNotFoundException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }

        jqlQuery = author.isPresent() ? jqlQuery.byAuthor(author.get()) : jqlQuery;

        List<CdoSnapshot> snapshots = javers.findSnapshots(jqlQuery.build());

        snapshots.sort(
            (o1, o2) -> -1 * o1.getCommitMetadata().getCommitDate().compareTo(o2.getCommitMetadata().getCommitDate()));

        return Y9Result.success(snapshots);
    }

    @RequestMapping("/getShadowTitles")
    public Y9Result<Set<String>> getShadowTitles(@RequestParam String entity, @RequestParam Optional<Object> id,
        @RequestParam Optional<String> author) {
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
