package net.risesoft.dataio.org;

import java.io.InputStream;
import java.io.OutputStream;

import net.risesoft.pojo.Y9Result;

public interface Y9OrgTreeDataHandler {

    Y9Result<Object> importOrgTree(InputStream inputStream, String orgId);

    Y9Result<Object> importPerson(InputStream inputStream, String orgId);

    void exportOrgTree(String orgId, OutputStream outputStream);

    void exportPerson(String orgBaseId, OutputStream outputStream);
}
