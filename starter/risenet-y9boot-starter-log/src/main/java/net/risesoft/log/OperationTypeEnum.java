package net.risesoft.log;

/**
 * 操作类型枚举
 *
 * @author shidaobang
 * @date 2022/09/22
 */
public enum OperationTypeEnum {
    /** 查看 */
    BROWSE("查看"),
    /** 增加 */
    ADD("增加"),
    /** 修改 */
    MODIFY("修改"),
    /** 删除 */
    DELETE("删除"),
    /** 发送 */
    SEND("发送"),
    /** 活动 */
    EVENT("活动"),
    /** 登录 */
    LOGIN("登录"),
    /** 退出 */
    LOGOUT("退出"),
    /** 检查 */
    CHECK("检查");

    private final String value;

    OperationTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
