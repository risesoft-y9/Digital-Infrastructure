package net.risesoft.dataio;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jxls.area.Area;
import org.jxls.command.AbstractCommand;
import org.jxls.command.Command;
import org.jxls.common.CellRef;
import org.jxls.common.Context;
import org.jxls.common.Size;
import org.jxls.transform.Transformer;
import org.jxls.transform.jexcel.JexcelTransformer;
import org.jxls.transform.poi.PoiCellData;
import org.jxls.transform.poi.PoiTransformer;

import jxl.write.WriteException;

/**
 *
 * @author mengjuhua
 *
 */
public class MergeCommand extends AbstractCommand {
    private String cols;
    private String rows;
    private CellStyle cellStyle;
    private Area area;

    private static void setRegionStyle(CellStyle cs, CellRangeAddress region, Sheet sheet) {
        for (int i = region.getFirstRow(); i <= region.getLastRow(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                row = sheet.createRow(i);
            }
            for (int j = region.getFirstColumn(); j <= region.getLastColumn(); j++) {
                Cell cell = row.getCell(j);
                if (cell == null) {
                    cell = row.createCell(j);
                }
                cell.setCellStyle(cs);
            }
        }
    }

    @Override
    public Command addArea(Area area) {
        if (super.getAreaList().size() >= 1) {
            throw new IllegalArgumentException("You can add only a single area to 'merge' command");
        }
        this.area = area;
        return super.addArea(area);
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public CellStyle getCellStyle() {
        return cellStyle;
    }

    public void setCellStyle(CellStyle cellStyle) {
        this.cellStyle = cellStyle;
    }

    public String getCols() {
        return cols;
    }

    public void setCols(String cols) {
        this.cols = cols;
    }

    @Override
    public String getName() {
        return "merge";
    }

    @Override
    public Size applyAt(CellRef cellRef, Context context) {
        int rowsAt = 1;
        int colsAt = 1;
        if (StringUtils.isNotBlank(this.rows)) {
            Object rowsObj = getTransformationConfig().getExpressionEvaluator().evaluate(this.rows, context.toMap());
            if (rowsObj != null && NumberUtils.isDigits(rowsObj.toString())) {
                rowsAt = NumberUtils.toInt(rowsObj.toString());
            }
        }
        if (StringUtils.isNotBlank(this.cols)) {
            Object colsObj = getTransformationConfig().getExpressionEvaluator().evaluate(this.cols, context.toMap());
            if (colsObj != null && NumberUtils.isDigits(colsObj.toString())) {
                colsAt = NumberUtils.toInt(colsObj.toString());
            }
        }

        if (rowsAt > 1 || colsAt > 1) {
            Transformer transformer = this.getTransformer();
            if (transformer instanceof PoiTransformer) {
                return poiMerge(cellRef, context, (PoiTransformer)transformer, rowsAt, colsAt);
            } else if (transformer instanceof JexcelTransformer) {
                return jexcelMerge(cellRef, context, (JexcelTransformer)transformer, rowsAt, colsAt);
            }
        }
        area.applyAt(cellRef, context);
        return new Size(colsAt, rowsAt);
    }

    public String getRows() {
        return rows;
    }

    public void setRows(String rows) {
        this.rows = rows;
    }

    protected Size jexcelMerge(CellRef cellRef, Context context, JexcelTransformer transformer, int rows, int cols) {
        try {
            transformer.getWritableWorkbook().getSheet(cellRef.getSheetName()).mergeCells(cellRef.getRow(),
                cellRef.getCol(), cellRef.getRow() + rows - 1, cellRef.getCol() + cols - 1);
            area.applyAt(cellRef, context);
        } catch (WriteException e) {
            throw new IllegalArgumentException("合并单元格失败");
        }
        return new Size(cols, rows);
    }

    protected Size poiMerge(CellRef cellRef, Context context, PoiTransformer transformer, int rows, int cols) {
        Sheet sheet = transformer.getWorkbook().getSheet(cellRef.getSheetName());
        CellRangeAddress region = new CellRangeAddress(cellRef.getRow(), cellRef.getRow() + rows - 1, cellRef.getCol(),
            cellRef.getCol() + cols - 1);
        sheet.addMergedRegion(region);

        // 合并之后单元格样式会丢失，以下操作将合并后的单元格恢复成合并前第一个单元格的样式
        area.applyAt(cellRef, context);
        if (cellStyle == null) {
            PoiCellData cellData = (PoiCellData)transformer.getCellData(cellRef);
            if (cellData != null) {
                cellStyle = cellData.getCellStyle();
            }
        }
        setRegionStyle(cellStyle, region, sheet);
        return new Size(cols, rows);
    }

}
