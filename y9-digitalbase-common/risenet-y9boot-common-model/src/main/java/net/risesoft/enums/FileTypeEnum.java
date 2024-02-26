package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 办件列表类型
 * 
 * @author qinman
 * @date 2022/12/20
 */
@Getter
@AllArgsConstructor
public enum FileTypeEnum {
    /** doc */
    DOC(".doc"),
    /** docx */
    DOCX(".docx"),
    /** pdf */
    PDF(".pdf"),
    /** tif */
    TIF(".tif"),
    /** ofd */
    OFD(".ofd");

    private final String name;
}
