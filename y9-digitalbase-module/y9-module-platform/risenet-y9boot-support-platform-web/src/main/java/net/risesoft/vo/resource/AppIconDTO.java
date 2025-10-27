package net.risesoft.vo.resource;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

/**
 * 图标上传对象
 */
@Getter
@Setter
public class AppIconDTO implements Serializable {

    private static final long serialVersionUID = 4262220963645420167L;

    /** 图标名称 */
    String name;
    /** 备注 */
    String remark;
    /*** 类别 */
    String category;

    /** 图标颜色 */
    String[] colors;

    /** 图标文件 */
    MultipartFile[] iconFiles;
}
