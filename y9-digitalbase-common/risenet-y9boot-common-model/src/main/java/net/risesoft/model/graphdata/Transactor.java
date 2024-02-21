package net.risesoft.model.graphdata;

import java.io.Serializable;

import lombok.Data;

/**
 * 办理人情况
 * 
 * @author panzhaoxiong
 *
 */
@Data
public class Transactor implements Serializable {

    private static final long serialVersionUID = -995110591648677541L;

    private String officeId;// 数据中心办件表主键ID

    private String personName;// 办理人

    private String content;// 办理内容

}
