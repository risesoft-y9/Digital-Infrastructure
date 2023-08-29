package net.risesoft.dataio.role;

import java.io.InputStream;
import java.io.OutputStream;

public interface Y9RoleDataHandler {

    void doExport(String resourceId, OutputStream outputStream);

    void doImport(InputStream inputStream, String rootRoleId);
}
