package net.risesoft.service.permission.cache;

import java.util.List;

import org.springframework.data.domain.Page;

import net.risesoft.entity.permission.cache.Y9PersonalApp;
import net.risesoft.pojo.AppCategory;
import net.risesoft.pojo.Y9PageQuery;

/**
 * 个人应用管理
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9PersonalAppService {

    /**
     * 根据组织节点id更新个人应用
     *
     * @param orgUnitId 组织节点id
     */
    void buildPersonalAppByOrgUnitId(String orgUnitId);

    /**
     * 根据部门ID，更新该部门下全部人员的图标
     *
     * @param deptId 部门id
     */
    void buildDeptPersonalAppForPerson(String deptId);

    /**
     * 更新部门下全部岗位的图标
     *
     * @param deptId 部门id
     */
    void buildDeptPersonalAppForPosition(String deptId);

    /**
     * 更新个人图标
     *
     * @param personId 人员id
     */
    void buildPersonalAppForPerson(String personId);

    /**
     * 更新个人图标
     *
     * @param personId 人员id
     * @param appCategoryList 应用排序列表
     */

    void buildPersonalAppForPerson(String personId, List<AppCategory> appCategoryList);

    /**
     * 更新岗位图标
     *
     * @param positionId 岗位id
     */
    void buildPersonalAppForPosition(String positionId);

    /**
     * 同步更新岗位id
     *
     * @param positionId 岗位id
     * @param appCategoryList 应用排序列表
     */
    void buildPersonalAppForPosition(String positionId, List<AppCategory> appCategoryList);

    /**
     * 同步图标，（租用的系统的所有未禁用的应用）
     *
     * @param personId 人员id
     */
    void buildPersonalAppWithoutPerm(String personId);

    /**
     * 根据人员ID获取图标列表
     *
     * @param orgUnitId 人员id
     * @return
     */
    List<Y9PersonalApp> listByOrgUnitId(String orgUnitId);

    /**
     * 根据人员id获取图标分页列表
     *
     * @param orgUnitId 人员id（或岗位id）
     * @param categoryId 分类 id
     * @param pageQuery 分页查询参数
     * @return
     */
    Page<Y9PersonalApp> pageByOrgUnitId(String orgUnitId, String categoryId, Y9PageQuery pageQuery);

    /**
     * 获取租用的人员id分页列表
     *
     * @param appId 应用id
     * @param deptName
     * @param pageQuery
     * @return
     */
    Page<Y9PersonalApp> pageOrgUnitIdByAppId(String appId, String deptName, Y9PageQuery pageQuery);

    /**
     * 保存个人图标信息
     *
     * @param personIconItem 个人图标对象
     */
    void save(Y9PersonalApp personIconItem);

    /**
     * 批量设置常用应用
     *
     * @param orgUnitId 人员id
     * @param appIds 应用id数组
     */
    void starApps(String orgUnitId, String[] appIds);

    /**
     * 对人员/岗位的应用进行排序
     *
     * @param orgUnitId 组织节点id
     * @param appIdList 应用id列表
     */
    void sort(String orgUnitId, List<String> appIdList);
}
