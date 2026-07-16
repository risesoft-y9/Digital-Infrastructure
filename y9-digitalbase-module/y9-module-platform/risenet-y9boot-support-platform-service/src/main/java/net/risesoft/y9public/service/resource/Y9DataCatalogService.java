package net.risesoft.y9public.service.resource;

import java.util.List;
import java.util.Optional;

import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.model.platform.dictionary.OptionValue;
import net.risesoft.model.platform.resource.DataCatalog;
import net.risesoft.y9.exception.Y9BusinessException;
import net.risesoft.y9.exception.Y9NotFoundException;
import net.risesoft.y9public.entity.resource.Y9DataCatalog;

/**
 * 数据目录 service
 *
 * @author shidaobang
 * @date 2024/10/09
 * @since 9.6.8
 */
public interface Y9DataCatalogService {

    /**
     * 根据 id 删除数据目录
     *
     * @param id 唯一标识
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    void delete(String id);

    /**
     * 根据 id 获取数据目录
     *
     * @param id 唯一标识
     * @return {@link DataCatalog}
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    DataCatalog getDataCatalogById(String id);

    /**
     * 保存或更新数据目录
     *
     * @param y9DataCatalog 数据目录对象
     * @return {@link DataCatalog}
     * @throws Y9BusinessException 自定义 id 已被使用的情况
     */
    DataCatalog saveOrUpdate(DataCatalog y9DataCatalog);

    /**
     * 按年份范围批量保存数据目录
     *
     * @param y9DataCatalog 数据目录对象
     * @param startYear 开始年份
     * @param endYear 结束年份
     * @throws Y9BusinessException 自定义 id 已被使用的情况
     */
    void saveByYears(DataCatalog y9DataCatalog, Integer startYear, Integer endYear);

    /**
     * 获取数据目录树
     *
     * @param tenantId 租户 id
     * @param parentId 父节点 id
     * @param treeType 树类型
     * @return {@code List<DataCatalog>}
     */
    List<DataCatalog> getTree(String tenantId, String parentId, String treeType);

    /**
     * 根据条件获取数据目录树
     *
     * @param tenantId 租户 id
     * @param parentId 父节点 id
     * @param treeType 树类型
     * @param enabled 是否启用
     * @param includeAllDescendant 是否包含所有后代节点
     * @param authority 权限类型
     * @param personId 人员 id
     * @return {@code List<DataCatalog>}
     */
    List<DataCatalog> getTree(String tenantId, String parentId, String treeType, Boolean enabled,
        boolean includeAllDescendant, AuthorityEnum authority, String personId);

    /**
     * 根据名称搜索数据目录树
     *
     * @param tenantId 租户 id
     * @param name 数据目录名称
     * @param treeType 树类型
     * @return {@code List<DataCatalog>}
     */
    List<DataCatalog> treeSearch(String tenantId, String name, String treeType);

    /**
     * 根据名称和权限搜索数据目录树
     *
     * @param tenantId 租户 id
     * @param name 数据目录名称
     * @param treeType 树类型
     * @param authority 权限类型
     * @param personId 人员 id
     * @return {@code List<DataCatalog>}
     */
    List<DataCatalog> treeSearch(String tenantId, String name, String treeType, AuthorityEnum authority,
        String personId);

    /**
     * 获取数据目录树类型列表
     *
     * @return {@code List<OptionValue>}
     */
    List<OptionValue> getTreeTypeList();

    /**
     * 按树类型保存数据目录
     *
     * @param y9DataCatalog 数据目录对象
     * @throws Y9BusinessException 自定义 id 已被使用的情况
     */
    void saveByType(DataCatalog y9DataCatalog);

    /**
     * 获取数据目录树的根节点
     *
     * @param id 数据目录 id
     * @return {@link DataCatalog}
     * @throws Y9NotFoundException id 或其祖先节点对应的记录不存在的情况
     */
    DataCatalog getTreeRoot(String id);

    /**
     * 查询所有数据目录根节点
     *
     * @return {@code List<DataCatalog>}
     */
    List<DataCatalog> listRoot();

    /**
     * 根据树类型、父节点 id 和名称查找数据目录
     *
     * @param treeType 树类型
     * @param parentId 父节点 id
     * @param dataCatalogName 数据目录名称
     * @return {@code Optional<Y9DataCatalog>}
     */
    Optional<Y9DataCatalog> findByTreeTypeAndParentIdAndName(String treeType, String parentId, String dataCatalogName);

    /**
     * 获取祖先列表（从父节点到根节点）
     *
     * @param parentId 父id
     * @return {@code List<DataCatalog> }
     * @throws Y9NotFoundException parentId 或其祖先节点对应的记录不存在的情况
     */
    List<DataCatalog> getAncestorList(String parentId);
}
