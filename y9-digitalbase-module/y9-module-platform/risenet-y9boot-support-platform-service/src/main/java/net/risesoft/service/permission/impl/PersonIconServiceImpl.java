package net.risesoft.service.permission.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.Person;
import net.risesoft.model.platform.org.Position;
import net.risesoft.model.platform.permission.PersonIconItem;
import net.risesoft.model.platform.resource.App;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.permission.PersonIconService;
import net.risesoft.service.permission.cache.IdentityResourceCalculator;
import net.risesoft.service.permission.cache.Y9PersonToResourceService;
import net.risesoft.service.permission.cache.Y9PositionToResourceService;
import net.risesoft.y9public.service.resource.Y9AppService;

/**
 * 人员图标 Service 实现类
 *
 * @author shidaobang
 * @date 2025/06/24
 */
@Service
@RequiredArgsConstructor
public class PersonIconServiceImpl implements PersonIconService {

    private final IdentityResourceCalculator identityResourceCalculator;
    private final Y9PersonToResourceService y9PersonToResourceService;
    private final Y9PositionToResourceService y9PositionToResourceService;

    protected final CompositeOrgBaseService compositeOrgBaseService;

    protected final Y9AppService y9AppService;

    @Override
    public void buildPersonalAppIconByOrgUnitId(String orgUnitId) {
        identityResourceCalculator.recalculateByOrgUnitId(orgUnitId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonIconItem> listByOrgUnitId(String orgUnitId) {
        Optional<OrgUnit> orgUnitOptional = compositeOrgBaseService.findOrgUnitPersonOrPosition(orgUnitId);
        if (orgUnitOptional.isPresent()) {
            OrgUnit orgUnit = orgUnitOptional.get();
            if (orgUnit instanceof Person) {
                List<App> y9Apps = y9PersonToResourceService.listAppsByAuthority(orgUnitId, AuthorityEnum.BROWSE);
                return convert(y9Apps);
            } else if (orgUnit instanceof Position) {
                List<App> y9Apps = y9PositionToResourceService.listAppsByAuthority(orgUnitId, AuthorityEnum.BROWSE);
                return convert(y9Apps);
            }
        }
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public Y9Page<PersonIconItem> pageByOrgUnitId(String orgUnitId, Y9PageQuery pageQuery) {
        Optional<OrgUnit> orgUnitOptional = compositeOrgBaseService.findOrgUnitPersonOrPosition(orgUnitId);
        if (orgUnitOptional.isPresent()) {
            OrgUnit orgUnit = orgUnitOptional.get();
            if (orgUnit instanceof Person) {
                Page<String> appIdPage =
                    y9PersonToResourceService.pageAppIdByAuthority(orgUnitId, AuthorityEnum.BROWSE, pageQuery);
                List<App> y9Apps = y9AppService.listByIds(appIdPage.getContent());
                return Y9Page.success(pageQuery.getPage(), appIdPage.getTotalPages(), appIdPage.getTotalElements(),
                    convert(y9Apps));
            } else if (orgUnit instanceof Position) {
                Page<String> appIdPage =
                    y9PositionToResourceService.pageAppIdByAuthority(orgUnitId, AuthorityEnum.BROWSE, pageQuery);
                List<App> y9Apps = y9AppService.listByIds(appIdPage.getContent());
                return Y9Page.success(pageQuery.getPage(), appIdPage.getTotalPages(), appIdPage.getTotalElements(),
                    convert(y9Apps));
            }
        }

        return Y9Page.success(pageQuery.getPage(), 0, 0, List.of());
    }

    private List<PersonIconItem> convert(List<App> y9Apps) {
        return y9Apps.stream().map(y9App -> {
            PersonIconItem personIconItem = new PersonIconItem();
            personIconItem.setAppId(y9App.getAppId());
            personIconItem.setAppName(y9App.getName());
            personIconItem.setUrl(y9App.getUrl());
            personIconItem.setIconUrl(y9App.getIconUrl());
            personIconItem.setIconData(y9App.getIconData());
            personIconItem.setShowNumber(y9App.getShowNumber());
            personIconItem.setNumberUrl(y9App.getNumberUrl());
            personIconItem.setOpentype(y9App.getOpentype());
            personIconItem.setTabIndex(y9App.getTabIndex());
            return personIconItem;
        }).collect(Collectors.toList());
    }
}
