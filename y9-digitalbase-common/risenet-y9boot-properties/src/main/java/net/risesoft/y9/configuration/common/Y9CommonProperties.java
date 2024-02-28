package net.risesoft.y9.configuration.common;

import lombok.Getter;
import lombok.Setter;

/**
 * 公共属性
 *
 * @author liansen
 * @date 2022/09/28
 */
@Getter
@Setter
public class Y9CommonProperties {

    /**
     * 主键guid的位数,可以是32,36,38
     */
    @Deprecated
    private int guidLength = 32;
    /**
     * 是否启用缓存
     */
    private boolean cacheEnabled = false;
    /**
     * 是否启用 kafka
     */
    private boolean kafkaEnabled = false;// 是否启用Kafka

    /**
     * 系统公共角色顶节点
     */
    private String topInternalRoleId4SystemList = "11111111-1111-1111-1111-111111111121";
    /**
     * 默认应用程序图标
     */
    private String defaultAppIcon = "/static/images/default.png";

    /**
     * 默认密码
     */
    private String defaultPassword = "Risesoft@2022";

    /**
     * 云
     */
    private String baseUrl = "https://vue.youshengyun.com";

    /**
     * 数字底座
     */
    private String orgBaseUrl = "https://vue.youshengyun.com/platform";
    /**
     * 企业版数字底座
     */
    private String y9DigitalBaseUrl = "https://vue.youshengyun.com/y9DigitalBase";
    /**
     * 内容管理
     */
    private String cmsBaseUrl = "https://vue.youshengyun.com/cms";
    /**
     * 统一待办
     */
    private String todoBaseUrl = "https://vue.youshengyun.com/todo";
    /**
     * 接口管理
     */
    private String soaBaseUrl = "https://vue.youshengyun.com/soa";
    /**
     * 日志管理
     */
    private String logBaseUrl = "https://vue.youshengyun.com/log";
    /**
     * 开发平台
     */
    private String devBaseUrl = "https://dev.youshengyun.com/admin-dev";
    /**
     * 用户在线
     */
    private String userOnlineBaseUrl = "https://vue.youshengyun.com/userOnline";
    /**
     * 地灾协会门户
     */
    private String dzxhPlatformBaseUrl = "https://vue.youshengyun.com/dzxhPlatform";
    /**
     * 日程安排
     */
    private String calendarBaseUrl = "https://vue.youshengyun.com/calendar";
    /**
     * y9门户
     */
    private String homeBaseUrl = "https://vue.youshengyun.com/y9home";
    /**
     * 消息中心
     */
    private String msgRemindBaseUrl = "https://vue.youshengyun.com/msgRemind";
    /**
     * 公务邮件
     */
    private String emailBaseUrl = "https://vue.youshengyun.com/email";
    /**
     * 电子邮件
     */
    private String webmailBaseUrl = "https://vue.youshengyun.com/webmail";
    /**
     * 网络硬盘
     */
    private String storageBaseUrl = "https://vue.youshengyun.com/storage";
    /**
     * 通讯录
     */
    private String addressBookBaseUrl = "https://vue.youshengyun.com/addressBook";
    /**
     * 数据中心
     */
    private String datacenterBaseUrl = "https://vue.youshengyun.com/datacenter";
    /**
     * 部门号
     */
    private String subscriptionBaseUrl = "https://vue.youshengyun.com/subscription";
    /**
     * 有生云外网
     */
    private String risecloudBaseUrl = "https://vue.youshengyun.com/risecloud";
    /**
     * 流程管理
     */
    private String processAdminBaseUrl = "https://vue.youshengyun.com/processAdmin";
    /**
     * 事项管理
     */
    private String itemAdminBaseUrl = "https://vue.youshengyun.com/itemAdmin";
    /**
     * 即时数据
     */
    private String dataServiceBaseUrl = "https://vue.youshengyun.com/dataservice";
    /**
     * 短信平台
     */
    private String smsBaseUrl = "https://vue.youshengyun.com/sms";
    /**
     * 弹窗管理
     */
    private String popupBaseUrl = "https://vue.youshengyun.com/popup";
    /**
     * 代码生成器
     */
    private String generateCodeBaseUrl = "https://vue.youshengyun.com/generateCode";
    /**
     * y9门户
     */
    private String y9homeBaseUrl = "https://vue.youshengyun.com/y9home";

    /**
     * 办件
     */
    private String flowableBaseUrl = "https://vue.youshengyun.com/flowableUI";

    /**
     * 代办路径
     */
    private String todoTaskUrlPrefix = "https://dev.youshengyun.com/flowableUI/todoIndex";
    /**
     * 文件预览
     */
    private String jodconverterBaseUrl = "https://vue.youshengyun.com/jodconverter";

    /**
     * bigdata
     */
    private String bigdataBaseUrl = "https://vue.youshengyun.com/bigdata";

    /**
     * graphData
     */
    private String graphDataBaseUrl = "https://vue.youshengyun.com/graphData";

    /**
     * gfltPlatform
     */
    private String gfltPlatformBaseUrl = "https://www.youshengyun.com/gfltPlatform";
    /**
     * 服务器管理
     */
    private String serverManageBaseUrl = "https://vue.youshengyun.com/serverManage";
    /**
     * 考勤管理
     */
    private String attendanceBaseUrl = "https://vue.youshengyun.com/attendance";

    /** 手机消息提醒 */
    private String messageBaseUrl = "https://vue.youshengyun.com/message";

}
