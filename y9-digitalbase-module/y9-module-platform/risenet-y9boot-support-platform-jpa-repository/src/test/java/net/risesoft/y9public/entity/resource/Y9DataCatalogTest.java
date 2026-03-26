package net.risesoft.y9public.entity.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.enums.platform.resource.DataCatalogTypeEnum;
import net.risesoft.model.platform.resource.DataCatalog;

class Y9DataCatalogTest {

    @Test
    void testConstructorWithParentInitializesBusinessRules() {
        Y9DataCatalog parent = new Y9DataCatalog();
        parent.setId("catalog-parent");
        parent.setGuidPath("catalog-root,catalog-parent");

        DataCatalog dataCatalog = new DataCatalog();
        dataCatalog.setName("2026");
        dataCatalog.setParentId("catalog-parent");
        dataCatalog.setTreeType("archive");
        dataCatalog.setType(DataCatalogTypeEnum.YEAR);

        Y9DataCatalog y9DataCatalog = new Y9DataCatalog(dataCatalog, parent, 7, "tenant-1");

        assertNotNull(y9DataCatalog.getId());
        assertFalse(y9DataCatalog.getId().isBlank());
        assertEquals("2026", y9DataCatalog.getName());
        assertEquals("catalog-parent", y9DataCatalog.getParentId());
        assertEquals("archive", y9DataCatalog.getTreeType());
        assertEquals(DataCatalogTypeEnum.YEAR, y9DataCatalog.getType());
        assertEquals("tenant-1", y9DataCatalog.getTenantId());
        assertEquals(InitDataConsts.SYSTEM_ID, y9DataCatalog.getSystemId());
        assertTrue(y9DataCatalog.getInherit());
        assertEquals(7, y9DataCatalog.getTabIndex());
        assertEquals(parent.getGuidPath() + "," + y9DataCatalog.getId(), y9DataCatalog.getGuidPath());
    }

    @Test
    void testConstructorWithoutParentAndUpdate() {
        DataCatalog rootCatalog = new DataCatalog();
        rootCatalog.setName("根目录");
        rootCatalog.setParentId("");
        rootCatalog.setTreeType("archive");

        Y9DataCatalog y9DataCatalog = new Y9DataCatalog(rootCatalog, null, 3, "tenant-1");

        assertNull(y9DataCatalog.getParentId());
        assertEquals(3, y9DataCatalog.getTabIndex());
        assertEquals(y9DataCatalog.getId(), y9DataCatalog.getGuidPath());

        y9DataCatalog.setTenantId("tenant-1");
        y9DataCatalog.setSystemId(InitDataConsts.SYSTEM_ID);
        y9DataCatalog.setInherit(Boolean.TRUE);
        y9DataCatalog.setEnabled(Boolean.TRUE);

        DataCatalog updateCatalog = new DataCatalog();
        updateCatalog.setName("已归档");
        updateCatalog.setParentId("");

        y9DataCatalog.update(updateCatalog, null);

        assertEquals("已归档", y9DataCatalog.getName());
        assertNull(y9DataCatalog.getParentId());
        assertEquals(y9DataCatalog.getId(), y9DataCatalog.getGuidPath());
        assertEquals("tenant-1", y9DataCatalog.getTenantId());
        assertEquals(InitDataConsts.SYSTEM_ID, y9DataCatalog.getSystemId());
        assertTrue(y9DataCatalog.getInherit());

        y9DataCatalog.disable();
        assertFalse(y9DataCatalog.getEnabled());

        y9DataCatalog.enable();
        assertTrue(y9DataCatalog.getEnabled());
    }
}
