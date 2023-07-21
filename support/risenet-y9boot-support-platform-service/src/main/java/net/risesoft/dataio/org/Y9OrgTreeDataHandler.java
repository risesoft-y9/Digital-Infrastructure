package net.risesoft.dataio.org;

import java.io.InputStream;
import java.util.Map;

import net.risesoft.pojo.Y9Result;

public interface Y9OrgTreeDataHandler {
    String doExport(String orgBaseId);

    Y9Result<Object> doImport(InputStream inputStream);

    Y9Result<Object> impXlsData(InputStream dataInputStream, InputStream xmlInputStream, String orgId);

    Map<String, Object> xlsData(String organizationId);

    Map<String, Object> xlsPersonData(String orgBaseId);
}
