package net.risesoft.y9public.service.resource;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

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
    Y9System findById(String id);

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
     * 根据开发商id查找系统列表
     *
     * @param isvGuid 开发商id
     * @return {@link List}<{@link Y9System}>
     */
    List<Y9System> listByIsvGuid(String isvGuid);

    /**
     * 根据Id集合获取系统名称,riseplatform除外
     *
     * @param ids 唯一标识数组
     * @return {@link List}<{@link String}>
     */
    List<String> listSystemNameByIds(List<String> ids);

    /**
     * 根据开发商id及系统名分页查询系统
     *
     * @param page 页数
     * @param rows 每页行数
     * @param isvGuid 开发商id
     * @param name 系统名
     * @return {@link Page}<{@link Y9System}>
     */
    Page<Y9System> page(int page, int rows, String isvGuid, String name);

    /**
     * Y9System分页列表
     *
     * @param page 页数
     * @param rows 每页行数
     * @return {@link Page}<{@link Y9System}>
     */
    Page<Y9System> page(int page, int rows);

    /**
     * 保存系统排序
     *
     * @param systemIds 系统id数组
     */
    void saveOrder(String[] systemIds);

    /**
     * 保存系统
     *
     * @param y9System 系统对象
     * @return {@link Y9System}
     */
    Y9System saveOrUpdate(Y9System y9System);

    /**
     * 检查系统名称可用性
     *
     * @param id id
     * @param name 系统名
     * @return boolean
     */
    boolean checkNameAvailability(String id, String name);
}
