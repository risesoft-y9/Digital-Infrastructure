package net.risesoft.service.org;

import java.util.List;

import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9OrgBase;
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
     * @param deptId 部门id
     * @return {@link Y9Department}
     */
    Y9Department changeDisable(String deptId);

    /**
     * 根据主键id删除部门实例
     *
     * @param deptId 部门id
     */
    void delete(String deptId);

    /**
     * 根据id判断部门是否存在
     *
     * @param id 部门id
     * @return boolean
     */
    boolean existsById(String id);

    /**
     * 根据id查找部门
     *
     * @param id 部门id
     * @return 部门对象 或 null
     */
    Y9Department findById(String id);

    /**
     * 根据主键id获取部门实例
     *
     * @param id 部门id
     * @return OrgDepartment
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Department getById(String id);

    /**
     * 根据部门id获取部门秘书
     *
     * @param deptId 部门id
     * @return {@link List}<{@link Y9OrgBase}>
     */
    List<Y9OrgBase> getDeptSecretarys(String deptId);

    /**
     * 获得部门树
     *
     * @param orgBaseId ：机构节点唯一标识(可能是机构id,也可能是部门id)
     * @return {@link List}<{@link Y9Department}>
     */
    List<Y9Department> getDeptTrees(String orgBaseId);

    /**
     * 获取子节点最大的TableIndex
     *
     * @return {@link Integer}
     */
    Integer getMaxTabIndex();

    /**
     * 查询部门
     *
     * @return {@link List}<{@link Y9Department}>
     */
    List<Y9Department> list();

    /**
     * 获取组织机构下所有的委办局
     *
     * @param organizationId 组织机构id
     * @return {@link List}<{@link Y9Department}>
     */
    List<Y9Department> listBureau(String organizationId);

    /**
     * 根据dn查询部门列表
     *
     * @param dn dn
     * @return {@link List}<{@link Y9Department}>
     */
    List<Y9Department> listByDn(String dn);

    /**
     * 根据名称查询
     *
     * @param name 部门名
     * @return {@link List}<{@link Y9Department}>
     */
    List<Y9Department> listByNameLike(String name);

    /**
     * 根据父节点id,获取本层级的部门列表
     *
     * @param parentId 父节点id
     * @return {@link List}<{@link Y9Department}>
     */
    List<Y9Department> listByParentId(String parentId);

    /**
     * 根据部门id获取部门领导
     *
     * @param deptId 部门id
     * @return {@link List}<{@link Y9OrgBase}>
     */
    List<Y9OrgBase> listLeaders(String deptId);

    /**
     * 根据部门Id获取部门的主管领导
     *
     * @param deptId 部门id
     * @return {@link List}<{@link Y9OrgBase}>
     */
    List<Y9OrgBase> listManagers(String deptId);

    /**
     * 根据部门id获取部门副领导
     *
     * @param deptId 部门id
     * @return {@link List}<{@link Y9OrgBase}>
     */
    List<Y9OrgBase> listViceLeaders(String deptId);

    /**
     * 移动部门到新的节点
     *
     * @param deptId 部门id
     * @param parentId 新父节点id
     * @return {@link Y9Department}
     */
    Y9Department move(String deptId, String parentId);

    /**
     * 移除部门领导
     *
     * @param deptId 部门id
     * @param orgBaseId 领导id
     */
    void removeLeader(String deptId, String orgBaseId);

    /**
     * 移除主管领导
     *
     * @param deptId 部门id
     * @param orgBaseId 领导id
     */
    void removeManager(String deptId, String orgBaseId);

    /**
     * 移除秘书
     *
     * @param deptId 部门id
     * @param personId 秘书id
     */
    void removeSecretary(String deptId, String personId);

    /**
     * 移除副领导
     *
     * @param deptId 部门id
     * @param personId 副领导id
     */
    void removeViceLeader(String deptId, String personId);

    /**
     * 保存新的部门排序
     *
     * @param deptIds 部门id数组
     * @return {@link List}<{@link Y9Department}>
     */
    List<Y9Department> saveOrder(String[] deptIds);

    /**
     * 保存或更新 新增或修改此部门实例的信息
     *
     * @param dept 部门对象
     * @param parent 父节点对象
     * @return {@link Y9Department}
     */
    Y9Department saveOrUpdate(Y9Department dept, Y9OrgBase parent);

    /**
     * 保存或者更新部门扩展信息
     *
     * @param id 部门唯一标识
     * @param properties 扩展属性
     * @return {@link Y9Department}
     */
    Y9Department saveProperties(String id, String properties);

    /**
     * 根据whereClause搜索
     *
     * @param whereClause 查询子句
     * @return {@link List}<{@link Y9Department}>
     */
    List<Y9Department> search(String whereClause);

    /**
     * 设置部门主管领导
     *
     * @param deptId 部门id
     * @param personIds 人员id数组
     */
    void setDeptLeaders(String deptId, String[] personIds);

    /**
     * 设置部门主管领导
     *
     * @param deptId 部门id
     * @param orgBaseIds 组织节点id数组
     */
    void setDeptManagers(String deptId, String[] orgBaseIds);

    /**
     * 设置部门秘书
     *
     * @param deptId 部门id
     * @param orgBaseIds 组织节点id数组
     */
    void setDeptSecretarys(String deptId, String[] orgBaseIds);

    /**
     * 设置部门副领导
     *
     * @param deptId 部门id
     * @param orgBaseIds 组织节点id数组
     */
    void setDeptViceLeaders(String deptId, String[] orgBaseIds);

    /**
     * 更新部门排列序号
     *
     * @param id 部门唯一标识
     * @param tabIndex 排列序号
     * @return {@link Y9Department}
     */
    Y9Department updateTabIndex(String id, int tabIndex);

    List<Y9Department> list(List<String> ids);
}
