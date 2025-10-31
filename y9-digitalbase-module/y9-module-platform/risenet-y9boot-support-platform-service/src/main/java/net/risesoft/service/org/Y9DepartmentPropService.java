package net.risesoft.service.org;

import java.util.List;
import java.util.Optional;

import net.risesoft.entity.org.Y9DepartmentProp;
import net.risesoft.enums.platform.org.DepartmentPropCategoryEnum;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9DepartmentPropService {

    /**
     * 根据部门唯一标识删除
     *
     * @param id 唯一标识
     */
    void deleteById(String id);

    /**
     * 删除部门信息配置
     *
     * @param deptId 部门唯一标识
     * @param {@link DepartmentPropCategoryEnum} category 类别
     * @param orgBaseId 组织唯一标示
     */
    void deleteByDeptIdAndCategoryAndOrgBaseId(String deptId, DepartmentPropCategoryEnum category, String orgBaseId);

    /**
     * 根据部门唯一标识查找
     *
     * @param id 唯一标识
     * @return {@code Optional<Y9DepartmentProp>} 部门配置对象 或 null
     */
    Optional<Y9DepartmentProp> findById(String id);

    /**
     * 查找所有的部门管理员
     *
     * @return {@code List<Y9DepartmentProp>}
     */
    List<Y9DepartmentProp> listAll();

    /**
     * 根据部门唯一标识和类别查找部门配置信息
     *
     * @param {@link DepartmentPropCategoryEnum} category 类别
     * @return {@code List<Y9DepartmentProp>}
     */
    List<Y9DepartmentProp> listByCategory(DepartmentPropCategoryEnum category);

    /**
     * 根据部门唯一标识查找部门配置信息
     *
     * @param deptId 部门id
     * @return {@code List<Y9DepartmentProp>}
     */
    List<Y9DepartmentProp> listByDeptId(String deptId);

    /**
     * 根据部门唯一标识和类别查找部门配置信息
     *
     * @param deptId 部门id
     * @param {@link DepartmentPropCategoryEnum} category 类别
     * @return {@code List<Y9DepartmentProp>}
     */
    List<Y9DepartmentProp> listByDeptIdAndCategory(String deptId, DepartmentPropCategoryEnum category);

    /**
     * 根据人员唯一标识查找管理的部门
     *
     * @param orgBaseId 组织节点id
     * @param {@link DepartmentPropCategoryEnum} category 类别
     * @return {@code List<Y9DepartmentProp>}
     */
    List<Y9DepartmentProp> listByOrgBaseIdAndCategory(String orgBaseId, DepartmentPropCategoryEnum category);

    /**
     * 保存或者更新
     *
     * @param y9DepartmentProp 部门属性对象
     */
    void saveOrUpdate(Y9DepartmentProp y9DepartmentProp);

}
