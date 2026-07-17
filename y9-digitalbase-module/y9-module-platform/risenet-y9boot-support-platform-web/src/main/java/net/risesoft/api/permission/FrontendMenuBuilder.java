package net.risesoft.api.permission;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import net.risesoft.enums.platform.org.IdentityTypeEnum;
import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.enums.platform.resource.ResourceTypeEnum;
import net.risesoft.model.platform.permission.cache.IdentityToResourceBase;
import net.risesoft.model.platform.resource.FrontendButton;
import net.risesoft.model.platform.resource.FrontendMenu;
import net.risesoft.model.platform.resource.Menu;
import net.risesoft.model.platform.resource.Operation;
import net.risesoft.service.permission.cache.Y9PersonToResourceService;
import net.risesoft.service.permission.cache.Y9PositionToResourceService;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;
import net.risesoft.y9public.manager.resource.CompositeResourceManager;
import net.risesoft.y9public.service.resource.Y9MenuService;
import net.risesoft.y9public.service.resource.Y9OperationService;

/**
 * 根据人员权限列表构建前端菜单组件
 * 
 * @author shidaobang
 * @date 2023/07/11
 * @since 9.6.2
 */
@Component
@RequiredArgsConstructor
public class FrontendMenuBuilder {

    private final CompositeResourceManager compositeResourceManager;

    private final Y9MenuService y9MenuService;
    private final Y9OperationService y9OperationService;

    private final Y9PersonToResourceService y9PersonToResourceService;
    private final Y9PositionToResourceService y9PositionToResourceService;

    private FrontendButton buildFrontendButton(Operation operation) {
        FrontendButton button = new FrontendButton();
        button.setName(operation.getName());
        button.setIcon(operation.getIconUrl());
        button.setButtonId(operation.getCustomId());
        button.setDisplayType(operation.getDisplayType());
        button.setUrl(operation.getUrl());
        button.setEventName(operation.getEventName());
        return button;
    }

    private List<FrontendButton> buildFrontendButtons(IdentityTypeEnum identityType, String personId,
        AuthorityEnum authority, String menuId) {
        List<Operation> operationList;
        if (IdentityTypeEnum.PERSON.equals(identityType)) {
            operationList = y9PersonToResourceService.list(personId, menuId, ResourceTypeEnum.OPERATION, authority)
                .stream()
                .map(IdentityToResourceBase::getResourceId)
                .distinct()
                .map(y9OperationService::getById)
                .sorted()
                .collect(Collectors.toList());
        } else {
            operationList = y9PositionToResourceService.list(personId, menuId, ResourceTypeEnum.OPERATION, authority)
                .stream()
                .map(IdentityToResourceBase::getResourceId)
                .distinct()
                .map(y9OperationService::getById)
                .sorted()
                .collect(Collectors.toList());
        }

        List<FrontendButton> buttonList = new ArrayList<>();
        for (Operation operation : operationList) {
            if (operation.getEnabled()) {
                buttonList.add(buildFrontendButton(operation));
            }
        }
        return buttonList;
    }

    private FrontendMenu buildFrontendMenu(IdentityTypeEnum identityType, String personId, AuthorityEnum authority,
        String menuId, Menu menu) {
        FrontendMenu frontendMenu = new FrontendMenu();
        frontendMenu.setName(menu.getName());
        frontendMenu.setPath(menu.getUrl());
        frontendMenu.setRedirect(menu.getRedirect());
        frontendMenu.setComponent(menu.getComponent());
        frontendMenu.setMeta(menu.getMeta());
        frontendMenu.setTarget(menu.getTarget());

        List<FrontendMenu> subFrontendMenuList = new ArrayList<>();
        buildFrontendMenus(identityType, personId, authority, menuId, subFrontendMenuList);
        frontendMenu.setChildren(subFrontendMenuList);

        List<FrontendButton> buttonList = buildFrontendButtons(identityType, personId, authority, menuId);
        frontendMenu.setButtons(buttonList);
        return frontendMenu;
    }

    public void buildFrontendMenus(IdentityTypeEnum identityType, String personId, AuthorityEnum authority,
        String resourceId, List<FrontendMenu> frontendMenuList) {
        List<Menu> menuList;
        if (IdentityTypeEnum.PERSON.equals(identityType)) {
            menuList = y9PersonToResourceService.list(personId, resourceId, ResourceTypeEnum.MENU, authority)
                .stream()
                .map(IdentityToResourceBase::getResourceId)
                .distinct()
                .map(y9MenuService::getById)
                .sorted()
                .collect(Collectors.toList());
        } else {
            menuList = y9PositionToResourceService.list(personId, resourceId, ResourceTypeEnum.MENU, authority)
                .stream()
                .map(IdentityToResourceBase::getResourceId)
                .distinct()
                .map(y9MenuService::getById)
                .sorted()
                .collect(Collectors.toList());
        }
        for (Menu menu : menuList) {
            if (menu.getEnabled()) {
                FrontendMenu frontendMenu = buildFrontendMenu(identityType, personId, authority, menu.getId(), menu);
                frontendMenuList.add(frontendMenu);
            }
        }
    }

    public void buildFrontendMenusByCustomId(IdentityTypeEnum identityTypeEnum, String personId,
        AuthorityEnum authority, String customId, List<FrontendMenu> frontendMenuList) {
        Y9ResourceBase y9ResourceBase = compositeResourceManager.getByCustomId(customId);
        buildFrontendMenus(identityTypeEnum, personId, authority, y9ResourceBase.getId(), frontendMenuList);
    }
}
