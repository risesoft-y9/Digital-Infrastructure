package net.risesoft.model.subscription;

import java.io.Serializable;

import lombok.Data;

/**
 *
 * @author shidaobang
 * @date 2022/12/29
 */
@Data
public class AttendeeModel implements Serializable {

    private static final long serialVersionUID = 4122910242319397563L;

    private String id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 简历
     */
    private String resume;

    /**
     * 公司/单位
     */
    private String company;

    /**
     * 产业观点及成果
     */
    private String productIdea;

    /**
     * 寸照
     */
    private String photo;

    /**
     * 生活照
     */
    private String lifephoto;

}
