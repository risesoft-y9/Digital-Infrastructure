package net.risesoft.y9public.service.resource;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.y9.exception.Y9NotFoundException;
import net.risesoft.y9public.entity.resource.Y9System;

/**
 * SystemService
 *
 * @author shidaobang
 * @date 2022/3/2
 */
public interface Y9SystemService {

    /**
     * 根据id删除
     *
     * @param id 唯一标识
     */
    void delete(String id);

    /**
     * 根据id禁用
     *
     * @param id 唯一标识
     * @return {@link Y9System}
     */
    Y9System disable(String id);

    /**
     * 根据id启用
     *
     * @param id 唯一标识
     * @return {@link Y9System}
     */
    Y9System enable(String id);

    /**
     * 根据id获取系统对象
     *
     * @param id 唯一标识
     * @return 系统对象 或 null
     */
    Optional<Y9System> findById(String id);

    /**
     * 根据系统名称获取系统实体
     *
     * @param name 系统名
     * @return {@link Y9System}
     */
    Optional<Y9System> findByName(String name);

    /**
     * 根据id查询System实体
     *
     * @param id 唯一标识
     * @return 系统对象
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9System getById(String id);

    /**
     * 根据名字查询System实体
     *
     * @param systemName 系统名称
     * @return {@link Y9System}
     * @throws Y9NotFoundException systemName 对应的记录不存在的情况
     */
    Y9System getByName(String systemName);

    /**
     * 检查系统名称可用性
     *
     * @param id id
     * @param name 系统名
     * @return boolean
     */
    boolean isNameAvailable(String id, String name);

    /**
     * 查询所有Y9System
     *
     * @return List<Y9System>
     */
    List<Y9System> listAll();

    /**
     * 获取系统id列表
     *
     * @param autoInit 是否自动租用系统
     * @return {@link List}<{@link String}>
     */
    List<String> listByAutoInit(Boolean autoInit);

    /**
     * 根据系统中文名称，模糊搜索系统列表
     *
     * @param cnName 系统中文名称
     * @return {@link List}<{@link Y9System}>
     */
    List<Y9System> listByCnNameContaining(String cnName);

    /**
     * 根据contextPath获取系统实体
     *
     * @param contextPath 上下文路径
     * @return {@link List}<{@link Y9System}>
     */
    List<Y9System> listByContextPath(String contextPath);

    /**
     * 分页查询系统列表
     *
     * @param pageQuery 分页查询参数
     * @return {@link Page}<{@link Y9System}>
     */
    Page<Y9System> page(Y9PageQuery pageQuery);

    Y9System saveAndRegister4Tenant(Y9System y9System);

    /**
     * 保存系统
     *
     * @param y9System 系统对象
     * @return {@link Y9System}
     */
    Y9System saveOrUpdate(Y9System y9System);

    /**
     * 保存系统排序
     *
     * @param systemIds 系统id数组
     */
    void saveOrder(String[] systemIds);
}
