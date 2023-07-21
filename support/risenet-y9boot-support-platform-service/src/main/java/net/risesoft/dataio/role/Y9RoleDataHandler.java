package net.risesoft.dataio.role;

import java.io.FileNotFoundException;
import java.io.InputStream;

public interface Y9RoleDataHandler {

    String doExport(String resourceId);
    
    void doImport(InputStream inputStream, String rootRoleId) throws FileNotFoundException;
}
