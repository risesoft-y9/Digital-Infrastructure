package net.risesoft.dataio.system;

import java.io.OutputStream;

import net.risesoft.dataio.system.model.AppJsonModel;
import net.risesoft.dataio.system.model.SystemJsonModel;

/**
 * Y9SystemDataHandler
 *
 * @author shidaobang
 * @date 2025/02/10
 */
public interface SystemDataHandler {

    void exportApp(String appId, OutputStream outStream);

    void exportSystem(String systemId, OutputStream outStream);

    void importApp(AppJsonModel appJsonModel, String systemId);

    void importSystem(SystemJsonModel systemJsonModel);
}
