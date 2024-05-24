package net.risesoft.consts;

/***
 * 接口地址的配置信息
 *
 */
public class UrlConst {

    /**
     * 101 一次性返回所有级别行政信息
     */
    public static final String URL_101 = "/sp/idcode/addresses";

    /**
     * 102 按照父级ID返回子级信息
     */
    public static final String URL_102 = "/sp/idcode/addresses/parent/id";

    /**
     * 103 一次性返回所有级别行业信息
     */
    public static final String URL_103 = "/sp/idcode/trades";

    /**
     * 104 行业按照父级ID返回子级信息
     */
    public static final String URL_104 = "/sp/idcode/trades/parent/id";

    /**
     * 105 获取单位性质分类接口
     */
    public static final String URL_105 = "/sp/idcode/unittypes";

    /**
     * 201 获取人、事、物所有用途接口（接口编号201）
     */
    public static final String URL_201 = "/sp/idcode/codeuse";

    /**
     * 202 获取所有品类接口
     */
    public static final String URL_202 = "/sp/idcode/industrycategory";

    /**
     * 203 获取某一级品类接口
     */
    public static final String URL_203 = "/sp/idcode/industrycategory/parent/id";

    /**
     * 204 获取产品品类接口
     */
    public static final String URL_204 = "/sp/idcode/industrycategory/product";

    /**
     * 301 单位注册信息提交接口
     */
    public static final String URL_301 = "/sp/idcode/companyinfo/reg";

    /**
     * 302 获取激活验证码接口(SP平台发短信）
     */
    public static final String URL_302 = "/sp/idcode/verifycode";

    /**
     * 303 发送激活验证码接口(IDcode平台发短信）
     */
    public static final String URL_303 = "/sp/idcode/verifycode/send";

    /**
     * 304 单位认证接口
     */
    public static final String URL_304 = "/sp/idcode/companyinfo/verify";

    /**
     * 305 单位资料完善相关接口
     */
    public static final String URL_305 = "/sp/idcode/companyinfo/modify";

    /**
     * 306 获取单位基本信息接口
     */
    public static final String URL_306 = "/sp/idcode/companyinfo/base";

    /**
     * 307 获取单位基本信息接口(查询)
     */
    public static final String URL_307 = "/sp/idcode/companyinfo/search";

    /**
     * 308 获取单位状态接口
     */
    public static final String URL_308 = "/sp/idcode/organunit/status";

    /**
     * 309 单位注册信息提交接口2
     */
    public static final String URL_309 = "/sp/idcode/unit/regist";

    /**
     * 310修改单位logo和企业码解析地址
     */
    public static final String URL_310 = "/sp/idcode/companyidcode/gotourl";

    /**
     * 401注册品类接口(页面接口)
     */
    public static final String URL_401 = "/sp/idcode/category/apply";

    /**
     * 402接口 批量注册品类接口
     */
    public static final String URL_402 = "/sp/idcode/category/batchregist";

    /**
     * 403接口 查询注册品类,通过品类码获取IDcode申请基本信息（精确匹配）
     */
    public static final String URL_403 = "/sp/idcode/idcodeinfo/base";

    /**
     * 404接口 查询所有注册品类
     */
    public static final String URL_404 = "/sp/idcode/idcodeinfo/search";

    /**
     * 406接口 注册/备案产品品类IDcode码接口
     */
    public static final String URL_406 = "/sp/idcode/idcodeinfo/reg/product";

    /**
     * 407接口 注册/备案非产品品类IDcode码接口
     */
    public static final String URL_407 = "/sp/idcode/idcodeinfo/reg/other";

    /**
     * 408备案品类接口(页面接口)
     */
    public static final String URL_408 = "/sp/idcode/category/record";

    /**
     * 409查询注册品类接口通过品类码获取 IDcode 申请基本信息（精确匹配）细化申请码类型
     */
    public static final String URL_409 = "/sp/idcode/idcodeinfo/base2";

    /**
     * 410查询所有注册品类 细化申请码类型
     */
    public static final String URL_410 = "/sp/idcode/idcodeinfo/search2";

    /**
     * 5011上传码接口（方式一：上传TXT文件）
     */
    public static final String URL_5011 = "/sp/idcode/upload/codefile";

    /**
     * 5012上传码接口（方式二：参数列表传递）
     */
    public static final String URL_5012 = "/sp/idcode/upload/codelist";

    /**
     * 5013上传码接口（方式三：前缀 + 起始号、终止号）
     */
    public static final String URL_5013 = "/sp/idcode/upload/codeprefix";

    /**
     * 5014上传码接口（方式四：前缀 + 起始号、终止号、数值长度，数值位数不够规定长度时高位补零）
     */
    public static final String URL_5014 = "/sp/idcode/upload/fixedlength";

    /**
     * 502 按品类查询上传的二维码接口
     */
    public static final String URL_502 = "/sp/idcode/upload/coderecord";

    /**
     * 503 获取IDcode上传码记录列表
     */
    public static final String URL_503 = "/sp/idcode/upload/codeinfo";

    /**
     * 504 获取一个IDcode码内容文件的下载地址
     */
    public static final String URL_504 = "/sp/idcode/uploadcode";

    /**
     * 505 根据上传批次ID获取IDcode码
     */
    public static final String URL_505 = "：/sp/idcode/uploadcode/json";

    /**
     * 506 根据原码查询简码
     */
    public static final String URL_506 = "：/sp/idcode/shortcode/search";

    /**
     * 601 登录验证接口
     */
    public static final String URL_601 = "/sp/idcode/loginverify";

    /**
     * 602 提交修改解析地址接口 post
     */
    public static final String URL_602 = "/sp/idcode/category/gotourl";

    /**
     * 603 对应的603接口的处理
     */
    public static final String URL_603 = "/sp/idcode/codepic";

    /**
     * 604 对应的604接口的处理
     */
    public static final String URL_604 = "/sp/idcode/codepic/withmargin";

    /**
     * 605 生成质量认证二维码图
     */
    public static final String URL_605 = "/sp/idcode/codepic/authen";

    /**
     * 606申请白名单whitelist
     */
    public static final String URL_606 = "/sp/idcode/category/gotourl/whitelist";

    /**
     * 607 单位登录页面接口(页面接口)
     */
    public static final String URL_607 = "/sp/idcode/idcodelogin";

    /**
     * 608 查询审核状态接口
     */
    public static final String URL_608 = "/sp/idcode/examinerecord";

    /**
     * 701 获取认证图片接口,没有对应的编号
     */
    public static final String URL_701 = "/sp/idcode/authenpic";
}
