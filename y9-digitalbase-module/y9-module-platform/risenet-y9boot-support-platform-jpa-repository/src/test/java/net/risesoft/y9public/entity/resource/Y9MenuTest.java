package net.risesoft.y9public.entity.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.risesoft.model.platform.resource.Menu;

class Y9MenuTest {

    @Test
    void testConstructorBuildsGuidPathFromParent() {
        Y9App parentApp = new Y9App();
        parentApp.setId("app-1");
        parentApp.setGuidPath("system-1,app-1");

        Menu menu = new Menu();
        menu.setName("菜单");
        menu.setAppId("app-1");
        menu.setParentId("app-1");

        Y9Menu y9Menu = new Y9Menu(menu, parentApp, 4);

        assertNotNull(y9Menu.getId());
        assertFalse(y9Menu.getId().isBlank());
        assertEquals("菜单", y9Menu.getName());
        assertEquals("app-1", y9Menu.getAppId());
        assertEquals("app-1", y9Menu.getParentId());
        assertEquals(4, y9Menu.getTabIndex());
        assertEquals(parentApp.getGuidPath() + "," + y9Menu.getId(), y9Menu.getGuidPath());
    }

    @Test
    void testUpdateAndStatusChanges() {
        Y9Menu y9Menu = new Y9Menu();
        y9Menu.setId("menu-1");
        y9Menu.setName("旧菜单");
        y9Menu.setDescription("旧描述");
        y9Menu.setAppId("app-1");
        y9Menu.setParentId("app-1");
        y9Menu.setEnabled(Boolean.TRUE);
        y9Menu.setGuidPath("system-1,app-1,menu-1");
        y9Menu.setTabIndex(1);

        Y9Menu parentMenu = new Y9Menu();
        parentMenu.setId("menu-parent");
        parentMenu.setGuidPath("system-1,app-1,menu-parent");

        Menu updateMenu = new Menu();
        updateMenu.setName("新菜单");
        updateMenu.setParentId("menu-parent");
        updateMenu.setTarget("_blank");

        y9Menu.update(updateMenu, parentMenu);

        assertEquals("menu-1", y9Menu.getId());
        assertEquals("新菜单", y9Menu.getName());
        assertEquals("旧描述", y9Menu.getDescription());
        assertEquals("menu-parent", y9Menu.getParentId());
        assertEquals("_blank", y9Menu.getTarget());
        assertEquals(parentMenu.getGuidPath() + ",menu-1", y9Menu.getGuidPath());

        y9Menu.disable();
        assertFalse(y9Menu.getEnabled());

        y9Menu.enable();
        assertTrue(y9Menu.getEnabled());

        y9Menu.changeTabIndex(8);
        assertEquals(8, y9Menu.getTabIndex());
    }
}
