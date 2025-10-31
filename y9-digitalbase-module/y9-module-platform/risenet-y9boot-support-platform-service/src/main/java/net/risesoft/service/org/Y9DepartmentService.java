package net.risesoft.service.org;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import net.risesoft.entity.org.Y9Department;
import net.risesoft.entity.org.Y9OrgBase;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.y9.exception.Y9NotFoundException;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9DepartmentService {

    /**
     * 禁用/启用部门(级联子部门)
     *
     * @param id 部门id
     * @return {@link Y9Department}
     */
    Y9Department changeDisable(String id);

    /**
     * 根据主键id删除部门实例
     *
     * @param id 部门id
     */
    void delete(String id);

    /**
     * 根据id查找部门
     *
     * @param id 部门id
     * @return {@code Optional<Y9Department>} 部门对象 或 null
     */
    Optional<Y9Department> findById(String id);

    /**
     * 根据主键id获取部门实例
     *
     * @param id 部门id
     * @return OrgDepartment
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Department getById(String id);

    /**
     * 查询部门
     *
     * @return {@code List<Y9Department>}
     */
    List<Y9Department> list();

    List<Y9Department> list(List<String> ids);

    /**
     * 获取组织机构下所有的委办局
     *
     * @param organizationId 组织机构id
     * @param disabled 是否禁用
     * @return {@code List<Y9Department>}
     */
    List<Y9Department> listBureau(String organizationId, Boolean disabled);

    /**
     * 根据名称查询委办局列表
     *
     * @param name 部门名
     * @param disabled 是否禁用
     * @return {@code List<Y9Department>}
     * @since 9.6.6
     */
    List<Y9Department> listBureauByNameLike(String name, Boolean disabled);

    /**
     * 根据dn查询部门列表
     *
     * @param dn dn
     * @param disabled 是否禁用
     * @return {@code List<Y9Department>}
     */
    List<Y9Department> listByDn(String dn, Boolean disabled);

    /**
     * 根据名称查询
     *
     * @param name 部门名
     * @param disabled 是否禁用
     * @return {@code List<Y9Department>}
     */
    List<Y9Department> listByNameLike(String name, Boolean disabled);

    /**
     * 根据父节点id,获取本层级的部门列表
     *
     * @param parentId 父节点id
     * @param disabled 是否禁用
     * @return {@code List<Y9Department>}
     */
    List<Y9Department> listByParentId(String parentId, Boolean disabled);

    /**
     * 组织节点（人员或岗位）列表
     *
     * @param deptId 部门ID
     * @param category 部门属性类型
     * @param inherit
     * @param disabled 是否禁用
     * @return {@code List<Y9OrgBase> }
     */
    List<Y9OrgBase> listDepartmentPropOrgUnits(String deptId, Integer category, Boolean inherit, Boolean disabled);

    /**
     * 获得部门树
     *
     * @param orgBaseId ：机构节点唯一标识(可能是机构id,也可能是部门id)
     * @param disabled 是否禁用
     * @return {@code List<Y9Department>}
     */
    List<Y9Department> listRecursivelyByParentId(String orgBaseId, Boolean disabled);

    /**
     * 移动部门到新的节点
     *
     * @param id 部门id
     * @param parentId 新父节点id
     * @return {@link Y9Department}
     */
    Y9Department move(String id, String parentId);

    /**
     * 删除部门配置信息
     *
     * @param deptId 部门id
     * @param category 部门配置类别
     * @param orgBaseId 机构节点唯一标识(可能是机构id,也可能是部门id)
     */
    void removeDepartmentProp(String deptId, Integer category, String orgBaseId);

    /**
     * 保存或更新 新增或修改此部门实例的信息
     *
     * @param dept 部门对象
     * @return {@link Y9Department}
     */
    Y9Department saveOrUpdate(Y9Department dept);

    /**
     * 保存或者更新部门扩展信息
     *
     * @param id 部门唯一标识
     * @param properties 扩展属性
     * @return {@link Y9Department}
     */
    Y9Department saveProperties(String id, String properties);

    /**
     * 设置部门属性组织节点
     *
     * @param deptId 部门ID
     * @param category 部门属性类型
     * @param orgBaseIds 组织节点ID
     */
    void setDepartmentPropOrgUnits(String deptId, Integer category, List<String> orgBaseIds);

    /**
     * 更新部门排列序号
     *
     * @param id 部门唯一标识
     * @param tabIndex 排列序号
     * @return {@link Y9Department}
     */
    Y9Department updateTabIndex(String id, int tabIndex);

    /**
     * 获取可继承的部门属性对应组织节点列表
     *
     * @param deptId 部门唯一标识
     * @param category 部门属性类别
     * @param disabled 是否包含禁用
     * @return
     */
    List<Y9OrgBase> listInheritableDepartmentPropOrgUnits(String deptId, Integer category, Boolean disabled);

    /**
     * 分页获取机构下的部门列表
     *
     * @param orgIdList 组织节点 id 列表，不为空则查询指定组织节点下的部门
     * @param pageQuery 分页查询
     * @return {@code Page<Y9Department>}
     */
    Page<Y9Department> page(List<String> orgIdList, Y9PageQuery pageQuery);
}
