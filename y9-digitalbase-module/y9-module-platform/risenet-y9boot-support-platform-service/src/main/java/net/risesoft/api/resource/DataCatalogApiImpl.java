package net.risesoft.api.resource;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.resource.DataCatalogApi;
import net.risesoft.enums.platform.AuthorityEnum;
import net.risesoft.model.platform.DataCatalog;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.service.resource.Y9DataCatalogService;

/**
 * 数据目录 API 实现类
 *
 * @author shidaobang
 * @date 2024/10/10
 * @since 9.6.8
 */
@Primary
@Validated
@RestController
@RequestMapping(value = "/services/rest/v1/dataCatalog", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class DataCatalogApiImpl implements DataCatalogApi {

    private final Y9DataCatalogService y9DataCatalogService;

    @Override
    public Y9Result<List<DataCatalog>> getTree(@RequestParam(name = "treeType") @NotBlank String treeType,
        @RequestParam(required = false, name = "parentId") String parentId,
        @RequestParam(name = "includeAllDescendant", required = false) boolean includeAllDescendant,
        @RequestParam(name = "tenantId") String tenantId,
        @RequestParam(name = "personId", required = false) String personId,
        @RequestParam(name = "authority", required = false) AuthorityEnum authority) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9DataCatalogService.getTree(tenantId, parentId, treeType, Boolean.TRUE,
            includeAllDescendant, authority, personId));
    }

    @Override
    public Y9Result<List<DataCatalog>> treeSearch(@RequestParam(name = "treeType") @NotBlank String treeType,
        @RequestParam("name") String name, @RequestParam("tenantId") String tenantId,
        @RequestParam(name = "personId", required = false) String personId,
        @RequestParam(name = "authority", required = false) AuthorityEnum authority) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9DataCatalogService.treeSearch(tenantId, name, treeType, authority, personId));
    }

    @Override
    public Y9Result<DataCatalog> getById(@RequestParam(name = "tenantId") String tenantId,
        @RequestParam(name = "id") @NotBlank String id) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9DataCatalogService.getById(id));
    }

    @Override
    public Y9Result<DataCatalog> getTreeRoot(@RequestParam("tenantId") String tenantId,
        @RequestParam(name = "id") @NotBlank String id) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9DataCatalogService.getTreeRoot(id));
    }

}
