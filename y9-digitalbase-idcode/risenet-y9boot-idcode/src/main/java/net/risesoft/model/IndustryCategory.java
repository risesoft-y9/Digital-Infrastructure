package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class IndustryCategory {

    /**
     * 品类id
     */
    @JsonProperty("industrycategory_id")
    private Integer id;

    /**
     * 品类名称
     */
    @JsonProperty("industrycategory_name")
    private String name;
    /**
     * 品类代码
     */
    @JsonProperty("industrycategory_code")
    private String code;
    /**
     * 品类父级ID
     */
    @JsonProperty("industrycategory_id_parent")
    private Integer parentId;
    /**
     * 品类等级
     */
    @JsonProperty("industrycategory_level")
    private Integer level;
    /**
     * 品类是否删除
     */
    @JsonProperty("industrycategory_isdel")
    private Integer isDel;
    /**
     * 所属用途ID
     */
    @JsonProperty("codeuse_id")
    private Integer useId;
    /**
     * 品类人事物类型
     */
    @JsonProperty("type_id")
    private Integer typeId;
    /**
     * 填写项验证规则（正则表达式格式，目前仅用于身份证品类）
     */
    @JsonProperty("check_format")
    private String checkStr;
}
