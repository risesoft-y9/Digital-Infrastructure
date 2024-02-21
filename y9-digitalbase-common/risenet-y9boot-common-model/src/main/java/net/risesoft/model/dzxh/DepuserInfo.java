package net.risesoft.model.dzxh;

import java.io.Serializable;

import lombok.Data;

@Data
public class DepuserInfo implements Serializable {

    private static final long serialVersionUID = 4820656927004582205L;

    private String id;

    private String name;// 单位名称

    private String province; // 省份

    private String socialCreditNum; // 统一社会信用代码

    private boolean ifvip; // true 是会员 false 不是会员

    private String level; // 会员级别

    private String identifier; // 会员编号

}
