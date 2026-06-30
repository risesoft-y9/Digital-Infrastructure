package net.risesoft.service.setting.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    @Transactional(readOnly = false)
    public void deleteByAppId(String appId) {
        Y9AppCategory oder = y9AppCategoryRepository.findByAppId(appId);
        if (null != oder) {
            y9AppCategoryRepository.delete(oder);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteByCategoryId(String categoryId) {
        List<Y9AppCategory> appList = y9AppCategoryRepository.findByCategoryIdOrderByTabIndex(categoryId);
        y9AppCategoryRepository.deleteAll(appList);
    }

    @Override
    public Y9AppCategory findByAppId(String appId) {
        return y9AppCategoryRepository.findByAppId(appId);
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
            Y9AppCategory order = this.findById(y9AppCategory.getId());
            order.setAppId(y9AppCategory.getAppId());
            order.setTabIndex(y9AppCategory.getTabIndex());
            y9AppCategoryRepository.save(order);
            return order;
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
            Y9AppCategory appCategoryMapping = y9AppCategoryRepository.findByAppId(appIds[i]);
            App app = y9AppService.findById(appIds[i]).orElse(null);
            if (app != null) {
                if (appCategoryMapping == null) {
                    appCategoryMapping = new Y9AppCategory();
                    appCategoryMapping.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    appCategoryMapping.setAppId(app.getId());
                }
                Integer maxTabIndex = getMaxIndex(categoryId);
                appCategoryMapping.setTabIndex(maxTabIndex + 1);
                appCategoryMapping.setCategoryId(categoryId);
                y9AppCategoryRepository.save(appCategoryMapping);
            }
        }
    }
}
