package net.risesoft.y9public.service.resource;

import java.util.List;

import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.model.platform.dictionary.OptionValue;
import net.risesoft.model.platform.resource.DataCatalog;

/**
 * 数据目录 service
 *
 * @author shidaobang
 * @date 2024/10/09
 * @since 9.6.8
 */
public interface Y9DataCatalogService {

    void delete(String id);

    DataCatalog getDataCatalogById(String id);

    DataCatalog saveOrUpdate(DataCatalog y9DataCatalog);

    void saveByYears(DataCatalog y9DataCatalog, Integer startYear, Integer endYear);

    List<DataCatalog> getTree(String tenantId, String parentId, String treeType);

    List<DataCatalog> getTree(String tenantId, String parentId, String treeType, Boolean enabled,
        boolean includeAllDescendant, AuthorityEnum authority, String personId);

    List<DataCatalog> treeSearch(String tenantId, String name, String treeType);

    List<DataCatalog> treeSearch(String tenantId, String name, String treeType, AuthorityEnum authority,
        String personId);

    List<OptionValue> getTreeTypeList();

    void saveByType(DataCatalog y9DataCatalog);

    DataCatalog getTreeRoot(String id);

    List<DataCatalog> listRoot();
}
