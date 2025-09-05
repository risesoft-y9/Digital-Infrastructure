package net.risesoft.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.log.LogLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.log.domain.Y9LogMappingDO;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9public.service.Y9logMappingService;

/**
 * 字段映射管理
 *
 * @author mengjuhua
 * @author guoweijun
 * @author shidaobang
 *
 */
@RestController
@RequestMapping(value = "/admin/mapping")
@RequiredArgsConstructor
public class FieldMappingController {

    private final Y9logMappingService y9logMappingService;

    /**
     * 批量移除字段映射
     *
     * @param ids id数组
     * @return {@code Y9Result<String>}
     */
    @RiseLog(moduleName = "日志系统", operationType = OperationTypeEnum.DELETE, operationName = "批量移除字段映射",
        logLevel = LogLevelEnum.RSLOG)
    @PostMapping(value = "/deleteByIds")
    public Y9Result<String> deleteByIds(String ids) {
        String[] id = ids.split(",");
        for (int i = 0; i < id.length; i++) {
            y9logMappingService.deleteFieldMapping(id[i]);
        }
        return Y9Result.success("删除成功");
    }

    /**
     * 移除字段映射
     *
     * @param id 主键
     * @return {@code Y9Result<String>}
     */
    @RiseLog(moduleName = "日志系统", operationType = OperationTypeEnum.DELETE, operationName = "移除字段映射",
        logLevel = LogLevelEnum.RSLOG)
    @PostMapping(value = "/deleteFieldMapping")
    public Y9Result<String> deleteFieldMapping(@RequestParam String id) {
        y9logMappingService.deleteFieldMapping(id);
        return Y9Result.success("", "删除成功");
    }

    /**
     * 根据id，获取字段映射
     *
     * @param id 字段映射id
     * @return {@code Y9Result<Y9logMapping>}
     */
    @RiseLog(moduleName = "日志系统", operationName = "根据id，获取字段映射", logLevel = LogLevelEnum.RSLOG)
    @GetMapping(value = "/getFieldMappingById")
    public Y9Result<Y9LogMappingDO> getFieldMappingById(String id) {
        Y9LogMappingDO y9LogMappingDO = y9logMappingService.getFieldMappingEntity(id);
        return Y9Result.success(y9LogMappingDO);
    }

    /**
     * 获取字段映射分页列表
     *
     * @param pageQuery 分页信息
     * @param sort 排序字段
     * @return {@code Y9Page<Y9logMapping>}
     */
    @RiseLog(moduleName = "日志系统", operationName = "获取字段映射分页列表", logLevel = LogLevelEnum.RSLOG)
    @GetMapping(value = "/page")
    public Y9Page<Y9LogMappingDO> page(Y9PageQuery pageQuery, String sort) {
        Page<Y9LogMappingDO> resultPage = y9logMappingService.page(pageQuery.getPage(), pageQuery.getSize(), sort);
        return Y9Page.success(pageQuery.getPage(), resultPage.getTotalPages(), resultPage.getTotalElements(),
            resultPage.getContent());
    }

    /**
     * 搜索字段映射分页列表
     *
     * @param modularName 模块名称
     * @param modularCnName 模块中文名称
     * @param pageQuery 分页信息
     * @return {@code Y9Page<Y9logMapping>}
     */
    @RiseLog(moduleName = "日志系统", operationName = "搜索字段映射分页列表", logLevel = LogLevelEnum.RSLOG)
    @GetMapping(value = "/pageSearchList")
    public Y9Page<Y9LogMappingDO> pageSearchList(String modularName, String modularCnName, Y9PageQuery pageQuery) {
        Page<Y9LogMappingDO> resultPage =
            y9logMappingService.pageSearchList(pageQuery.getPage(), pageQuery.getSize(), modularName, modularCnName);
        return Y9Page.success(pageQuery.getPage(), resultPage.getTotalPages(), resultPage.getTotalElements(),
            resultPage.getContent());
    }

    /**
     * 保存字段映射信息
     *
     * @param modularNameMapping 字段映射信息
     * @return {@code Y9Result<String>}
     */
    @RiseLog(moduleName = "日志系统", operationType = OperationTypeEnum.ADD, operationName = "保存字段映射信息",
        logLevel = LogLevelEnum.RSLOG)
    @PostMapping(value = "/saveMapping")
    public Y9Result<String> saveMapping(Y9LogMappingDO modularNameMapping) {
        if (StringUtils.isBlank(modularNameMapping.getId())) {
            modularNameMapping.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }
        y9logMappingService.save(modularNameMapping);
        return Y9Result.success("", "保存成功");
    }

    /**
     * 验证字段映射是否存在
     *
     * @param name 字段映射
     * @return {@code Y9Result<Boolean>}
     */
    @RiseLog(moduleName = "日志系统", operationName = "验证字段映射是否存在", logLevel = LogLevelEnum.RSLOG)
    @PostMapping(value = "/validate")
    public Y9Result<Boolean> validateField(String name) {
        List<Y9LogMappingDO> list = y9logMappingService.validateName(name);
        if (list.isEmpty()) {
            return Y9Result.success(true);
        }
        return Y9Result.success(false);
    }
}
