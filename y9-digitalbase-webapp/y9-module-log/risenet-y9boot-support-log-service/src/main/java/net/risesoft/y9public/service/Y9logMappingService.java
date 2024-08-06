package net.risesoft.y9public.service;

import java.util.List;

import org.springframework.data.domain.Page;

import net.risesoft.y9public.entity.Y9logMapping;

/**
 * 模块名称映射
 * 
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
public interface Y9logMappingService {

    void deleteFieldMapping(String id);

    /**
     * 根据模块名称，获取对应的中文映射
     * 
     * @param modularName 模块名称
     * @return String
     */
    String getCnModularName(String modularName);

    Y9logMapping getFieldMappingEntity(String id);

    /**
     * 分页获取模块名称映射
     *
     * @param page 页数
     * @param rows 条数
     * @param sort 排序方式
     * @return {@code Page<Y9logMapping>}
     */
    Page<Y9logMapping> page(int page, int rows, String sort);

    /**
     * 搜索模块名称映射
     * 
     * @param page 页数
     * @param rows 条数
     * @param modularName 模块名称
     * @param modularCnName 中文映射
     * @return {@code Page<Y9logMapping>}
     */
    Page<Y9logMapping> pageSearchList(Integer page, Integer rows, String modularName, String modularCnName);

    /**
     * 保存模块名称映射
     * 
     * @param y9logMapping 模块名称映射详情
     */
    void save(Y9logMapping y9logMapping);

    /**
     * 验证字段映射是否存在
     * 
     * @param name 模块名称
     * @return {@code List<Y9logMapping>}
     */
    List<Y9logMapping> validateName(String name);
}
