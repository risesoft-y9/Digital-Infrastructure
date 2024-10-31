package net.risesoft.api.platform.resource;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.enums.platform.AuthorityEnum;
import net.risesoft.model.platform.DataCatalog;
import net.risesoft.pojo.Y9Result;

/**
 * 数据目录 API
 *
 * @author shidaobang
 * @date 2024/10/10
 * @since 9.6.8
 */
public interface DataCatalogApi {

    /**
     * 根据父节点 id 获取数据目录树
     *
     * @param treeType 树类型
     * @param parentId 父节点 id
     * @param includeAllDescendant 包括所有后代节点
     * @param tenantId 租户 id
     * @param personId 人员 id，当 authority 不为空时 personId 也不能为空
     * @param authority 权限类型
     * @return {@code Y9Result<List<DataCatalog>> }
     */
    @GetMapping(value = "/tree")
    Y9Result<List<DataCatalog>> getTree(@RequestParam(name = "treeType") @NotBlank String treeType,
        @RequestParam(required = false) String parentId,
        @RequestParam(name = "includeAllDescendant", required = false) boolean includeAllDescendant,
        @RequestParam("tenantId") String tenantId, @RequestParam(name = "personId", required = false) String personId,
        @RequestParam(name = "authority", required = false) AuthorityEnum authority);

    /**
     * 搜索数据目录树
     *
     * @param treeType 树型
     * @param name 名称
     * @param tenantId 租户 id
     * @param personId 人员 id，当 authority 不为空时 personId 也不能为空
     * @param authority 权限类型
     * @return {@code Y9Result<List<DataCatalog>> }
     */
    @GetMapping(value = "/treeSearch")
    Y9Result<List<DataCatalog>> treeSearch(@RequestParam(name = "treeType") @NotBlank String treeType,
        @RequestParam("name") String name, @RequestParam("tenantId") String tenantId,
        @RequestParam(name = "personId", required = false) String personId,
        @RequestParam(name = "authority", required = false) AuthorityEnum authority);

    /**
     * 根据 id 获取目录
     *
     * @param id ID
     * @return {@code Y9Result<DataCatalog> }
     */
    @GetMapping(value = "/get")
    Y9Result<DataCatalog> getById(@RequestParam(name = "id") @NotBlank String id);

}
