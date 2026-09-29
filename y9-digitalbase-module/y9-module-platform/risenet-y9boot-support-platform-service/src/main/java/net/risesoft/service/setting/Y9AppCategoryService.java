package net.risesoft.service.setting;

import java.util.List;

import org.springframework.data.domain.Page;

import net.risesoft.pojo.AppCategory;
import net.risesoft.pojo.Y9PageQuery;

/**
 * 图标排序
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9AppCategoryService {

    /**
     * 批量删除
     *
     * @param ids id数组
     */
    void delete(String[] ids);

    /**
     * 返回默认已排序的应用分类列表 首先排已分类的应用，然后排剩下未分类的应用
     *
     * @return
     */
    List<AppCategory> buildDefaultAppCategoryList();

    /**
     * 根据资源id，获取图标排序列表
     *
     * @param categoryId 资源id
     * @return
     */
    List<AppCategory> listByCategoryId(String categoryId);

    /**
     * 根据资源id，获取图标排序分页列表
     *
     * @param categoryId 分类 id
     * @param pageQuery
     * @return
     */
    Page<AppCategory> pageByCategoryId(String categoryId, Y9PageQuery pageQuery);

    /**
     * 保存排序
     *
     * @param ids id数组
     */
    void saveOrder(String[] ids);

    /**
     * 保存应用排序
     *
     * @param appIds 应用id数组
     * @param categoryId 资源id
     */
    void saveOrUpdate(String[] appIds, String categoryId);

}
