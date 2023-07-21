package net.risesoft.model.graphdata;

import java.io.Serializable;

import lombok.Data;

/**
 * 办件
 * 
 * @author panzhaoxiong
 *
 */
@Data
public class Office implements Serializable {

    private static final long serialVersionUID = 8943998425756466722L;

    private String officeId;// 数据中心办件表主键ID

    private String title;// 办件标题

    private String departName;// 部门名称

    private String createName;// 创建人

    private String createTime;// 创建时间

    private String finishName;// 办结人

    private String finishTime;// 办结时间

    private String content;// 内容

}
