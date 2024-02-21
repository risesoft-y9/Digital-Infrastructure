package net.risesoft.dataio.system;

import java.io.OutputStream;

import net.risesoft.dataio.system.model.Y9AppExportModel;
import net.risesoft.dataio.system.model.Y9SystemExportModel;

public interface Y9SystemDataHandler {

    void importApp(Y9AppExportModel y9AppExportModel, String systemId);

    void importSystem(Y9SystemExportModel y9SystemExportModel);

    void exportSystem(String systemId, OutputStream outStream);

    void exportApp(String appId, OutputStream outStream);
}
