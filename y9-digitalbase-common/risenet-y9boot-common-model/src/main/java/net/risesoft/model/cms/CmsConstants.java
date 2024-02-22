package net.risesoft.model.cms;

/**
 * 内容管理的基本标识
 *
 * @author mengjuhua
 */

public class CmsConstants {
    /**
     * 路径分隔符
     */
    public static final String SPT = "/";
    /**
     * 分隔符
     */
    public static final String PAGE_SPT = "_";
    /**
     * URL
     */
    public static final String URL = "url";
    /**
     * 首页
     */
    public static final String INDEX = "index";
    /**
     * 默认模板
     */
    public static final String DEFAULT = "default";
    /**
     * 部门标识
     */
    public static final String TAG_DEPART = "dp~";
    /**
     * UTF-8编码
     */
    public static final String UTF8 = "UTF-8";
    /**
     * 提示信息
     */
    public static final String MSG = "msg";
    /**
     * HTTP POST请求
     */
    public static final String POST = "POST";
    /**
     * HTTP GET请求
     */
    public static final String GET = "GET";

    /**
     * 公共资源路径
     */
    public static final String RES_COMM = "comm";
    /**
     * 模板资源路径
     */
    public static final String RES_SKIN = "skin";
    /**
     * 模板资源路径
     */
    public static final String RES_EXP = "${skin}";
    /**
     * 部署路径
     */
    public static final String BASE = "base";
    /**
     * 站点
     */
    public static final String SITE = "site";
    /**
     * 用户
     */
    public static final String USER = "user";
    /**
     * 页码
     */
    public static final String PAGE_NO = "pn";
    /**
     * 每页显示数量
     */
    public static final String COUNT = "count";
    /**
     * 起始条数
     */
    public static final String FIRST = "first";

    /**
     * 传入参数，系统预定义翻页。
     */
    public static final String PARAM_PAGE_TYPE = "pageType";

    /**
     * 公共模板
     */
    public static final String TPLDIR_COMMON = "common";
    /**
     * 标签模板
     */
    public static final String TPLDIR_COMMON_PAGE = TPLDIR_COMMON + SPT + "style_page";
    /**
     * 标签模板
     */
    public static final String TPLDIR_COMMON_TAGS = TPLDIR_COMMON + SPT + "tags";
    /**
     * 提示模板模板
     */
    public static final String TPLDIR_COMMON_TIPS = TPLDIR_COMMON + SPT + "tips";
    /**
     * 文档中心模板
     */
    public static final String TPLDIR_DOC = "doc";
    /**
     * 单页模板
     */
    public static final String TPLDIR_DOC_ALONE = TPLDIR_DOC + SPT + "alone";
    /**
     * 文档模板
     */
    public static final String TPLDIR_DOC_DOC = TPLDIR_DOC + SPT + "doc";
    /**
     * 栏目模板
     */
    public static final String TPLDIR_DOC_CHANNEL = TPLDIR_DOC + SPT + "channel";
    /**
     * 栏目模板
     */
    public static final String TPLDIR_DOC_INDEX = TPLDIR_DOC + SPT + INDEX;
    /**
     * 扩展功能模板
     */
    public static final String TPLDIR_EXTRAFUNC = "extrafunc";
    /**
     * 评论模板
     */
    public static final String TPLDIR_EXTRAFUNC_COMMENT = TPLDIR_EXTRAFUNC + SPT + "comment";
    /**
     * 调查问卷模板
     */
    public static final String TPLDIR_EXTRAFUNC_QUESTION = TPLDIR_EXTRAFUNC + SPT + "question";
    /**
     * 评论模板
     */
    public static final String TPLDIR_EXTRAFUNC_ADVERT = TPLDIR_EXTRAFUNC + SPT + "advert";
    /**
     * 留言板模板
     */
    public static final String TPLDIR_EXTRAFUNC_GBOOK = TPLDIR_EXTRAFUNC + SPT + "gbook";
    /**
     * 新留言板模板
     */
    public static final String TPLDIR_EXTRAFUNC_BOARD = TPLDIR_EXTRAFUNC + SPT + "board";
    /**
     * 版块模板
     */
    public static final String TPLDIR_EXTRAFUNC_FORUM = TPLDIR_EXTRAFUNC + SPT + "forum";
    /**
     * RSS模板
     */
    public static final String TPLDIR_EXTRAFUNC_RSS = TPLDIR_EXTRAFUNC + SPT + "rss";
    /**
     * 搜索模板
     */
    public static final String TPLDIR_EXTRAFUNC_SEARCH = TPLDIR_EXTRAFUNC + SPT + "search";
    /**
     * 政府中心模板
     */
    public static final String TPLDIR_GOVCENTER = "govcenter";
    /**
     * 政民互动模板
     */
    public static final String TPLDIR_INTERACTIVE = TPLDIR_GOVCENTER + SPT + "interactive";
    /**
     * 会员中心模板
     */
    public static final String TPLDIR_USER = "user";
    /**
     * 招聘会模板
     */
    public static final String TPLDIR_FAIRS = "fairs";
    /**
     * 个人中心模板
     */
    public static final String TPLDIR_PERSONAL = "personal";
    /**
     * 企业中心模板
     */
    public static final String TPLDIR_COMPANY = "company";
    /**
     * 招聘模板
     */
    public static final String TPLDIR_RECRUIT = "recruit";

    /**
     * 信息提示页面
     */
    public static final String MSG_PAGE = "tpl.msg";

    /**
     * 会员登录页地址
     */
    public static final String LOGIN_URL = "login.jsp";
    /**
     * 会员登录页
     */
    public static final String LOGIN_INPUT = "member.login";

    /**
     * 下一个地址
     */
    public static final String NEXT_URL = "nextUrl";
    /**
     * 重定向地址
     */
    public static final String REDIRECT_URL = "redirectUrl";

    /**
     * 模板后缀和静态页
     */
    public static final String TPL_SUFFIX = ".html";

    /**
     * 静态页后缀
     */
    public static final String TPL_SHTML = ".shtml";

    /**
     * 访问地址后缀
     */
    public static final String URL_SUFFIX = ".jsp";

    /**
     * 上传路径
     */
    public static final String UPLOAD_PATH = "/member/upload/";
    /**
     * 资源路径
     */
    public static final String RES_PATH = "/skin";
    /**
     * 公用路径
     */
    public static final String COMM_PATH = "/skin/comm";
    /**
     * 系统路径
     */
    public static final String SYS_PATH = "/skin/sys";
    /**
     * 模板路径
     */
    public static final String TPL_BASE = "/WEB-INF/tpl";
    /**
     * 全文检索索引路径
     */
    public static final String LUCENE_PATH = "/WEB-INF/lucene";
    /**
     * 分页模板路径
     */
    public static final String TPL_PAGE_PATH = "/common/style_page/page_";
    /**
     * 默认栏目模板路径
     */
    public static final String DEFAULT_CHANNEL_PATH = "/doc/channel/默认栏目页";
    /**
     * 页面禁止访问
     */
    public static final String ERROR_403 = "error/403";

    public static final String[] DEF_IMG_ACCEPT = {"jpg", "gif", "jpeg", "png", "bmp",};

    public static final String DOC_URL = "doc";

    private CmsConstants() {
        throw new IllegalStateException(" CmsConstants Utility class");
    }

}
