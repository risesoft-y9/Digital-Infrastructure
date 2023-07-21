package net.risesoft.dataio.system;

import net.risesoft.dataio.system.model.Y9AppExportModel;
import net.risesoft.dataio.system.model.Y9SystemExportModel;

public interface Y9SystemDataHandler {

    Y9AppExportModel buildApp(String appId);

    Y9SystemExportModel buildSystem(String systemId);

    void importApp(Y9AppExportModel y9AppExportModel);

    void importSystem(Y9SystemExportModel y9SystemExportModel);
}
