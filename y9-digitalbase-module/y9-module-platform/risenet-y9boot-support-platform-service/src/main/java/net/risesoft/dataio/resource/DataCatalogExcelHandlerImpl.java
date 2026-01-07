package net.risesoft.dataio.resource;

import static net.risesoft.consts.JpaPublicConsts.PUBLIC_TRANSACTION_MANAGER;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.OrgLevelConsts;
import net.risesoft.dataio.ExcelImportError;
import net.risesoft.model.platform.resource.DataCatalog;
import net.risesoft.model.platform.resource.Resource;
import net.risesoft.pojo.DataCatalog4Excel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.resource.Y9DataCatalog;
import net.risesoft.y9public.service.resource.Y9DataCatalogService;

import cn.idev.excel.FastExcel;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.read.listener.ReadListener;

/**
 * 数据目录 Excel 导入导出
 *
 * @author shidaobang
 * @date 2026/01/07
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DataCatalogExcelHandlerImpl implements DataCatalogHandler {

    private final Y9DataCatalogService y9DataCatalogService;

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public Y9Result<Object> importData(InputStream inputStream, String treeType) {
        List<ExcelImportError> excelImportErrorList = new ArrayList<>();
        FastExcel.read(inputStream, DataCatalog4Excel.class, new ReadListener<DataCatalog4Excel>() {
            @Override
            public void invoke(DataCatalog4Excel dataCatalog4Excel, AnalysisContext context) {
                try {
                    impData2Db(dataCatalog4Excel, treeType);
                } catch (Exception e) {
                    Integer rowNumber = context.readRowHolder().getRowIndex();
                    excelImportErrorList.add(new ExcelImportError(rowNumber, e.getMessage()));
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {

            }
        }).sheet().doRead();

        if (excelImportErrorList.isEmpty()) {
            return Y9Result.success();
        } else {
            return Y9Result.failure(excelImportErrorList, "导入有错误");
        }
    }

    private void impData2Db(DataCatalog4Excel dataCatalog4Excel, String treeType) {
        String name = StringUtils.trim(dataCatalog4Excel.getName());
        String ancestorNamePath = Optional.ofNullable(dataCatalog4Excel.getAncestorNamePath()).orElse("");

        String[] dataCatalogNames = StringUtils.split(ancestorNamePath, OrgLevelConsts.SEPARATOR);

        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("名称不能为空");
        }

        String currentParentId = null;
        for (int i = 0; i < dataCatalogNames.length; i++) {
            // 查看各个祖先的数据目录是否存在，如果不存在则创建
            String dataCatalogName = dataCatalogNames[i];
            Optional<Y9DataCatalog> y9DataCatalogOptional =
                y9DataCatalogService.findByTreeTypeAndParentIdAndName(treeType, currentParentId, dataCatalogName);
            if (y9DataCatalogOptional.isEmpty()) {
                DataCatalog dataCatalog = new DataCatalog();
                dataCatalog.setName(dataCatalogName);
                dataCatalog.setParentId(currentParentId);
                dataCatalog.setTenantId(Y9LoginUserHolder.getTenantId());
                dataCatalog.setTreeType(treeType);
                DataCatalog savedDataCatalog = y9DataCatalogService.saveOrUpdate(dataCatalog);
                currentParentId = savedDataCatalog.getId();
            } else {
                currentParentId = y9DataCatalogOptional.get().getId();
            }
        }

        // 创建当前的数据目录
        DataCatalog dataCatalog = new DataCatalog();
        dataCatalog.setName(name);
        dataCatalog.setParentId(currentParentId);
        dataCatalog.setTenantId(Y9LoginUserHolder.getTenantId());
        dataCatalog.setTreeType(treeType);
        dataCatalog.setCustomId(dataCatalog4Excel.getCustomId());
        dataCatalog.setDescription(dataCatalog4Excel.getDescription());
        y9DataCatalogService.saveOrUpdate(dataCatalog);
    }

    @Override
    public void exportData(OutputStream outputStream, String treeType, String id) {
        FastExcel.write(outputStream, DataCatalog4Excel.class).sheet().doWrite(getDataCatalog4ExcelList(id, treeType));
    }

    private List<DataCatalog4Excel> getDataCatalog4ExcelList(String id, String treeType) {
        // 当前节点的所有后代节点
        List<DataCatalog> dataCatalogList =
            y9DataCatalogService.getTree(Y9LoginUserHolder.getTenantId(), id, treeType, null, true, null, null);
        if (StringUtils.isNotBlank(id)) {
            // 当前节点
            dataCatalogList.add(0, y9DataCatalogService.getDataCatalogById(id));
        }

        List<DataCatalog4Excel> dataCatalog4ExcelList = new ArrayList<>();
        for (DataCatalog dataCatalog : dataCatalogList) {
            DataCatalog4Excel dataCatalog4Excel = new DataCatalog4Excel();
            dataCatalog4Excel.setName(dataCatalog.getName());
            dataCatalog4Excel.setCustomId(dataCatalog.getCustomId());
            dataCatalog4Excel.setDescription(dataCatalog.getDescription());
            dataCatalog4Excel.setAncestorNamePath(getAncestorNamePath(dataCatalog));
            dataCatalog4ExcelList.add(dataCatalog4Excel);
        }
        return dataCatalog4ExcelList;
    }

    private String getAncestorNamePath(DataCatalog dataCatalog) {
        List<DataCatalog> ancestorDataCatalogList = y9DataCatalogService.getAncestorList(dataCatalog.getParentId());

        // 反转顺序，从根节点到父节点
        Collections.reverse(ancestorDataCatalogList);

        return ancestorDataCatalogList.stream()
            .map(Resource::getName)
            .collect(Collectors.joining(OrgLevelConsts.SEPARATOR));
    }
}
