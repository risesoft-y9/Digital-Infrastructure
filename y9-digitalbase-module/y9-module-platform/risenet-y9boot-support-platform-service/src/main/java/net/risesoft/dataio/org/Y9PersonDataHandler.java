package net.risesoft.dataio.org;

import java.io.InputStream;
import java.io.OutputStream;

import net.risesoft.pojo.Y9Result;

/**
 * Y9PersonDataHandler
 *
 * @author shidaobang
 * @date 2025/02/10
 */
public interface Y9PersonDataHandler {

    void exportPerson(OutputStream outputStream, String orgBaseId);

    Y9Result<Object> importPerson(InputStream inputStream, String orgId);
}
