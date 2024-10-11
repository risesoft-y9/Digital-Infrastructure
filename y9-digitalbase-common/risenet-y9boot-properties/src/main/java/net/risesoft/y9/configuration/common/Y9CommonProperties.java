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
     * 是否启用缓存
     */
    private boolean cacheEnabled = false;

    /**
     * 是否启用 kafka
     */
    private boolean kafkaEnabled = false;// 是否启用Kafka

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
     * 日志管理
     */
    private String logBaseUrl = "https://vue.youshengyun.com/log";

    /**
     * 用户在线
     */
    private String userOnlineBaseUrl = "https://vue.youshengyun.com/userOnline";

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
     * y9门户
     */
    private String y9homeBaseUrl = "https://vue.youshengyun.com/y9home";

    /**
     * 办件
     */
    private String flowableBaseUrl = "https://vue.youshengyun.com/flowableUI";

    /**
     * 待办
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
     * 服务器管理
     */
    private String serverManageBaseUrl = "https://vue.youshengyun.com/serverManage";

    /**
     * 考勤管理
     */
    private String attendanceBaseUrl = "https://vue.youshengyun.com/attendance";

    /**
     * 文件服务
     */
    private String fileManagerBaseUrl = "https://vue.youshengyun.com/fileManager";

}
