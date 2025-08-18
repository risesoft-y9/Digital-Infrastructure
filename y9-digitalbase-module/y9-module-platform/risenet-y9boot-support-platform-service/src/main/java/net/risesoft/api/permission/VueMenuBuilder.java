package net.risesoft.api.permission;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.permission.cache.Y9IdentityToResourceAndAuthorityBase;
import net.risesoft.enums.platform.org.IdentityTypeEnum;
import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.enums.platform.resource.ResourceTypeEnum;
import net.risesoft.model.platform.resource.VueButton;
import net.risesoft.model.platform.resource.VueMenu;
import net.risesoft.service.permission.cache.Y9PersonToResourceAndAuthorityService;
import net.risesoft.service.permission.cache.Y9PositionToResourceAndAuthorityService;
import net.risesoft.y9public.entity.resource.Y9Menu;
import net.risesoft.y9public.entity.resource.Y9Operation;
import net.risesoft.y9public.service.resource.Y9MenuService;
import net.risesoft.y9public.service.resource.Y9OperationService;

/**
 * 根据人员权限列表构建 vue 菜单组件
 * 
 * @author shidaobang
 * @date 2023/07/11
 * @since 9.6.2
 */
@Component
@RequiredArgsConstructor
public class VueMenuBuilder {

    private final Y9MenuService y9MenuService;
    private final Y9OperationService y9OperationService;

    private final Y9PersonToResourceAndAuthorityService y9PersonToResourceAndAuthorityService;
    private final Y9PositionToResourceAndAuthorityService y9PositionToResourceAndAuthorityService;

    private VueButton buildVueButton(Y9Operation y9Operation) {
        VueButton button = new VueButton();
        button.setName(y9Operation.getName());
        button.setIcon(y9Operation.getIconUrl());
        button.setButtonId(y9Operation.getCustomId());
        button.setDisplayType(y9Operation.getDisplayType());
        button.setUrl(y9Operation.getUrl());
        button.setEventName(y9Operation.getEventName());
        return button;
    }

    private List<VueButton> buildVueButtons(IdentityTypeEnum identityType, String personId, AuthorityEnum authority,
        String menuId) {
        List<Y9Operation> y9OperationList;
        if (IdentityTypeEnum.PERSON.equals(identityType)) {
            y9OperationList =
                y9PersonToResourceAndAuthorityService.list(personId, menuId, ResourceTypeEnum.OPERATION, authority)
                    .stream()
                    .map(Y9IdentityToResourceAndAuthorityBase::getResourceId)
                    .distinct()
                    .map(y9OperationService::getById)
                    .sorted()
                    .collect(Collectors.toList());
        } else {
            y9OperationList =
                y9PositionToResourceAndAuthorityService.list(personId, menuId, ResourceTypeEnum.OPERATION, authority)
                    .stream()
                    .map(Y9IdentityToResourceAndAuthorityBase::getResourceId)
                    .distinct()
                    .map(y9OperationService::getById)
                    .sorted()
                    .collect(Collectors.toList());
        }

        List<VueButton> buttonList = new ArrayList<>();
        for (Y9Operation y9Operation : y9OperationList) {
            if (y9Operation.getEnabled()) {
                buttonList.add(buildVueButton(y9Operation));
            }
        }
        return buttonList;
    }

    private VueMenu buildVueMenu(IdentityTypeEnum identityType, String personId, AuthorityEnum authority, String menuId,
        Y9Menu y9Menu) {
        VueMenu vueMenu = new VueMenu();
        vueMenu.setName(y9Menu.getName());
        vueMenu.setPath(y9Menu.getUrl());
        vueMenu.setRedirect(y9Menu.getRedirect());
        vueMenu.setComponent(y9Menu.getComponent());
        vueMenu.setMeta(y9Menu.getMeta());
        vueMenu.setTarget(y9Menu.getTarget());

        List<VueMenu> subVueMenuList = new ArrayList<>();
        buildVueMenus(identityType, personId, authority, menuId, subVueMenuList);
        vueMenu.setChildren(subVueMenuList);

        List<VueButton> buttonList = buildVueButtons(identityType, personId, authority, menuId);
        vueMenu.setButtons(buttonList);
        return vueMenu;
    }

    public void buildVueMenus(IdentityTypeEnum identityType, String personId, AuthorityEnum authority,
        String resourceId, List<VueMenu> vueMenuList) {
        List<Y9Menu> menuList;
        if (IdentityTypeEnum.PERSON.equals(identityType)) {
            menuList =
                y9PersonToResourceAndAuthorityService.list(personId, resourceId, ResourceTypeEnum.MENU, authority)
                    .stream()
                    .map(Y9IdentityToResourceAndAuthorityBase::getResourceId)
                    .distinct()
                    .map(y9MenuService::getById)
                    .sorted()
                    .collect(Collectors.toList());
        } else {
            menuList =
                y9PositionToResourceAndAuthorityService.list(personId, resourceId, ResourceTypeEnum.MENU, authority)
                    .stream()
                    .map(Y9IdentityToResourceAndAuthorityBase::getResourceId)
                    .distinct()
                    .map(y9MenuService::getById)
                    .sorted()
                    .collect(Collectors.toList());
        }
        for (Y9Menu y9Menu : menuList) {
            if (y9Menu.getEnabled()) {
                VueMenu vueMenu = buildVueMenu(identityType, personId, authority, y9Menu.getId(), y9Menu);
                vueMenuList.add(vueMenu);
            }
        }
    }
}
