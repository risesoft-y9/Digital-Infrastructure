package net.risesoft.service.permission.cache.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.permission.cache.Y9PersonalApp;
import net.risesoft.enums.platform.PersonalAppTabIndexTypeEnum;
import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.org.Department;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.Person;
import net.risesoft.model.platform.org.Position;
import net.risesoft.model.platform.permission.cache.PersonalApp;
import net.risesoft.model.platform.resource.App;
import net.risesoft.pojo.AppCategory;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.repository.permission.cache.Y9PersonalAppRepository;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9DepartmentService;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.service.permission.cache.Y9PersonToResourceService;
import net.risesoft.service.permission.cache.Y9PersonalAppService;
import net.risesoft.service.permission.cache.Y9PositionToResourceService;
import net.risesoft.service.setting.Y9AppCategoryService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.service.resource.Y9AppService;
import net.risesoft.y9public.service.tenant.Y9TenantSystemService;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service(value = "y9PersonIconItemService")
@RequiredArgsConstructor
public class Y9PersonalAppServiceImpl implements Y9PersonalAppService {

    private final Y9AppService y9AppService;

    private final Y9PersonalAppRepository y9PersonalAppRepository;

    private final Y9TenantSystemService y9TenantSystemService;

    private final Y9AppCategoryService y9AppCategoryService;

    private final Y9PersonToResourceService y9PersonToResourceService;

    private final Y9PositionToResourceService y9PositionToResourceService;

    private final CompositeOrgBaseService compositeOrgBaseService;

    private final Y9DepartmentService y9DepartmentService;

    private final Y9PositionService y9PositionService;

    @Transactional(readOnly = false)
    public void buildAppIconForPerson(String personId, List<AppCategory> appCategoryList) {
        List<Y9PersonalApp> y9PersonalAppList = y9PersonalAppRepository.findByOrgUnitIdOrderByTabIndex(personId);
        List<App> accessAppList = y9PersonToResourceService.listAppsByAuthority(personId, AuthorityEnum.BROWSE);
        Set<String> appIds = accessAppList.stream().map(App::getId).collect(Collectors.toSet());

        // 移除不再有权限的
        List<Y9PersonalApp> y9PersonalApps = y9PersonalAppList.stream()
            .filter(identityApp -> !appIds.contains(identityApp.getAppId()))
            .collect(Collectors.toList());
        y9PersonalAppRepository.deleteAll(y9PersonalApps);

        List<Y9PersonalApp> y9PersonalAppListToSave = new ArrayList<>();
        for (int i = 0; i < appCategoryList.size(); i++) {
            AppCategory appCategory = appCategoryList.get(i);
            if (appIds.contains(appCategory.getAppId())) {
                // 有权限的应用才保存
                Y9PersonalApp y9PersonalApp = this.buildIdentityApp(personId, appCategory, i);
                y9PersonalAppListToSave.add(y9PersonalApp);
            }
        }
        y9PersonalAppRepository.saveAll(y9PersonalAppListToSave);
    }

    @Transactional(readOnly = false)
    public void buildAppIconForPosition(String positionId, List<AppCategory> appCategoryList) {
        List<Y9PersonalApp> y9PersonalAppList = y9PersonalAppRepository.findByOrgUnitIdOrderByTabIndex(positionId);
        List<App> accessAppList = y9PositionToResourceService.listAppsByAuthority(positionId, AuthorityEnum.BROWSE);
        Set<String> appIds = accessAppList.stream().map(App::getId).collect(Collectors.toSet());

        // 移除 不再有权限的/删除的应用
        List<Y9PersonalApp> y9PersonalApps = y9PersonalAppList.stream()
            .filter(identityApp -> !appIds.contains(identityApp.getAppId()))
            .collect(Collectors.toList());
        y9PersonalAppRepository.deleteAll(y9PersonalApps);

        List<Y9PersonalApp> y9PersonalAppListToSave = new ArrayList<>();
        for (int i = 0; i < appCategoryList.size(); i++) {
            AppCategory appCategory = appCategoryList.get(i);
            if (appIds.contains(appCategory.getAppId())) {
                Y9PersonalApp y9PersonalApp = this.buildIdentityApp(positionId, appCategory, i);
                y9PersonalAppListToSave.add(y9PersonalApp);
            }
        }
        y9PersonalAppRepository.saveAll(y9PersonalAppListToSave);
    }

