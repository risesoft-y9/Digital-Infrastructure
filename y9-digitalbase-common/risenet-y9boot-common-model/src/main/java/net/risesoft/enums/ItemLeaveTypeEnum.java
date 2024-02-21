package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 请假类型
 * 
 * @author qinman
 * @date 2022/12/20
 */
@Getter
@AllArgsConstructor
public enum ItemLeaveTypeEnum {
    /** 签到 */
    SIGNIN("0", "签到"),
    /** 出差 */
    TRAVEL("1", "出差"),
    /** 事假 */
    PERSONALLEAVE("2", "事假"),
    /** 病假 */
    SICKLEAVE("3", "病假"),
    /** 年假 */
    ANNUALLEAVE("4", "年假"),
    /** 探亲假 */
    HOMELEAVE("5", "探亲假"),
    /** 生育假 */
    MATERNITYLEAVE("6", "生育假"),
    /** 产检 */
    DATETIME("7", "产检"),
    /** 婚假 */
    MARRIAGELEAVE("8", "婚假"),
    /** 丧假 */
    FUNERALLEAVE("9", "丧假"),
    /** 丧假 */
    OUTLEAVE("10", "外勤"),
    /** 全脱产学习 */
    DAYRELEASE("11", "全脱产学习"),
    /** 半脱产学习 */
    HALFDAYRELEASE("12", "半脱产学习"),
    /** 调休 */
    COMPENSATORYLEAVE("13", "调休"),
    /** 调休 */
    OVERTIME("14", "加班");

    private final String value;
    private final String name;
}
