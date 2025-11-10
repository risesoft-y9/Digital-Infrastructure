package net.risesoft.dataio.org;

import java.io.OutputStream;

import net.risesoft.dataio.org.model.OrganizationJsonModel;

/**
 * Y9OrgTreeDataHandler
 *
 * @author shidaobang
 * @date 2025/02/10
 */
public interface Y9OrgTreeDataHandler {

    void exportOrgTree(String orgId, OutputStream outputStream);

    void importOrgTree(OrganizationJsonModel organizationJsonModel);
}
