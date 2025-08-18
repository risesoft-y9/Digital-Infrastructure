package net.risesoft.service.permission.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.org.Y9OrgBase;
import net.risesoft.entity.org.Y9Person;
import net.risesoft.entity.org.Y9Position;
import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.model.platform.permission.PersonIconItem;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.permission.PersonIconService;
import net.risesoft.service.permission.cache.IdentityResourceCalculator;
import net.risesoft.service.permission.cache.Y9PersonToResourceAndAuthorityService;
import net.risesoft.service.permission.cache.Y9PositionToResourceAndAuthorityService;
import net.risesoft.y9public.entity.resource.Y9App;
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
    private final Y9PersonToResourceAndAuthorityService y9PersonToResourceAndAuthorityService;
    private final Y9PositionToResourceAndAuthorityService y9PositionToResourceAndAuthorityService;

    protected final CompositeOrgBaseService compositeOrgBaseService;

    protected final Y9AppService y9AppService;

    @Override
    public void buildPersonalAppIconByOrgUnitId(String orgUnitId) {
        identityResourceCalculator.recalculateByOrgUnitId(orgUnitId);
    }

    @Override
    public List<PersonIconItem> listByOrgUnitId(String orgUnitId) {
        Optional<Y9OrgBase> orgUnitOptional = compositeOrgBaseService.findOrgUnitPersonOrPosition(orgUnitId);
        if (orgUnitOptional.isPresent()) {
            Y9OrgBase orgUnit = orgUnitOptional.get();
            if (orgUnit instanceof Y9Person) {
                List<Y9App> y9Apps =
                    y9PersonToResourceAndAuthorityService.listAppsByAuthority(orgUnitId, AuthorityEnum.BROWSE);
                return convert(y9Apps);
            } else if (orgUnit instanceof Y9Position) {
                List<Y9App> y9Apps =
                    y9PositionToResourceAndAuthorityService.listAppsByAuthority(orgUnitId, AuthorityEnum.BROWSE);
                return convert(y9Apps);
            }
        }
        return List.of();
    }

    @Override
    public Y9Page<PersonIconItem> pageByOrgUnitId(String orgUnitId, Y9PageQuery pageQuery) {
        Optional<Y9OrgBase> orgUnitOptional = compositeOrgBaseService.findOrgUnitPersonOrPosition(orgUnitId);
        if (orgUnitOptional.isPresent()) {
            Y9OrgBase orgUnit = orgUnitOptional.get();
            if (orgUnit instanceof Y9Person) {
                Page<String> appIdPage = y9PersonToResourceAndAuthorityService.pageAppIdByAuthority(orgUnitId,
                    AuthorityEnum.BROWSE, pageQuery);
                List<Y9App> y9Apps = y9AppService.listByIds(appIdPage.getContent());
                return Y9Page.success(pageQuery.getPage(), appIdPage.getTotalPages(), appIdPage.getTotalElements(),
                    convert(y9Apps));
            } else if (orgUnit instanceof Y9Position) {
                Page<String> appIdPage = y9PositionToResourceAndAuthorityService.pageAppIdByAuthority(orgUnitId,
                    AuthorityEnum.BROWSE, pageQuery);
                List<Y9App> y9Apps = y9AppService.listByIds(appIdPage.getContent());
                return Y9Page.success(pageQuery.getPage(), appIdPage.getTotalPages(), appIdPage.getTotalElements(),
                    convert(y9Apps));
            }
        }

        return Y9Page.success(pageQuery.getPage(), 0, 0, List.of());
    }

    private List<PersonIconItem> convert(List<Y9App> y9Apps) {
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
