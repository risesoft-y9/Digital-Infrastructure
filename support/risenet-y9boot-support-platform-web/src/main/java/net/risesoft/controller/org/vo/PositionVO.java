package net.risesoft.controller.org.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * 岗位
 *
 * @author shidaobang
 * @date 2022/09/21
 */
@Data
public class PositionVO implements Serializable {
    private static final long serialVersionUID = -236845145715510206L;

    private String id;
    private String name;
    private Integer capacity;
    /** 当前岗位人数 */
    private Integer headcount;

}
