package net.risesoft.y9public.service.resource;

import java.util.List;
import java.util.Optional;

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
     * @param idList 资源id列表
     */
    void delete(List<String> idList);

    /**
     * 删除资源
     *
     * @param id 资源id
     */
    void delete(String id);

    /**
     * 批量禁用资源
     *
     * @param idList 资源id列表
     * @return {@code List<T>}
     */
    List<T> disable(List<String> idList);

    /**
     * 禁用资源
     *
     * @param id 资源id
     * @return {@code T}
     */
    T disable(String id);

    /**
     * 批量启用资源
     *
     * @param idList 资源id列表
     * @return {@code List<T>}
     */
    List<T> enable(List<String> idList);

    /**
     * 启用资源
     *
     * @param id 资源id
     * @return {@code T}
     */
    T enable(String id);

    /**
     * 根据id判断资源是否存在
     *
     * @param id 资源id
     * @return 对应的资源是否存在
     */
    boolean existsById(String id);

    /**
     * 根据id获取资源
     *
     * @param id 资源id
     * @return {@code Optional<T>}资源对象
     */
    Optional<T> findById(String id);

    /**
     * 根据资源名称查找资源列表
     *
     * @param name 名称
     * @return {@code List<T>}
     */
    List<T> findByNameLike(String name);

    /**
     * 根据id获取资源
     *
     * @param id 资源id
     * @return 资源对象
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    T getById(String id);

    /**
     * 新增或保存资源
     *
     * @param resourceBase 资源信息
     * @return {@code  T}
     */
    T saveOrUpdate(T resourceBase);

    /**
     * 更新排列序号
     *
     * @param id 资源id
     * @param index 序列号
     * @return {@code  T}
     */
    T updateTabIndex(String id, int index);
}
