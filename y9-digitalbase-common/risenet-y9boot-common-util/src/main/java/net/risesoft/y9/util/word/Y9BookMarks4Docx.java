package net.risesoft.y9.util.word;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import lombok.extern.slf4j.Slf4j;

/**
 * 利用POI进行Word文件相关的操作，针对docx形式的封装
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Slf4j
public class Y9BookMarks4Docx {

    /** 保存Word文件中定义的标签 **/
    private HashMap<String, Y9BookMark4Docx> bookmarks = null;

    /**
     * 构造函数，用以分析文档，解析出所有的标签
     *
     * @param document Word OOXML document instance.
     */
    public Y9BookMarks4Docx(XWPFDocument document) {
        // 初始化标签缓存
        this.bookmarks = new HashMap<String, Y9BookMark4Docx>();
        // 首先解析文档普通段落中的标签
        this.procParaList(document.getParagraphs());
        // 利用繁琐的方法，从所有的表格中得到得到标签，处理比较原始和简单
        List<XWPFTable> tableList = document.getTables();
        for (XWPFTable table : tableList) {
            // 得到表格的列信息
            List<XWPFTableRow> rowList = table.getRows();
            for (XWPFTableRow row : rowList) {
                // 得到行中的列信息
                List<XWPFTableCell> cellList = row.getTableCells();
                for (XWPFTableCell cell : cellList) {
                    // 逐个解析标签信息
                    // this.procParaList(cell.getParagraphs(), row);
                    this.procParaList(cell);
                }
            }
        }
    }

    /**
     * 根据标签名称，获得标签的相关定义，如果不存在，则返回空
     *
     * @param bookmarkName 标签名称
     * @return 返回封装好的对象
     */
    public Y9BookMark4Docx getBookmark(String bookmarkName) {
        Y9BookMark4Docx bookmark = null;
        if (this.bookmarks.containsKey(bookmarkName)) {
            bookmark = this.bookmarks.get(bookmarkName);
        }
        return bookmark;
    }

    /**
     * 得到所有的标签信息集合
     *
     * @return 缓存的标签信息集合
     */
    public Collection<Y9BookMark4Docx> getBookmarkList() {
        return (this.bookmarks.values());
    }

    /**
     * 返回文档中的标签名称迭代器
     *
     * @return 由Map KEY 转换的迭代器
     */
    public Iterator<String> getNameIterator() {
        return (this.bookmarks.keySet().iterator());
    }

    /**
     * 解析普通段落中的标签
     *
     * @param paragraphList 传入的段落
     */
    private void procParaList(List<XWPFParagraph> paragraphList) {
        for (XWPFParagraph paragraph : paragraphList) {
            List<CTBookmark> bookmarkList = paragraph.getCTP().getBookmarkStartList();
            // 循环加入标签
            for (CTBookmark bookmark : bookmarkList) {
                if ("_GoBack".equals(bookmark.getName())) {
                    continue;
                }
                this.bookmarks.put(bookmark.getName(), new Y9BookMark4Docx(bookmark, paragraph));
            }
        }
    }

    /**
     * 解析表格中的标签
     *
     * @param paragraphList 传入的段落列表
     * @param tableRow 对应的表格行对象
     */
    public void procParaList(List<XWPFParagraph> paragraphList, XWPFTableRow tableRow) {
        NamedNodeMap attributes = null;
        Node colFirstNode = null;
        Node colLastNode = null;
        int firstColIndex = 0;
        int lastColIndex = 0;
        // 循环判断，解析段落中的标签
        for (XWPFParagraph paragraph : paragraphList) {
            // 得到段落中的标签标记
            List<CTBookmark> bookmarkList = paragraph.getCTP().getBookmarkStartList();
            for (CTBookmark bookmark : bookmarkList) {
                if ("_GoBack".equals(bookmark.getName())) {
                    continue;
                }
                attributes = bookmark.getDomNode().getAttributes();
                if (attributes != null) {
                    colFirstNode = attributes.getNamedItem("w:colFirst");
                    colLastNode = attributes.getNamedItem("w:colLast");
                    if (colFirstNode != null && colLastNode != null) {
                        firstColIndex = Integer.parseInt(colFirstNode.getNodeValue());
                        lastColIndex = Integer.parseInt(colLastNode.getNodeValue());
                        if (firstColIndex == lastColIndex) {
                            this.bookmarks.put(bookmark.getName(),
                                new Y9BookMark4Docx(bookmark, paragraph, tableRow.getCell(firstColIndex)));
                        } else {
                            LOGGER.info(
                                "This bookmark {} identifies a number of cells in the table. That condition is not handled yet.",
                                bookmark.getName());
                        }
                    } else {
                        this.bookmarks.put(bookmark.getName(),
                            new Y9BookMark4Docx(bookmark, paragraph, tableRow.getCell(1)));
                    }
                } else {
                    this.bookmarks.put(bookmark.getName(),
                        new Y9BookMark4Docx(bookmark, paragraph, tableRow.getCell(1)));
                }
            }
        }
    }

    private void procParaList(XWPFTableCell cell) {
        List<XWPFParagraph> paragraphList = cell.getParagraphs();
        for (XWPFParagraph paragraph : paragraphList) {
            // 得到段落中的标签标记
            List<CTBookmark> bookmarkList = paragraph.getCTP().getBookmarkStartList();
            for (CTBookmark bookmark : bookmarkList) {
                if ("_GoBack".equals(bookmark.getName())) {
                    continue;
                }
                this.bookmarks.put(bookmark.getName(), new Y9BookMark4Docx(bookmark, paragraph, cell));
            }
        }
    }
}