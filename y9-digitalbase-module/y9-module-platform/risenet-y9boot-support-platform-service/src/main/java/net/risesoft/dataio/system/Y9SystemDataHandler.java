package net.risesoft.dataio.system;

import java.io.OutputStream;

import net.risesoft.dataio.system.model.Y9AppJsonModel;
import net.risesoft.dataio.system.model.Y9SystemJsonModel;

/**
 * Y9SystemDataHandler
 *
 * @author shidaobang
 * @date 2025/02/10
 */
public interface Y9SystemDataHandler {

    void exportApp(String appId, OutputStream outStream);

    void exportSystem(String systemId, OutputStream outStream);

    void importApp(Y9AppJsonModel y9AppJsonModel, String systemId);

    void importSystem(Y9SystemJsonModel y9SystemJsonModel);
}
