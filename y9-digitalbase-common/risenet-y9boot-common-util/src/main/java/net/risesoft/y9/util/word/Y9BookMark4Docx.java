package net.risesoft.y9.util.word;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Word 文件中标签的封装类，保存了其定义和内部的操作
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public class Y9BookMark4Docx {

    /** 替换标签时，设于标签的后面 **/
    public static final int INSERT_AFTER = 0;

    /** 替换标签时，设于标签的前面 **/
    public static final int INSERT_BEFORE = 1;

    /** 替换标签时，将内容替换书签 **/
    public static final int REPLACE = 2;

    /** docx中定义的部分常量引用 **/
    public static final String RUN_NODE_NAME = "w:r";
    public static final String TEXT_NODE_NAME = "w:t";
    public static final String BOOKMARK_START_TAG = "bookmarkStart";
    public static final String BOOKMARK_END_TAG = "bookmarkEnd";
    public static final String BOOKMARK_ID_ATTR_NAME = "w:id";
    public static final String STYLE_NODE_NAME = "w:rPr";

    /** 内部的标签定义类 **/
    private CTBookmark ctBookmark = null;

    /** 标签所处的段落 **/
    private XWPFParagraph para = null;

    /** 标签所在的表cell对象 **/
    private XWPFTableCell tableCell = null;

    /** 标签名称 **/
    private String bookmarkName = null;

    /** 该标签是否处于表格内 **/
    private boolean isCell = false;

    /**
     * 构造函数
     *
     * @param ctBookmark 内部的标签定义类
     * @param para 标签所处的段落
     */
    public Y9BookMark4Docx(CTBookmark ctBookmark, XWPFParagraph para) {
        this.ctBookmark = ctBookmark;
        this.para = para;
        this.bookmarkName = ctBookmark.getName();
        this.tableCell = null;
        this.isCell = false;
    }

    /**
     * 构造函数，用于表格中的标签
     *
     * @param ctBookmark 内部的标签定义类
     * @param para 标签所处的段落
     * @param tableCell 标签所在的表cell对象
     */
    public Y9BookMark4Docx(CTBookmark ctBookmark, XWPFParagraph para, XWPFTableCell tableCell) {
        this(ctBookmark, para);
        this.tableCell = tableCell;
        this.isCell = true;
    }

    public static void main(String[] args) {
        try {
            FileInputStream inputStreamdoc = new FileInputStream("D:/qm1.docx");
            OutputStream out = new FileOutputStream("D:/qm2.docx");
            XWPFDocument document = new XWPFDocument(inputStreamdoc);
            Y9BookMarks4Docx bookMarks = new Y9BookMarks4Docx(document);
            // 循环进行替换
            Iterator<String> bookMarkIter = bookMarks.getNameIterator();
            while (bookMarkIter.hasNext()) {
                String bookMarkName = bookMarkIter.next();
                // 得到标签名称
                System.out.println("=======bookMarkName:" + bookMarkName);
                Y9BookMark4Docx bookMark = bookMarks.getBookmark(bookMarkName);
                // 进行替换
                bookMark.insertPicAtBookMark(new FileInputStream("D:/1.png"), XWPFDocument.PICTURE_TYPE_PNG, "2.png",
                    Units.toEMU(100), Units.toEMU(100), Y9BookMark4Docx.REPLACE);
            }
            document.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void deleteChildNodes(Stack<Node> nodeStack) {
        Node toDelete = null;
        int bookmarkStartId = 0;
        int bookmarkEndId = 0;
        boolean inNestedBookmark = false;
        for (int i = 1; i < nodeStack.size(); i++) {
            toDelete = nodeStack.elementAt(i);
            if (toDelete.getNodeName().contains(Y9BookMark4Docx.BOOKMARK_START_TAG)) {
                bookmarkStartId = Integer.parseInt(
                    toDelete.getAttributes().getNamedItem(Y9BookMark4Docx.BOOKMARK_ID_ATTR_NAME).getNodeValue());
                inNestedBookmark = true;
            } else if (toDelete.getNodeName().contains(Y9BookMark4Docx.BOOKMARK_END_TAG)) {
                bookmarkEndId = Integer.parseInt(
                    toDelete.getAttributes().getNamedItem(Y9BookMark4Docx.BOOKMARK_ID_ATTR_NAME).getNodeValue());
                if (bookmarkEndId == bookmarkStartId) {
                    inNestedBookmark = false;
                }
            } else {
                if (!inNestedBookmark) {
                    this.para.getCTP().getDomNode().removeChild(toDelete);
                }
            }
        }
    }

    public String getBookmarkName() {
        return this.bookmarkName;
    }

    public String getBookmarkText() throws XmlException {
        StringBuilder builder = null;
        if (this.tableCell != null) {
            builder = new StringBuilder(this.tableCell.getText());
        } else {
            builder = this.getTextFromBookmark();
        }
        return (builder == null ? null : builder.toString());
    }

    public XWPFTable getContainerTable() {
        return this.tableCell.getTableRow().getTable();
    }

    public XWPFTableRow getContainerTableRow() {
        return this.tableCell.getTableRow();
    }

    private Node getStyleNode(Node parentNode) {
        Node childNode = null;
        Node styleNode = null;
        if (parentNode != null) {

            if (parentNode.getNodeName().equalsIgnoreCase(Y9BookMark4Docx.RUN_NODE_NAME)
                && parentNode.hasChildNodes()) {
                childNode = parentNode.getFirstChild();
                if ("w:rPr".equals(childNode.getNodeName())) {
                    styleNode = childNode;
                } else {
                    while ((childNode = childNode.getNextSibling()) != null) {
                        if (childNode.getNodeName().equals(Y9BookMark4Docx.STYLE_NODE_NAME)) {
                            styleNode = childNode;
                            childNode = null;
                        }
                    }
                }
            }
        }
        return (styleNode);
    }

    private StringBuilder getTextFromBookmark() throws XmlException {
        int startBookmarkId = 0;
        int endBookmarkId = -1;
        Node nextNode = null;
        StringBuilder builder = null;
        startBookmarkId = this.ctBookmark.getId().intValue();
        nextNode = this.ctBookmark.getDomNode();
        builder = new StringBuilder();

        while (startBookmarkId != endBookmarkId) {

            nextNode = nextNode.getNextSibling();
            if (nextNode.getNodeName().contains(Y9BookMark4Docx.BOOKMARK_END_TAG)) {
                try {
                    endBookmarkId = Integer.parseInt(
                        nextNode.getAttributes().getNamedItem(Y9BookMark4Docx.BOOKMARK_ID_ATTR_NAME).getNodeValue());
                } catch (NumberFormatException nfe) {
                    endBookmarkId = startBookmarkId;
                }
            } else {
                if (nextNode.getNodeName().equals(Y9BookMark4Docx.RUN_NODE_NAME) && nextNode.hasChildNodes()) {
                    builder.append(this.getTextFromChildNodes(nextNode));
                }
            }
        }
        return (builder);
    }

    private String getTextFromChildNodes(Node node) throws XmlException {
        NodeList childNodes = null;
        Node childNode = null;
        CTText text = null;
        StringBuilder builder = new StringBuilder();
        int numChildNodes = 0;

        childNodes = node.getChildNodes();
        numChildNodes = childNodes.getLength();

        for (int i = 0; i < numChildNodes; i++) {
            childNode = childNodes.item(i);

            if (childNode.getNodeName().equals(Y9BookMark4Docx.TEXT_NODE_NAME)) {
                if (childNode.getNodeType() == Node.TEXT_NODE) {
                    builder.append(childNode.getNodeValue());
                } else {
                    text = CTText.Factory.parse(childNode);
                    builder.append(text.getStringValue());
                }
            }
        }
        return (builder.toString());
    }

    private void handleBookmarkedCells(String bookmarkValue, int where) {
        List<XWPFParagraph> paraList = null;
        XWPFParagraph para = null;
        paraList = this.tableCell.getParagraphs();
        for (int i = 0; i < paraList.size(); i++) {
            this.tableCell.removeParagraph(i);
        }
        para = this.tableCell.addParagraph();
        para.createRun().setText(bookmarkValue);
    }

    private void handleBookmarkedCells4pic(InputStream pictureData, int pictureType, String filename, int width,
        int height, int where) throws InvalidFormatException, IOException {
        List<XWPFParagraph> paraList = null;
        XWPFParagraph para = null;
        paraList = this.tableCell.getParagraphs();
        for (int i = 0; i < paraList.size(); i++) {
            this.tableCell.removeParagraph(i);
        }
        para = this.tableCell.addParagraph();
        para.createRun().addPicture(pictureData, pictureType, filename, width, height);
    }

    private void insertAfterBookmark(XWPFRun run) {
        Node nextNode = null;
        Node insertBeforeNode = null;
        Node styleNode = null;
        int bookmarkStartId = 0;
        int bookmarkEndId = -1;
        bookmarkStartId = this.ctBookmark.getId().intValue();
        nextNode = this.ctBookmark.getDomNode();
        while (bookmarkStartId != bookmarkEndId) {
            nextNode = nextNode.getNextSibling();
            if (nextNode.getNodeName().contains(Y9BookMark4Docx.BOOKMARK_END_TAG)) {
                try {
                    bookmarkEndId = Integer.parseInt(
                        nextNode.getAttributes().getNamedItem(Y9BookMark4Docx.BOOKMARK_ID_ATTR_NAME).getNodeValue());
                } catch (NumberFormatException nfe) {
                    bookmarkEndId = bookmarkStartId;
                }
            } else {
                if (nextNode.getNodeName().equals(Y9BookMark4Docx.RUN_NODE_NAME)) {
                    styleNode = this.getStyleNode(nextNode);
                }
            }
        }

        insertBeforeNode = nextNode.getNextSibling();

        if (styleNode != null) {
            run.getCTR().getDomNode().insertBefore(styleNode.cloneNode(true),
                run.getCTR().getDomNode().getFirstChild());
        }
        if (insertBeforeNode != null) {
            this.para.getCTP().getDomNode().insertBefore(run.getCTR().getDomNode(), insertBeforeNode);
        }
    }

    private void insertBeforeBookmark(XWPFRun run) {
        Node insertBeforeNode = null;
        Node childNode = null;
        Node styleNode = null;
        insertBeforeNode = this.ctBookmark.getDomNode();
        childNode = insertBeforeNode.getPreviousSibling();
        if (childNode != null) {
            styleNode = this.getStyleNode(childNode);
            if (styleNode != null) {
                run.getCTR().getDomNode().insertBefore(styleNode.cloneNode(true),
                    run.getCTR().getDomNode().getFirstChild());
            }
        }
        this.para.getCTP().getDomNode().insertBefore(run.getCTR().getDomNode(), insertBeforeNode);
    }

    public void insertTextAtBookMark(String bookmarkValue, int where) {
        // 根据标签的类型，进行不同的操作
        if (this.isCell) {
            this.handleBookmarkedCells(bookmarkValue, where);
        } else {
            // 普通标签，直接创建一个元素
            XWPFRun run = this.para.createRun();
            run.setText(bookmarkValue);
            switch (where) {
                case Y9BookMark4Docx.INSERT_AFTER:
                    this.insertAfterBookmark(run);
                    break;
                case Y9BookMark4Docx.INSERT_BEFORE:
                    this.insertBeforeBookmark(run);
                    break;
                case Y9BookMark4Docx.REPLACE:
                    this.replaceBookmark(run);
                    break;
                default:
            }
        }
    }

    public void insertPicAtBookMark(InputStream pictureData, int pictureType, String filename, int width, int height,
        int where) throws InvalidFormatException, IOException {
        // 根据标签的类型，进行不同的操作
        if (this.isCell) {
            handleBookmarkedCells4pic(pictureData, pictureType, filename, width, height, where);
        } else {
            // 普通标签，直接创建一个元素
            XWPFRun run = this.para.createRun();
            run.addPicture(pictureData, pictureType, filename, width, height);
            switch (where) {
                case Y9BookMark4Docx.INSERT_AFTER:
                    this.insertAfterBookmark(run);
                    break;
                case Y9BookMark4Docx.INSERT_BEFORE:
                    this.insertBeforeBookmark(run);
                    break;
                case Y9BookMark4Docx.REPLACE:
                    this.replaceBookmark(run);
                    break;
                default:
            }
        }
    }

    public boolean isInTable() {
        return this.isCell;
    }

    private void replaceBookmark(XWPFRun run) {
        Node nextNode = null;
        Node styleNode = null;
        Node lastRunNode = null;
        Stack<Node> nodeStack = null;
        int bookmarkStartId = 0;
        int bookmarkEndId = -1;
        nodeStack = new Stack<Node>();
        bookmarkStartId = this.ctBookmark.getId().intValue();
        nextNode = this.ctBookmark.getDomNode();
        nodeStack.push(nextNode);

        while (bookmarkStartId != bookmarkEndId) {
            nextNode = nextNode.getNextSibling();
            nodeStack.push(nextNode);

            if (nextNode.getNodeName().contains(Y9BookMark4Docx.BOOKMARK_END_TAG)) {
                try {
                    bookmarkEndId = Integer.parseInt(
                        nextNode.getAttributes().getNamedItem(Y9BookMark4Docx.BOOKMARK_ID_ATTR_NAME).getNodeValue());
                } catch (NumberFormatException nfe) {
                    bookmarkEndId = bookmarkStartId;
                }
            }
            // else {
            // Place a reference to the node on the nodeStack
            // nodeStack.push(nextNode);
            // }
        }
        if (!nodeStack.isEmpty()) {
            lastRunNode = nodeStack.peek();
            if ((lastRunNode.getNodeName().equals(Y9BookMark4Docx.RUN_NODE_NAME))) {
                styleNode = this.getStyleNode(lastRunNode);
                if (styleNode != null) {
                    run.getCTR().getDomNode().insertBefore(styleNode.cloneNode(true),
                        run.getCTR().getDomNode().getFirstChild());
                }
            }
            this.deleteChildNodes(nodeStack);
        }
        this.para.getCTP().getDomNode().insertBefore(run.getCTR().getDomNode(), nextNode);
    }
}