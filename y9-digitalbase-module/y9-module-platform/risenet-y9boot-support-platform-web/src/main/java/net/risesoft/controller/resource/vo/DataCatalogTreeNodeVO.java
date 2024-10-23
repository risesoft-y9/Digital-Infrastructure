package net.risesoft.controller.resource.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.enums.TreeTypeEnum;
import net.risesoft.model.platform.DataCatalog;
import net.risesoft.pojo.TreeNodeVO;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 数据目录树节点
 *
 * @author shidaobang
 * @date 2024/10/09
 * @since 9.6.8
 */
@Getter
@Setter
public class DataCatalogTreeNodeVO extends TreeNodeVO {

    private static final long serialVersionUID = 2699938316097655791L;

    /**
     * 所属数据目录树类型
     */
    private String dataCatalogTreeType;

    /**
     * 数据目录类型
     */
    private String dataCatalogType;

    /**
     * 是否启用
     */
    private Boolean enabled;

    public static List<DataCatalogTreeNodeVO> convertDataCatalogList(List<DataCatalog> dataCatalogList) {
        List<DataCatalogTreeNodeVO> list = new ArrayList<>();
        for (DataCatalog dataCatalog : dataCatalogList) {
            list.add(convertDataCatalog(dataCatalog));
        }
        return list;
    }

    private static DataCatalogTreeNodeVO convertDataCatalog(DataCatalog dataCatalog) {
        DataCatalogTreeNodeVO dataCatalogTreeNodeVO = new DataCatalogTreeNodeVO();
        Y9BeanUtil.copyProperties(dataCatalog, dataCatalogTreeNodeVO);
        dataCatalogTreeNodeVO.setDataCatalogTreeType(dataCatalog.getTreeType());
        dataCatalogTreeNodeVO.setDataCatalogType(dataCatalog.getType().getValue());
        return dataCatalogTreeNodeVO;
    }

    @Override
    public TreeTypeEnum getTreeType() {
        return TreeTypeEnum.DATA_CATALOG;
    }
}
