package net.risesoft.api.v0.permission;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.enums.platform.resource.ResourceTypeEnum;
import net.risesoft.model.platform.permission.cache.IdentityToResourceBase;
import net.risesoft.model.platform.permission.cache.PersonToResource;
import net.risesoft.model.platform.resource.FrontendButton;
import net.risesoft.model.platform.resource.FrontendMenu;
import net.risesoft.model.platform.resource.Menu;
import net.risesoft.model.platform.resource.Operation;
import net.risesoft.service.permission.cache.Y9PersonToResourceService;
import net.risesoft.y9public.service.resource.Y9MenuService;
import net.risesoft.y9public.service.resource.Y9OperationService;

/**
 * 根据人员权限列表构建 vue 菜单组件
 * 
 * @author shidaobang
 * @date 2023/07/11
 * @since 9.6.2
 */
@Component(value = "v0VueMenuBuilder")
@RequiredArgsConstructor
@Deprecated
public class VueMenuBuilder {

    private final Y9MenuService y9MenuService;
    private final Y9OperationService y9OperationService;
    private final Y9PersonToResourceService y9PersonToResourceService;

    private FrontendButton buildVueButton(Operation y9Operation) {
        FrontendButton button = new FrontendButton();
        button.setName(y9Operation.getName());
        button.setIcon(y9Operation.getIconUrl());
        button.setButtonId(y9Operation.getCustomId());
        button.setDisplayType(y9Operation.getDisplayType());
        button.setUrl(y9Operation.getUrl());
        button.setEventName(y9Operation.getEventName());
        return button;
    }

    private List<FrontendButton> buildVueButtons(String personId, AuthorityEnum authority, String menuId) {
        List<FrontendButton> buttonList = new ArrayList<>();
        List<PersonToResource> authorizedButtonList =
            y9PersonToResourceService.list(personId, menuId, ResourceTypeEnum.OPERATION, authority);
        List<Operation> y9OperationList = authorizedButtonList.stream()
            .map(IdentityToResourceBase::getResourceId)
            .distinct()
            .map(y9OperationService::getById)
            .sorted()
            .collect(Collectors.toList());
        for (Operation y9Operation : y9OperationList) {
            if (y9Operation.getEnabled()) {
                buttonList.add(buildVueButton(y9Operation));
            }
        }
        return buttonList;
    }

    private FrontendMenu buildVueMenu(String personId, AuthorityEnum authority, String menuId, Menu y9Menu) {
        FrontendMenu frontendMenu = new FrontendMenu();
        frontendMenu.setName(y9Menu.getName());
        frontendMenu.setPath(y9Menu.getUrl());
        frontendMenu.setRedirect(y9Menu.getRedirect());
        frontendMenu.setComponent(y9Menu.getComponent());
        frontendMenu.setMeta(y9Menu.getMeta());
        frontendMenu.setTarget(y9Menu.getTarget());

        List<FrontendMenu> subFrontendMenuList = new ArrayList<>();
        buildVueMenus(personId, authority, menuId, subFrontendMenuList);
        frontendMenu.setChildren(subFrontendMenuList);

        List<FrontendButton> buttonList = buildVueButtons(personId, authority, menuId);
        frontendMenu.setButtons(buttonList);
        return frontendMenu;
    }

    public void buildVueMenus(String personId, AuthorityEnum authority, String resourceId,
        List<FrontendMenu> frontendMenuList) {
        List<PersonToResource> authorizedMenuList =
            y9PersonToResourceService.list(personId, resourceId, ResourceTypeEnum.MENU, authority);
        List<Menu> menuList = authorizedMenuList.stream()
            .map(IdentityToResourceBase::getResourceId)
            .distinct()
            .map(y9MenuService::getById)
            .sorted()
            .collect(Collectors.toList());
        for (Menu y9Menu : menuList) {
            if (y9Menu.getEnabled()) {
                FrontendMenu frontendMenu = buildVueMenu(personId, authority, y9Menu.getId(), y9Menu);
                frontendMenuList.add(frontendMenu);
            }
        }
    }
}
