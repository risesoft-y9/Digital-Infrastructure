package net.risesoft.controller.setting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.OptionClassConsts;
import net.risesoft.entity.permission.cache.Y9PersonalApp;
import net.risesoft.entity.setting.Y9AppCategory;
import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.model.platform.dictionary.OptionValue;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.resource.App;
import net.risesoft.model.platform.tenant.TenantApp;
import net.risesoft.permission.annotation.IsAnyManager;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.dictionary.Y9OptionValueService;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.permission.cache.Y9PersonalAppService;
import net.risesoft.service.setting.Y9AppCategoryService;
import net.risesoft.vo.AppCategoryVO;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.service.resource.Y9AppService;
import net.risesoft.y9public.service.tenant.Y9TenantAppService;

/**
 * 应用分类管理
 *
 * @author shidaobang
 *
 */
@RestController
@RequestMapping(value = "/api/rest/appCategory", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
@IsAnyManager(value = {ManagerLevelEnum.SYSTEM_MANAGER, ManagerLevelEnum.SECURITY_MANAGER})
public class AppCategoryController {

    private final Y9AppService y9AppService;

    private final Y9TenantAppService y9TenantAppService;

    private final Y9PersonalAppService y9PersonalAppService;

    private final CompositeOrgBaseService compositeOrgBaseService;

    private final Y9AppCategoryService y9AppCategoryService;

    private final Y9OptionValueService y9OptionValueService;

    /**
     * 获取应用分类列表
     *
     * @return {@code Y9Result<List<OptionValue>> }
     */
    @RiseLog(operationName = "获取应用分类列表")
    @GetMapping(value = "/categoryList")
    public Y9Result<List<OptionValue>> getCategoryList() {
        List<OptionValue> categoryList = y9OptionValueService.listByType(OptionClassConsts.APP_CATEGORY);
        return Y9Result.success(categoryList);
    }

    /**
     * 批量移除应用分类配置
     *
     * @param ids 应用分类配置的ids
     * @return
     */
    @RiseLog(operationName = "批量移除应用分类配置", operationType = OperationTypeEnum.DELETE)
    @PostMapping(value = "/delete")
    public Y9Result<Object> delete(@NotEmpty String[] ids) {
        y9AppCategoryService.delete(ids);
        return Y9Result.successMsg("批量移除应用分类配置成功！");
    }

    /**
     * 获取全部应用列表
     *
     * @return
     */
    @RiseLog(operationName = "获取全部应用列表")
    @RequestMapping(value = "/listAllAppsByTenantId")
    public Y9Result<List<Map<String, Object>>> listAllAppsByTenantId() {
        List<String> tenantAppIdList =
            y9TenantAppService.listAppIdByTenantIdAndTenancy(Y9LoginUserHolder.getTenantId(), Boolean.TRUE);
        List<Map<String, Object>> appList = new ArrayList<>();
        for (String appId : tenantAppIdList) {
            App app = y9AppService.findById(appId).orElse(null);
            if (null != app) {
                Boolean enable = app.getEnabled();
                if (Boolean.TRUE.equals(enable)) {
                    TenantApp y9TenantApp = y9TenantAppService
                        .findByTenantIdAndAppIdAndTenancy(Y9LoginUserHolder.getTenantId(), appId, Boolean.TRUE)
                        .orElse(null);
                    if (Boolean.TRUE.equals(y9TenantApp.getVerify())) {
                        Map<String, Object> returnMap = new HashMap<>(5);
                        returnMap.put("id", app.getId());
                        returnMap.put("name", app.getName());
                        returnMap.put("url", app.getUrl());
                        returnMap.put("title_icon", app.getIconData());
                        returnMap.put("hasChild", false);
                        appList.add(returnMap);
                    }
                }
            }
        }
        return Y9Result.success(appList, "获取全部应用列表成功!");
    }

    /**
     * 根据人员Id,获取个人的图标app列表
     *
     * @param personId
     * @return
     */
    @RiseLog(operationName = "根据人员Id,获取个人的图标app列表")
    @RequestMapping(value = "/listAppByPersonId2")
    public Y9Result<List<App>> listAppByPersonId2(@NotBlank String personId) {
        List<App> appList = new ArrayList<>();
        List<Y9PersonalApp> personIconList = y9PersonalAppService.listByOrgUnitId(personId);
        if (!personIconList.isEmpty()) {
            for (Y9PersonalApp y9PersonalApp : personIconList) {
                y9AppService.findById(y9PersonalApp.getAppId()).ifPresent(appList::add);
            }
        }
        return Y9Result.success(appList, "根据人员Id获取图标app列表成功!");
    }

    /**
     * 获取全部应用排序数据
     *
     * @param categoryId 分类id
     * @return
     */
    @RiseLog(operationName = "获取应用排序数据")
    @RequestMapping(value = "/listByCategoryId")
    public Y9Result<List<AppCategoryVO>> listByCategoryId(@RequestParam @NotBlank String categoryId) {
        List<Y9AppCategory> appCategoryMappingList = y9AppCategoryService.listByCategoryId(categoryId);
        List<AppCategoryVO> appCategoryVOList = toVo(appCategoryMappingList);
        return Y9Result.success(appCategoryVOList, "获取应用排序数据成功！");
    }

    private List<AppCategoryVO> toVo(List<Y9AppCategory> appCategoryMappingList) {
        List<AppCategoryVO> appCategoryVOList = new ArrayList<>();
        for (Y9AppCategory appCategoryMapping : appCategoryMappingList) {
            App app = y9AppService.getById(appCategoryMapping.getAppId());

            AppCategoryVO appCategoryVO = new AppCategoryVO();
            appCategoryVO.setId(appCategoryMapping.getId());
            appCategoryVO.setCategoryId(appCategoryMapping.getCategoryId());
            appCategoryVO.setTabIndex(appCategoryMapping.getTabIndex());
            appCategoryVO.setAppId(app.getAppId());
            appCategoryVO.setAppName(app.getName());
            appCategoryVO.setAppUrl(app.getUrl());
            appCategoryVOList.add(appCategoryVO);
        }
        return appCategoryVOList;
    }

    /**
     * 根据人员名称或者部门名称进行模糊查询
     *
     * @param pageQuery 分页信息
     * @param appId 应用id
     * @param deptName 部门名称
     * @return
     */
    @RiseLog(operationName = "根据人员名称或者部门名称进行模糊查询")
    @RequestMapping(value = "/pageByApp")
    public Y9Page<Map<String, Object>> pageByApp(Y9PageQuery pageQuery, @NotBlank String appId, String deptName) {
        List<Map<String, Object>> items = new ArrayList<>();
        Page<Y9PersonalApp> y9PersonalAppPage = y9PersonalAppService.pageOrgUnitIdByAppId(appId, deptName, pageQuery);
        for (Y9PersonalApp y9PersonalApp : y9PersonalAppPage.getContent()) {
            Optional<OrgUnit> y9OrgBaseOptional =
                compositeOrgBaseService.findPersonOrPosition(y9PersonalApp.getOrgUnitId());
            if (y9OrgBaseOptional.isPresent()) {
                OrgUnit orgUnit = y9OrgBaseOptional.get();
                Map<String, Object> map = new HashMap<>(3);
                map.put("id", orgUnit.getId());
                map.put("name", orgUnit.getName());
                map.put("dn", orgUnit.getDn());
                items.add(map);
            }
        }
        return Y9Page.success(pageQuery.getPage(), y9PersonalAppPage.getTotalPages(),
            y9PersonalAppPage.getTotalElements(), items);
    }

    /**
     * 分页获取应用分类
     *
     * @param categoryId 分类id
     * @param pageQuery 分页信息
     * @return
     */
    @RiseLog(operationName = "分页获取应用分类")
    @RequestMapping(value = "/pageByCategoryId")
    public Y9Page<AppCategoryVO> pageByCategoryId(@RequestParam @NotBlank String categoryId, Y9PageQuery pageQuery) {
        Page<Y9AppCategory> y9AppCategoryMappingPage = y9AppCategoryService.pageByCategoryId(categoryId, pageQuery);
        List<AppCategoryVO> appCategoryVOList = toVo(y9AppCategoryMappingPage.getContent());
        return Y9Page.success(pageQuery.getPage(), y9AppCategoryMappingPage.getTotalPages(),
            y9AppCategoryMappingPage.getTotalElements(), appCategoryVOList);
    }

    /**
     * 保存应用排序信息
     *
     * @param appIds 应用ids
     * @param categoryId 分类id
     * @return
     */
    @RiseLog(operationName = "保存应用信息", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/save")
    public Y9Result<Boolean> save(@RequestParam @NotEmpty String[] appIds, @RequestParam @NotBlank String categoryId) {
        y9AppCategoryService.saveOrUpdate(appIds, categoryId);
        return Y9Result.successMsg("保存应用排序信息成功!");
    }

    /**
     * 根据系统名称或应用名称，获取应用列表
     *
     * @param appName
     * @return
     */
    @RiseLog(operationName = "根据系统名称或应用名称，获取应用列表")
    @RequestMapping(value = "/searchAppList")
    public Y9Result<List<App>> searchAppList(String appName) {
        List<App> y9AppList = y9AppService.listByAppName(appName);
        return Y9Result.success(y9AppList, "获取搜索应用列表成功!");
    }

    /**
     * 更新应用的排序信息
     *
     * @param ids 应用排序的ids
     * @return
     */
    @RiseLog(operationName = "更新应用的排序信息", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/updateOrder")
    public Y9Result<Boolean> updateOrder(@RequestParam @NotEmpty String[] ids) {
        y9AppCategoryService.saveOrder(ids);
        return Y9Result.successMsg("更新应用的排序信息成功!");
    }

}