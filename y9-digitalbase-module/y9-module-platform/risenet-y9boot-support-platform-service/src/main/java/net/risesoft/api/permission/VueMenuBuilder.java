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
import net.risesoft.model.platform.resource.Menu;
import net.risesoft.model.platform.resource.Operation;
import net.risesoft.model.platform.resource.VueButton;
import net.risesoft.model.platform.resource.VueMenu;
import net.risesoft.service.permission.cache.Y9PersonToResourceService;
import net.risesoft.service.permission.cache.Y9PositionToResourceService;
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

    private final Y9PersonToResourceService y9PersonToResourceService;
    private final Y9PositionToResourceService y9PositionToResourceService;

    private VueButton buildVueButton(Operation operation) {
        VueButton button = new VueButton();
        button.setName(operation.getName());
        button.setIcon(operation.getIconUrl());
        button.setButtonId(operation.getCustomId());
        button.setDisplayType(operation.getDisplayType());
        button.setUrl(operation.getUrl());
        button.setEventName(operation.getEventName());
        return button;
    }

    private List<VueButton> buildVueButtons(IdentityTypeEnum identityType, String personId, AuthorityEnum authority,
        String menuId) {
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

        List<VueButton> buttonList = new ArrayList<>();
        for (Operation operation : operationList) {
            if (operation.getEnabled()) {
                buttonList.add(buildVueButton(operation));
            }
        }
        return buttonList;
    }

    private VueMenu buildVueMenu(IdentityTypeEnum identityType, String personId, AuthorityEnum authority, String menuId,
        Menu menu) {
        VueMenu vueMenu = new VueMenu();
        vueMenu.setName(menu.getName());
        vueMenu.setPath(menu.getUrl());
        vueMenu.setRedirect(menu.getRedirect());
        vueMenu.setComponent(menu.getComponent());
        vueMenu.setMeta(menu.getMeta());
        vueMenu.setTarget(menu.getTarget());

        List<VueMenu> subVueMenuList = new ArrayList<>();
        buildVueMenus(identityType, personId, authority, menuId, subVueMenuList);
        vueMenu.setChildren(subVueMenuList);

        List<VueButton> buttonList = buildVueButtons(identityType, personId, authority, menuId);
        vueMenu.setButtons(buttonList);
        return vueMenu;
    }

    public void buildVueMenus(IdentityTypeEnum identityType, String personId, AuthorityEnum authority,
        String resourceId, List<VueMenu> vueMenuList) {
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
                VueMenu vueMenu = buildVueMenu(identityType, personId, authority, menu.getId(), menu);
                vueMenuList.add(vueMenu);
            }
        }
    }
}
