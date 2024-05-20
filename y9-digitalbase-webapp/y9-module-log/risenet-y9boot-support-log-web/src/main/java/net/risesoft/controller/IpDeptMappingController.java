package net.risesoft.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.log.LogLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9public.entity.Y9logIpDeptMapping;
import net.risesoft.y9public.service.Y9logIpDeptMappingService;

/**
 * 部门网段管理
 *
 * @author mengjuhua
 * @author guoweijun
 * @author shidaobang
 *
 */
@RestController
@RequestMapping(value = "/admin/ipDeptMapping")
@RequiredArgsConstructor
public class IpDeptMappingController {

    private final Y9logIpDeptMappingService ipDeptMappingService;

    /**
     * 查询部门网段分页列表
     *
     * @param pageQuery 分页详情
     * @param clientIpSection IP的ABC段
     * @param deptName 部门名称
     * @return
     */
    @RiseLog(moduleName = "日志系统", operationName = "查询部门网段分页列表", logLevel = LogLevelEnum.RSLOG)
    @GetMapping(value = "/pageSearchList")
    public Y9Page<Y9logIpDeptMapping> pageSearchList(Y9PageQuery pageQuery, String clientIpSection, String deptName) {
        return ipDeptMappingService.pageSearchList(pageQuery.getPage(), pageQuery.getSize(), clientIpSection, deptName);
    }

    /**
     * 批量移除部门网段信息
     *
     * @param ipDeptMappingIds 部门网段id数组
     * @return
     */
    @RiseLog(moduleName = "日志系统", operationType = OperationTypeEnum.DELETE, operationName = "移除部门网段信息",
        logLevel = LogLevelEnum.RSLOG)
    @PostMapping(value = "/remove")
    public Y9Result<String> remove(String[] ipDeptMappingIds) {
        ipDeptMappingService.removeOrganWords(ipDeptMappingIds);
        return Y9Result.success("", "移除成功");
    }

    /**
     * 部门网段排序
     *
     * @param idAndTabIndexs 部门网段id数组
     * @return
     */
    @RiseLog(moduleName = "日志系统", operationType = OperationTypeEnum.MODIFY, operationName = "部门网段排序",
        logLevel = LogLevelEnum.RSLOG)
    @PostMapping(value = "/saveOrder")
    public Y9Result<String> saveOrder(String[] idAndTabIndexs) {
        ipDeptMappingService.update4Order(idAndTabIndexs);
        return Y9Result.success("", "保存成功");
    }

    /**
     * 保存部门网段信息
     *
     * @param ipDeptMapping 部门网段信息
     * @return
     */
    @RiseLog(moduleName = "日志系统", operationType = OperationTypeEnum.ADD, operationName = "保存部门网段信息",
        logLevel = LogLevelEnum.RSLOG)
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<Y9logIpDeptMapping> saveOrUpdate(Y9logIpDeptMapping ipDeptMapping) {
        Y9logIpDeptMapping y9logIpDeptMapping = ipDeptMappingService.saveOrUpdate(ipDeptMapping);
        return Y9Result.success(y9logIpDeptMapping, "保存成功");
    }
}
