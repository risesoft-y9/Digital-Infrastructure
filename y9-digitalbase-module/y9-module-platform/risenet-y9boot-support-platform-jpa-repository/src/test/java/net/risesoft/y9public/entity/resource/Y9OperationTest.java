package net.risesoft.y9public.entity.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.risesoft.model.platform.resource.Operation;

class Y9OperationTest {

    @Test
    void testConstructorBuildsGuidPathFromParent() {
        Y9Menu parentMenu = new Y9Menu();
        parentMenu.setId("menu-1");
        parentMenu.setGuidPath("system-1,app-1,menu-1");

        Operation operation = new Operation();
        operation.setName("查看");
        operation.setAppId("app-1");
        operation.setParentId("menu-1");
        operation.setEventName("onView");

        Y9Operation y9Operation = new Y9Operation(operation, parentMenu, 5);

        assertNotNull(y9Operation.getId());
        assertFalse(y9Operation.getId().isBlank());
        assertEquals("查看", y9Operation.getName());
        assertEquals("app-1", y9Operation.getAppId());
        assertEquals("menu-1", y9Operation.getParentId());
        assertEquals("onView", y9Operation.getEventName());
        assertEquals(5, y9Operation.getTabIndex());
        assertEquals(parentMenu.getGuidPath() + "," + y9Operation.getId(), y9Operation.getGuidPath());
    }

    @Test
    void testUpdateAndStatusChanges() {
        Y9Operation y9Operation = new Y9Operation();
        y9Operation.setId("operation-1");
        y9Operation.setName("旧按钮");
        y9Operation.setDescription("旧描述");
        y9Operation.setAppId("app-1");
        y9Operation.setParentId("menu-1");
        y9Operation.setEnabled(Boolean.TRUE);
        y9Operation.setGuidPath("system-1,app-1,menu-1,operation-1");
        y9Operation.setTabIndex(2);

        Y9Menu parentMenu = new Y9Menu();
        parentMenu.setId("menu-2");
        parentMenu.setGuidPath("system-1,app-1,menu-2");

        Operation updateOperation = new Operation();
        updateOperation.setName("新按钮");
        updateOperation.setParentId("menu-2");
        updateOperation.setEventName("onSave");

        y9Operation.update(updateOperation, parentMenu);

        assertEquals("operation-1", y9Operation.getId());
        assertEquals("新按钮", y9Operation.getName());
        assertEquals("旧描述", y9Operation.getDescription());
        assertEquals("menu-2", y9Operation.getParentId());
        assertEquals("onSave", y9Operation.getEventName());
        assertEquals(parentMenu.getGuidPath() + ",operation-1", y9Operation.getGuidPath());

        y9Operation.disable();
        assertFalse(y9Operation.getEnabled());

        y9Operation.enable();
        assertTrue(y9Operation.getEnabled());

        y9Operation.changeTabIndex(10);
        assertEquals(10, y9Operation.getTabIndex());
    }
}
