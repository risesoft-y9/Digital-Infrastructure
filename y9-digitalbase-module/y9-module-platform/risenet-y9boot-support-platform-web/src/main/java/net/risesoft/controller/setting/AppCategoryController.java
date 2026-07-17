package net.risesoft.controller.setting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.model.platform.dictionary.OptionValue;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.resource.App;
import net.risesoft.model.platform.tenant.TenantApp;
import net.risesoft.permission.annotation.IsAnyManager;
import net.risesoft.pojo.AppCategory;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.dictionary.Y9OptionValueService;
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
        return Y9Result.success(y9PersonalAppService.listAppsByOrgUnitId(personId), "根据人员Id获取图标app列表成功!");
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
        return Y9Result.success(toVO(y9AppCategoryService.listByCategoryId(categoryId)), "获取应用排序数据成功！");
    }

    private List<AppCategoryVO> toVO(List<AppCategory> appCategoryList) {
        List<AppCategoryVO> appCategoryVOList = new ArrayList<>();
        for (AppCategory appCategory : appCategoryList) {
            appCategoryVOList.add(toVO(appCategory));
        }
        return appCategoryVOList;
    }

    private AppCategoryVO toVO(AppCategory appCategory) {
        App app = y9AppService.getById(appCategory.getAppId());
        AppCategoryVO appCategoryVO = new AppCategoryVO();
        appCategoryVO.setId(appCategory.getId());
        appCategoryVO.setCategoryId(appCategory.getCategoryId());
        appCategoryVO.setAppId(appCategory.getAppId());
        appCategoryVO.setAppName(app.getName());
        appCategoryVO.setAppUrl(app.getUrl());
        appCategoryVO.setTabIndex(appCategory.getTabIndex());
        return appCategoryVO;
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
    public Y9Page<OrgUnit> pageByApp(Y9PageQuery pageQuery, @NotBlank String appId, String deptName) {
        Page<OrgUnit> orgUnitPage = y9PersonalAppService.pageOrgUnitByAppId(appId, deptName, pageQuery);
        return Y9Page.success(pageQuery.getPage(), orgUnitPage.getTotalPages(), orgUnitPage.getTotalElements(),
            orgUnitPage.getContent());
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
        Page<AppCategory> appCategoryPage = y9AppCategoryService.pageByCategoryId(categoryId, pageQuery);
        return Y9Page.success(pageQuery.getPage(), appCategoryPage.getTotalPages(), appCategoryPage.getTotalElements(),
            toVO(appCategoryPage.getContent()));
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