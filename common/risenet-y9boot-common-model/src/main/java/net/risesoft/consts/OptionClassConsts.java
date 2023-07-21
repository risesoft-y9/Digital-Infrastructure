package net.risesoft.consts;

/**
 * 字典类型常量
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
public class OptionClassConsts {
    /** 组织机构类型 */
    public static final String ORGANIZATION_TYPE = "organizationType";

    /** 证件类型 */
    public static final String PRINCIPAL_ID_TYPE = "principalIDType";

    /** 职务名称 */
    public static final String DUTY = "duty";

    /** 职务类型 */
    public static final String DUTY_TYPE = "dutyType";

    /** 职备级别 */
    public static final String DUTY_LEVEL = "dutyLevel";

    /** 人员编制 */
    public static final String OFFICIAL_TYPE = "officialType";

    private OptionClassConsts() {
        throw new IllegalStateException("OptionClassConsts class");
    }

}
