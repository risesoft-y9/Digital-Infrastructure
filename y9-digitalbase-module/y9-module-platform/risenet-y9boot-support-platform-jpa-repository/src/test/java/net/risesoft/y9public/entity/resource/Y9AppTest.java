package net.risesoft.y9public.entity.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.risesoft.enums.platform.resource.AppTypeEnum;
import net.risesoft.model.platform.resource.App;

class Y9AppTest {

    @Test
    void testConstructorInitializesBusinessRules() {
        App app = new App();
        app.setName("测试应用");
        app.setSystemId("system-1");
        app.setType(AppTypeEnum.DATA_SERVICE);
        app.setChecked(Boolean.TRUE);

        Y9App y9App = new Y9App(app, 6);

        assertNotNull(y9App.getId());
        assertFalse(y9App.getId().isBlank());
        assertEquals("测试应用", y9App.getName());
        assertEquals("system-1", y9App.getSystemId());
        assertEquals(AppTypeEnum.DATA_SERVICE, y9App.getType());
        assertEquals(6, y9App.getTabIndex());
        assertFalse(y9App.getChecked());
        assertEquals(y9App.getId(), y9App.getGuidPath());
    }

    @Test
    void testUpdateVerifyAndStatusChanges() {
        Y9App y9App = new Y9App();
        y9App.setId("app-1");
        y9App.setName("原应用");
        y9App.setDescription("原描述");
        y9App.setChecked(Boolean.TRUE);
        y9App.setVerifyUserName("审核人");
        y9App.setGuidPath("old-guid");
        y9App.setEnabled(Boolean.TRUE);
        y9App.setTabIndex(1);

        App updateApp = new App();
        updateApp.setName("新应用");

        y9App.update(updateApp);

        assertEquals("app-1", y9App.getId());
        assertEquals("新应用", y9App.getName());
        assertEquals("原描述", y9App.getDescription());
        assertFalse(y9App.getChecked());
        assertEquals("审核人", y9App.getVerifyUserName());
        assertEquals("app-1", y9App.getGuidPath());

        y9App.verify(true, "管理员");
        assertTrue(y9App.getChecked());
        assertEquals("管理员", y9App.getVerifyUserName());

        y9App.disable();
        assertFalse(y9App.getEnabled());

        y9App.enable();
        assertTrue(y9App.getEnabled());

        y9App.changeTabIndex(9);
        assertEquals(9, y9App.getTabIndex());
    }
}
