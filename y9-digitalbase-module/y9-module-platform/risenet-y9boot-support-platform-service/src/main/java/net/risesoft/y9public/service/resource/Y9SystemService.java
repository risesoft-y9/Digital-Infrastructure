package net.risesoft.y9public.service.resource;

import java.util.List;
import java.util.Optional;

import net.risesoft.model.platform.System;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.y9.exception.Y9BusinessException;
import net.risesoft.y9.exception.Y9NotFoundException;

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
     * 检查系统关联关系后根据 id 删除
     *
     * @param id 唯一标识
     * @throws Y9BusinessException 系统存在关联应用或已被租户租用的情况
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    void deleteAfterCheck(String id);

    /**
     * 根据当前登录管理员删除系统
     *
     * @param id 唯一标识
     */
    void deleteForManager(String id);

    /**
     * 根据id禁用
     *
     * @param id 唯一标识
     * @return {@link System}
     */
    System disable(String id);

    /**
     * 根据id启用
     *
     * @param id 唯一标识
     * @return {@link System}
     */
    System enable(String id);

    /**
     * 根据id获取系统对象
     *
     * @param id 唯一标识
     * @return {@code Optional<System>}系统对象 或 null
     */
    Optional<System> findById(String id);

    /**
     * 根据系统名称获取系统实体
     *
     * @param name 系统名
     * @return {@code Optional<System>}
     */
    Optional<System> findByName(String name);

    /**
     * 根据id查询System实体
     *
     * @param id 唯一标识
     * @return 系统对象
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    System getById(String id);

    /**
     * 根据名字查询System实体
     *
     * @param systemName 系统名称
     * @return {@link System}
     * @throws Y9NotFoundException systemName 对应的记录不存在的情况
     */
    System getByName(String systemName);

    /**
     * 查询所有系统
     *
     * @return {@code List<System>}
     */
    List<System> listAll();

    /**
     * 根据当前登录管理员查询系统列表
     *
     * @return {@code List<System>}
     */
    List<System> listByManager();

    /**
     * 根据 id 列表查询系统
     *
     * @param ids 系统 id 列表
     * @return {@code List<System>}
     */
    List<System> listByIds(List<String> ids);

    /**
     * 分页查询系统列表
     *
     * @param pageQuery 分页查询参数
     * @return {@code Page<System>}
     */
    Y9Page<System> page(Y9PageQuery pageQuery);

    /**
     * 保存系统并为租户租用
     *
     * @param y9System 系统对象
     * @return {@link System}
     * @throws Y9BusinessException 系统名称已被使用的情况
     */
    System saveAndRegister4Tenant(System y9System);

    /**
     * 保存系统
     *
     * @param y9System 系统对象
     * @return {@link System}
     */
    System saveOrUpdate(System y9System);

    /**
     * 保存系统排序
     *
     * @param systemIds 系统id数组
     */
    void saveOrder(String[] systemIds);
}
