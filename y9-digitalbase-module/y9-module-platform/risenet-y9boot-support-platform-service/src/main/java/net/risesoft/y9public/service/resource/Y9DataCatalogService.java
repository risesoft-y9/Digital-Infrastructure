package net.risesoft.y9public.service.resource;

import java.util.List;

import net.risesoft.entity.Y9OptionValue;
import net.risesoft.enums.platform.AuthorityEnum;
import net.risesoft.model.platform.DataCatalog;
import net.risesoft.y9public.entity.resource.Y9DataCatalog;

/**
 * 数据目录 service
 *
 * @author shidaobang
 * @date 2024/10/09
 * @since 9.6.8
 */
public interface Y9DataCatalogService {

    List<DataCatalog> getTree(String parentId, String treeType);

    DataCatalog getById(String id);

    Y9DataCatalog saveOrUpdate(Y9DataCatalog y9DataCatalog);

    void delete(String id);

    void saveByYears(Y9DataCatalog y9DataCatalog, Integer startYear, Integer endYear);

    List<DataCatalog> treeSearch(String name, String treeType);

    List<DataCatalog> getTree(String parentId, String treeType, Boolean enabled, boolean includeAllDescendant,
        AuthorityEnum authority, String personId);

    List<DataCatalog> treeSearch(String name, String treeType, AuthorityEnum authority, String personId);

    List<Y9OptionValue> getTreeTypeList();

    void saveByType(Y9DataCatalog y9DataCatalog);
}
