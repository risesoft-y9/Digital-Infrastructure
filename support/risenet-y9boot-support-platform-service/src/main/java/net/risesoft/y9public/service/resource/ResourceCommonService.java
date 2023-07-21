package net.risesoft.y9public.service.resource;

import java.util.List;

import net.risesoft.y9.exception.Y9NotFoundException;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface ResourceCommonService<T extends Y9ResourceBase> {

    /**
     * 批量删除资源
     *
     * @param idList
     */
    void delete(List<String> idList);

    /**
     * 删除资源
     *
     * @param id
     */
    void delete(String id);

    /**
     * 批量禁用资源
     *
     * @param idList
     * @return
     */
    List<T> disable(List<String> idList);

    /**
     * 禁用资源
     *
     * @param id
     * @return
     */
    T disable(String id);

    /**
     * 批量启用资源
     *
     * @param idList
     * @return
     */
    List<T> enable(List<String> idList);

    /**
     * 启用资源
     *
     * @param id
     * @return
     */
    T enable(String id);

    /**
     * 根据id判断资源是否存在
     *
     * @param id
     * @return 对应的资源是否存在
     */
    boolean existsById(String id);

    /**
     * 根据id获取资源
     *
     * @param id
     * @return 资源对象
     */
    T findById(String id);

    /**
     * 根据资源名称查找资源列表
     *
     * @param name
     * @return
     */
    List<T> findByNameLike(String name);

    /**
     * 根据父资源id获取其下子资源
     *
     * @param parentId
     * @return
     */
    List<T> findByParentId(String parentId);

    /**
     * 根据id获取资源
     *
     * @param id
     * @return 资源对象
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    T getById(String id);

    /**
     * 新增或保存资源
     *
     * @param resourceBase
     * @return
     */
    T saveOrUpdate(T resourceBase);

    /**
     * 更新排列序号
     *
     * @param id
     * @param index
     */
    T updateTabIndex(String id, int index);
}