    @Override
    @Transactional(readOnly = false)
    public void buildPersonalAppByOrgUnitId(String orgUnitId) {
        Optional<OrgUnit> orgUnitOptional = compositeOrgBaseService.findPersonOrPosition(orgUnitId);
        if (orgUnitOptional.isPresent()) {
            OrgUnit orgUnit = orgUnitOptional.get();
            if (orgUnit instanceof Person) {
                this.buildPersonalAppForPerson(orgUnitId);
            } else if (orgUnit instanceof Position) {
                this.buildPersonalAppForPosition(orgUnitId);
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void buildDeptPersonalAppForPerson(String deptId) {
        List<AppCategory> appCategoryList = y9AppCategoryService.buildDefaultAppCategoryList();
        List<Person> persons = compositeOrgBaseService.listAllDescendantPersons(deptId, false);
        for (Person person : persons) {
            if (Boolean.TRUE.equals(person.getDisabled())) {
                continue;
            }
            buildAppIconForPerson(person.getId(), appCategoryList);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void buildDeptPersonalAppForPosition(String deptId) {
        List<AppCategory> appCategoryList = y9AppCategoryService.buildDefaultAppCategoryList();
        List<Position> positions = compositeOrgBaseService.listAllDescendantPositions(deptId);
        for (Position position : positions) {
            if (Boolean.TRUE.equals(position.getDisabled())) {
                continue;
            }
            buildAppIconForPosition(position.getId(), appCategoryList);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void buildPersonalAppForPerson(String personId) {
        List<AppCategory> appCategoryList = y9AppCategoryService.buildDefaultAppCategoryList();
        buildAppIconForPerson(personId, appCategoryList);
    }

    @Override
    @Transactional(readOnly = false)
    public void buildPersonalAppForPosition(String positionId) {
        List<AppCategory> appCategoryList = y9AppCategoryService.buildDefaultAppCategoryList();
        buildAppIconForPosition(positionId, appCategoryList);
    }

    @Override
    public void buildPersonalAppWithoutPerm(String personId) {
        List<AppCategory> appCategoryList = y9AppCategoryService.buildDefaultAppCategoryList();
        List<String> systemIds = y9TenantSystemService.listSystemIdByTenantId(Y9LoginUserHolder.getTenantId());
        List<App> appLists = y9AppService.listAll();

        Set<String> appIds = appLists.stream()
            .filter(it -> systemIds.contains(it.getSystemId()))
            .map(App::getId)
            .collect(Collectors.toSet());
        List<Y9PersonalApp> y9PersonalAppList = y9PersonalAppRepository.findByOrgUnitIdOrderByTabIndex(personId);

        for (Y9PersonalApp y9PersonalApp : y9PersonalAppList) {
            if (!appIds.contains(y9PersonalApp.getAppId())) {
                y9PersonalAppRepository.delete(y9PersonalApp);
            }
        }

        List<Y9PersonalApp> identityAppListToSave = new ArrayList<>();
        for (int i = 0; i < appCategoryList.size(); i++) {
            AppCategory appCategory = appCategoryList.get(i);
            if (appIds.contains(appCategory.getAppId())) {
                Y9PersonalApp y9PersonalApp = this.buildIdentityApp(personId, appCategory, i);
                identityAppListToSave.add(y9PersonalApp);
            }
        }
        y9PersonalAppRepository.saveAll(identityAppListToSave);
    }

    /**
     * 构建人员图标排序
     *
     * @param orgUnitId 人员/岗位id
     * @param appCategory 图标排序
     * @param tabIndex 排序
     * @return
     */
    private Y9PersonalApp buildIdentityApp(String orgUnitId, AppCategory appCategory, Integer tabIndex) {
        Y9PersonalApp y9PersonalApp =
            y9PersonalAppRepository.findByOrgUnitIdAndAppId(orgUnitId, appCategory.getAppId());
        if (y9PersonalApp == null) {
            y9PersonalApp = new Y9PersonalApp();
            y9PersonalApp.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            y9PersonalApp.setTabIndex(tabIndex);
            y9PersonalApp.setTabIndexType(PersonalAppTabIndexTypeEnum.DEFAULT);
            y9PersonalApp.setOrgUnitId(orgUnitId);
            y9PersonalApp.setAppId(appCategory.getAppId());
            y9PersonalApp.setCategoryId(appCategory.getCategoryId());
            return y9PersonalApp;
        }
        if (y9PersonalApp.getTabIndexType().equals(PersonalAppTabIndexTypeEnum.DEFAULT)) {
            y9PersonalApp.setTabIndex(tabIndex);
        }
        y9PersonalApp.setCategoryId(appCategory.getCategoryId());
        return y9PersonalApp;
    }

    @Override
    public List<PersonalApp> listByOrgUnitId(String orgUnitId) {
        return entityToModel(y9PersonalAppRepository.findByOrgUnitIdOrderByTabIndex(orgUnitId));
    }

    @Override
    public List<App> listAppsByOrgUnitId(String orgUnitId) {
        return y9PersonalAppRepository.findByOrgUnitIdOrderByTabIndex(orgUnitId)
            .stream()
            .map(Y9PersonalApp::getAppId)
            .map(y9AppService::findById)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    @Override
    public Page<PersonalApp> pageByOrgUnitId(String orgUnitId, String categoryId, Y9PageQuery pageQuery) {
        Pageable pageable = PageRequest.of(pageQuery.getPage4Db(), pageQuery.getSize(), Sort.by("tabIndex"));
        Page<Y9PersonalApp> y9PersonalAppPage;
        if (StringUtils.isNotBlank(categoryId)) {
            y9PersonalAppPage = y9PersonalAppRepository.findByOrgUnitIdAndCategoryId(orgUnitId, categoryId, pageable);
        } else {
            y9PersonalAppPage = y9PersonalAppRepository.findByOrgUnitId(orgUnitId, pageable);
        }
        return new PageImpl<>(entityToModel(y9PersonalAppPage.getContent()), pageable,
            y9PersonalAppPage.getTotalElements());
    }

    @Override
    public Page<OrgUnit> pageOrgUnitByAppId(String appId, String deptName, Y9PageQuery pageQuery) {
        Pageable pageable = PageRequest.of(pageQuery.getPage4Db(), pageQuery.getSize(), Sort.by("createTime"));
        List<Department> departmentList = y9DepartmentService.listByNameLike(deptName, Boolean.FALSE);
        List<Position> positionList = new ArrayList<>();
        for (Department department : departmentList) {
            positionList.addAll(compositeOrgBaseService.listAllDescendantPositions(department.getId()));
        }
        List<String> orgUnitIdList = positionList.stream().map(OrgUnit::getId).collect(Collectors.toList());

        Page<Y9PersonalApp> y9PersonalAppPage;
        if (orgUnitIdList.isEmpty()) {
            y9PersonalAppPage = y9PersonalAppRepository.findByAppId(appId, pageable);
        } else {
            y9PersonalAppPage = y9PersonalAppRepository.findByOrgUnitIdInAndAppId(orgUnitIdList, appId, pageable);
        }
        List<OrgUnit> orgUnitList = y9PersonalAppPage.getContent()
            .stream()
            .map(Y9PersonalApp::getOrgUnitId)
            .map(compositeOrgBaseService::findPersonOrPosition)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
        return new PageImpl<>(orgUnitList, pageable, y9PersonalAppPage.getTotalElements());
    }

    private List<PersonalApp> entityToModel(List<Y9PersonalApp> y9PersonalAppList) {
        List<PersonalApp> personalAppList = new ArrayList<>();
        for (Y9PersonalApp y9PersonalApp : y9PersonalAppList) {
            App app = y9AppService.findById(y9PersonalApp.getAppId()).orElse(null);
            if (app == null || !Boolean.TRUE.equals(app.getEnabled())) {
                continue;
            }
            PersonalApp personalApp = new PersonalApp();
            personalApp.setAppId(app.getId());
            personalApp.setAppName(app.getName());
            personalApp.setUrl(app.getUrl());
            personalApp.setIconUrl(app.getIconUrl());
            personalApp.setIconData(app.getIconData());
            personalApp.setShowNumber(app.getShowNumber());
            personalApp.setNumberUrl(app.getNumberUrl());
            personalApp.setOpentype(app.getOpentype());
            personalApp.setTabIndex(y9PersonalApp.getTabIndex());
            personalApp.setStar(y9PersonalApp.getStar());
            personalAppList.add(personalApp);
        }
        return personalAppList;
    }

    @Override
    @Transactional(readOnly = false)
    public void save(Y9PersonalApp acPersonIconItem) {
        y9PersonalAppRepository.save(acPersonIconItem);
    }

    @Override
    @Transactional(readOnly = false)
    public void starApps(String orgUnitId, String[] appIds) {
        List<Y9PersonalApp> y9PersonalAppList = y9PersonalAppRepository.findByOrgUnitIdAndStar(orgUnitId, Boolean.TRUE);
        for (Y9PersonalApp personalApp : y9PersonalAppList) {
            personalApp.setStar(Boolean.FALSE);
        }
        y9PersonalAppRepository.saveAll(y9PersonalAppList);

        for (String appId : appIds) {
            Y9PersonalApp y9PersonalApp = y9PersonalAppRepository.findByOrgUnitIdAndAppId(orgUnitId, appId);
            if (y9PersonalApp != null) {
                y9PersonalApp.setStar(Boolean.TRUE);
                y9PersonalAppRepository.save(y9PersonalApp);
            }
        }

    }

    @Override
    @Transactional(readOnly = false)
    public void buildPersonalAppForPerson(String personId, List<AppCategory> appCategoryList) {
        buildAppIconForPerson(personId, appCategoryList);
    }

    @Override
    @Transactional(readOnly = false)
    public void buildPersonalAppForPosition(String positionId, List<AppCategory> appCategoryList) {
        buildAppIconForPosition(positionId, appCategoryList);
    }

    @Override
    @Transactional(readOnly = false)
    public void sort(String orgUnitId, List<String> appIdList) {
        for (int i = 0; i < appIdList.size(); i++) {
            String appId = appIdList.get(i);
            Y9PersonalApp y9PersonalApp = y9PersonalAppRepository.findByOrgUnitIdAndAppId(orgUnitId, appId);
            if (y9PersonalApp != null) {
                y9PersonalApp.setTabIndex(i);
                y9PersonalApp.setTabIndexType(PersonalAppTabIndexTypeEnum.PERSONAL);
                y9PersonalAppRepository.save(y9PersonalApp);
            }
        }
    }
}
