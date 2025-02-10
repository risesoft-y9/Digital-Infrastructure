package net.risesoft.dataio.org;

import java.io.OutputStream;

import net.risesoft.dataio.org.model.Y9OrganizationJsonModel;

/**
 * Y9OrgTreeDataHandler
 *
 * @author shidaobang
 * @date 2025/02/10
 */
public interface Y9OrgTreeDataHandler {

    void exportOrgTree(String orgId, OutputStream outputStream);

    void importOrgTree(Y9OrganizationJsonModel y9OrganizationJsonModel);
}
