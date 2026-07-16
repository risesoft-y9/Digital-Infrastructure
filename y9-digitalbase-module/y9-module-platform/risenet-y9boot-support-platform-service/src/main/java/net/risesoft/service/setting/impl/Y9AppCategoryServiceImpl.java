package net.risesoft.service.setting.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.OptionClassConsts;
import net.risesoft.entity.setting.Y9AppCategory;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.dictionary.OptionValue;
import net.risesoft.model.platform.resource.App;
import net.risesoft.model.platform.resource.Resource;
import net.risesoft.pojo.AppCategory;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.repository.setting.Y9AppCategoryRepository;
import net.risesoft.service.dictionary.Y9OptionValueService;
import net.risesoft.service.setting.Y9AppCategoryService;
import net.risesoft.util.Y9PlatformUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.service.resource.Y9AppService;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service(value = "iconItemOrderService")
@RequiredArgsConstructor
@Slf4j
public class Y9AppCategoryServiceImpl implements Y9AppCategoryService {

    private final Y9AppCategoryRepository y9AppCategoryRepository;

    private final Y9AppService y9AppService;

    private final Y9OptionValueService y9OptionValueService;

    @Override
    @Transactional(readOnly = false)
    public void delete(String[] ids) {
        for (String id : ids) {
            y9AppCategoryRepository.deleteById(id);
        }
    }

    @Override
    public Y9AppCategory findById(String id) {
        return y9AppCategoryRepository.findById(id).orElse(null);
    }

    private Integer getMaxIndex(String resourceId) {
        return y9AppCategoryRepository.findTopByCategoryIdOrderByTabIndexDesc(resourceId)
            .map(Y9AppCategory::getTabIndex)
            .orElse(0);
    }

    @Override
    public List<AppCategory> buildDefaultAppCategoryList() {
        List<App> appList = y9AppService.listByEnable();

        Map<String, App> appIdToAppMap = appList.stream().collect(Collectors.toMap(Resource::getId, app -> app));

        List<AppCategory> configuredAppCategoryList = new ArrayList<>();
        List<Y9AppCategory> configuredY9AppCategoryList = this.listByAllAppCategory();
        for (Y9AppCategory y9AppCategory : configuredY9AppCategoryList) {
            AppCategory appCategory = new AppCategory();
            appCategory.setAppId(y9AppCategory.getAppId());
            appCategory.setCategoryId(y9AppCategory.getCategoryId());

            App y9App = appIdToAppMap.get(y9AppCategory.getAppId());
            if (y9App != null) {
                appList.remove(y9App);
            }

            configuredAppCategoryList.add(appCategory);
            LOGGER.info("分类排序的app个数-》{}，剩余有app个数-》{}", configuredAppCategoryList.size(), appList.size());
        }

        List<AppCategory> appCategoryList = new ArrayList<>(configuredAppCategoryList);
        for (App app : appList) {
            AppCategory appCategory = new AppCategory();
            appCategory.setAppId(app.getId());
            appCategory.setCategoryId(null);
            appCategoryList.add(appCategory);
        }
        return appCategoryList;
    }

    @Override
    public List<Y9AppCategory> listByAllAppCategory() {
        List<Y9AppCategory> appCategoryMappingList = new ArrayList<>();

        List<OptionValue> optionValues = y9OptionValueService.listByType(OptionClassConsts.APP_CATEGORY);
        for (OptionValue resource : optionValues) {
            List<Y9AppCategory> appList = y9AppCategoryRepository.findByCategoryIdOrderByTabIndex(resource.getCode());
            for (Y9AppCategory order : appList) {
                App y9App = y9AppService.findById(order.getAppId()).orElse(null);
                if (y9App != null && Boolean.TRUE.equals(y9App.getEnabled())) {
                    appCategoryMappingList.add(order);
                }
            }
        }
        return appCategoryMappingList;
    }

    @Override
    public List<Y9AppCategory> listByCategoryId(String categoryId) {
        return y9AppCategoryRepository.findByCategoryIdOrderByTabIndex(categoryId);
    }

    @Override
    public Page<Y9AppCategory> pageByCategoryId(String categoryId, Y9PageQuery pageQuery) {
        Pageable pageable =
            PageRequest.of(pageQuery.getPage4Db(), pageQuery.getSize(), Sort.by(Sort.Direction.ASC, "tabIndex"));
        return y9AppCategoryRepository.findPageByCategoryId(categoryId, pageable);
    }

    @Override
    @Transactional(readOnly = false)
    public void saveOrder(String[] ids) {
        for (int i = 0; i < ids.length; i++) {
            Y9AppCategory order = y9AppCategoryRepository.findById(ids[i]).orElse(null);
            if (null != order) {
                order.setTabIndex(i);
                y9AppCategoryRepository.save(order);
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Y9AppCategory saveOrUpdate(Y9AppCategory y9AppCategory) {
        if (StringUtils.isNotEmpty(y9AppCategory.getId())) {
            Y9AppCategory originalAppCategory = this.findById(y9AppCategory.getId());
            originalAppCategory.setAppId(y9AppCategory.getAppId());
            originalAppCategory.setTabIndex(y9AppCategory.getTabIndex());
            y9AppCategoryRepository.save(originalAppCategory);
            return originalAppCategory;
        }
        if (StringUtils.isEmpty(y9AppCategory.getId())) {
            y9AppCategory.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }
        Integer maxTabIndex = getMaxIndex(y9AppCategory.getCategoryId());
        y9AppCategory.setTabIndex(maxTabIndex + 1);
        y9AppCategoryRepository.save(y9AppCategory);
        return y9AppCategory;
    }

    @Override
    @Transactional(readOnly = false)
    public void saveOrUpdate(String[] appIds, String categoryId) {
        for (int i = 0; i < appIds.length; i++) {
            Y9AppCategory y9AppCategory = y9AppCategoryRepository.findByAppId(appIds[i]);
            App app = y9AppService.findById(appIds[i]).orElse(null);
            if (app != null) {
                if (y9AppCategory == null) {
                    y9AppCategory = new Y9AppCategory();
                    y9AppCategory.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    y9AppCategory.setAppId(app.getId());
                }
                Integer maxTabIndex = getMaxIndex(categoryId);
                y9AppCategory.setTabIndex(maxTabIndex + 1);
                y9AppCategory.setCategoryId(categoryId);
                y9AppCategoryRepository.save(y9AppCategory);
            }
        }
    }

    @EventListener
    public void onAppDeleted(Y9EntityDeletedEvent<Y9App> event) {
        Y9App entity = event.getEntity();
        for (String tenantId : Y9PlatformUtil.getTenantIds()) {
            deleteByResource(tenantId, entity);
        }
    }

    @Async
    protected void deleteByResource(String tenantId, Y9App y9App) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9AppCategory y9AppCategory = y9AppCategoryRepository.findByAppId(y9App.getId());
        if (null != y9AppCategory) {
            y9AppCategoryRepository.delete(y9AppCategory);
        }
        LOGGER.trace("{}资源[{}]删除时同步删除租户[{}]的应用排序", y9App.getResourceType().getName(), y9App.getId(), tenantId);
    }
}
