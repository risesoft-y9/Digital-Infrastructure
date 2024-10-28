package net.risesoft.controller.resource;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.controller.resource.vo.DataCatalogTreeNodeVO;
import net.risesoft.entity.Y9OptionValue;
import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.model.platform.DataCatalog;
import net.risesoft.permission.annotation.IsAnyManager;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9public.entity.resource.Y9DataCatalog;
import net.risesoft.y9public.service.resource.Y9DataCatalogService;

/**
 * 数据目录控制器
 *
 * @author shidaobang
 * @date 2024/10/09
 * @since 9.6.8
 */
@Validated
@RestController
@RequestMapping(value = "/api/rest/dataCatalog", produces = MediaType.APPLICATION_JSON_VALUE)
@IsAnyManager({ManagerLevelEnum.SYSTEM_MANAGER, ManagerLevelEnum.OPERATION_SYSTEM_MANAGER,
    ManagerLevelEnum.SECURITY_MANAGER})
@RequiredArgsConstructor
public class DataCatalogController {

    private final Y9DataCatalogService y9DataCatalogService;

    @RiseLog(operationName = "或许数据目录树类型")
    @GetMapping(value = "/treeTypeList")
    public Y9Result<List<Y9OptionValue>> getTreeTypeList() {
        List<Y9OptionValue> treeTypeList = y9DataCatalogService.getTreeTypeList();
        return Y9Result.success(treeTypeList);
    }

    @RiseLog(operationName = "根据父节点id分层获取数据目录树")
    @GetMapping(value = "/tree")
    public Y9Result<List<DataCatalogTreeNodeVO>> getTree(@RequestParam(required = false) String parentId,
        @NotBlank String treeType) {
        List<DataCatalog> dataCatalogList = y9DataCatalogService.getTree(parentId, treeType);
        return Y9Result.success(DataCatalogTreeNodeVO.convertDataCatalogList(dataCatalogList));
    }

    @RiseLog(operationName = "搜索数据目录树")
    @GetMapping(value = "/treeSearch")
    public Y9Result<List<DataCatalogTreeNodeVO>> treeSearch(@NotBlank String name, @NotBlank String treeType) {
        List<DataCatalog> dataCatalogList = y9DataCatalogService.treeSearch(name, treeType);
        return Y9Result.success(DataCatalogTreeNodeVO.convertDataCatalogList(dataCatalogList));
    }

    @RiseLog(operationName = "根据id获取数据目录详情")
    @GetMapping(value = "/{id}")
    public Y9Result<DataCatalog> getById(@PathVariable @NotBlank String id) {
        return Y9Result.success(y9DataCatalogService.getById(id), "成功获取数据目录详情");
    }

    @RiseLog(operationName = "保存数据目录")
    @PostMapping(value = "/save")
    public Y9Result<Y9DataCatalog> save(Y9DataCatalog y9DataCatalog) {
        Y9DataCatalog savedDataCatalog = y9DataCatalogService.saveOrUpdate(y9DataCatalog);
        return Y9Result.success(savedDataCatalog, "成功保存数据目录");
    }

    @RiseLog(operationName = "按年保存数据目录")
    @PostMapping(value = "/saveByYears")
    public Y9Result<Y9DataCatalog> saveByYears(Y9DataCatalog y9DataCatalog, Integer startYear, Integer endYear) {
        y9DataCatalogService.saveByYears(y9DataCatalog, startYear, endYear);
        return Y9Result.successMsg("成功保存数据目录");
    }

    @RiseLog(operationName = "按类型保存数据目录")
    @PostMapping(value = "/saveByType")
    public Y9Result<Y9DataCatalog> saveByType(Y9DataCatalog y9DataCatalog) {
        y9DataCatalogService.saveByType(y9DataCatalog);
        return Y9Result.successMsg("成功保存数据目录");
    }

    @RiseLog(operationName = "删除数据目录")
    @PostMapping(value = "/delete")
    public Y9Result<Object> delete(@RequestParam @NotBlank String id) {
        y9DataCatalogService.delete(id);
        return Y9Result.successMsg("删除数据目录成功");
    }
}
