package net.risesoft.dataio.resource;

import java.io.InputStream;
import java.io.OutputStream;

import net.risesoft.pojo.Y9Result;

/**
 * DataCatalogHandler
 *
 * @author shidaobang
 * @date 2026/01/07
 */
public interface DataCatalogHandler {

    Y9Result<Object> importData(InputStream inputStream, String treeType);

    void exportData(OutputStream outputStream, String treeType, String id);
}
