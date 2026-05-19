package net.risesoft.pojo;

import java.io.Serializable;

import lombok.Data;

/**
 * 应用分类配置
 *
 * @author mengjuhua
 *
 */
@Data
public class AppCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 应用id */
    private String appId;

    /** 分类id */
    private String categoryId;

}
