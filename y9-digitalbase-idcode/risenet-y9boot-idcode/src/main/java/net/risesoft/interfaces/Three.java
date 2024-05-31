package net.risesoft.interfaces;

import net.risesoft.consts.UrlConst;
import net.risesoft.model.OrganUnit;
import net.risesoft.model.OrganUnitResult;
import net.risesoft.model.OrganUnitStatusInfo;
import net.risesoft.model.RegistResult;
import net.risesoft.model.Result;
import net.risesoft.model.SmsVerifyCode;
import net.risesoft.model.UnitRegistResult;
import net.risesoft.model.UpdateGotoUrlResult;
import net.risesoft.util.ConfigReader;
import net.risesoft.util.IDCodeApiExecute;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Three {
    /**
     * 单位注册信息提交接口1
     *
     * @param loginName     用户名(必填)
     * @param loginPassword 密码(必填)
     * @param email         单位邮箱
     * @param orgUnitName   组织单位名称(必填)
     * @param orgUnitEnName 单位英文名称
     * @param code          单位性质编码(必填)，可通过接口105获取
     * @param provinceId    所属省(必填) ，可通过接口101/102获取
     * @param cityId        所属市(必填) ，可通过接口101/102获取
     * @param areaId        所属区(必填) ，可通过接口101/102获取
     * @param linkman       联系人
     * @param linkmanEn     联系人英文名称
     * @param linkPhone     联系人手机(必填)
     * @param smsVerifyCode 用户输入验证码(必填)，有短信验证功能平台使用,可通过302/303接口获取
     * @return
     */
    public static RegistResult m301(String loginName, String loginPassword, String email, String orgUnitName, String orgUnitEnName, Integer code, Integer provinceId, Integer cityId, Integer areaId, String linkman, String linkmanEn, String linkPhone, String smsVerifyCode) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("login_name", loginName);
        params.put("login_password", loginPassword);
        params.put("email", email);
        params.put("organunit_name", orgUnitName);
        params.put("organunit_name_en", orgUnitEnName);
        params.put("code", code);
        params.put("province_id", provinceId);
        params.put("city_id", cityId);
        params.put("area_id", areaId);
        params.put("linkman", linkman);
        params.put("linkman_en", linkmanEn);
        params.put("linkphone", linkPhone);
        params.put("sms_verify_code", smsVerifyCode);
        RegistResult result = null;
        try {
            result = new IDCodeApiExecute<RegistResult>().execute(RegistResult.class, params, UrlConst.URL_301, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取激活验证码接口(SP平台发短信）
     *
     * @param phoneCode 手机号码(保证格式正确) (必填)
     * @return
     */
    public static SmsVerifyCode m302(String phoneCode) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("phone_code", phoneCode);
        SmsVerifyCode result = null;
        try {
            result = new IDCodeApiExecute<SmsVerifyCode>().execute(SmsVerifyCode.class, params, UrlConst.URL_302, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 发送激活验证码接口(IDcode平台发短信）
     *
     * @param phoneCode 手机号码(保证格式正确) (必填)
     * @return
     */
    public static Result m303(String phoneCode) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("phone_code", phoneCode);
        Result result = null;
        try {
            result = new IDCodeApiExecute<Result>().execute(Result.class, params, UrlConst.URL_303, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 单位认证接口
     *
     * @param idCode  单位主码(必填)
     * @param payType 证件类型(必填) 2：统一社会信用代码 0：其他
     * @param orgCode 证件代码(必填)
     * @param file    营业执照图片(必填，上传图片大小不能超过5M，支持的图片上传格式包括 bmp png jpg jpeg gif)
     * @return
     */
    public static Result m304(String idCode, Integer payType, String orgCode, File file) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("company_idcode", idCode);
        params.put("code_pay_type", payType);
        params.put("organization_code", orgCode);
        params.put("file1", file);
        Result result = null;
        try {
            result = new IDCodeApiExecute<Result>().execute(Result.class, params, UrlConst.URL_304, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 单位资料完善相关接口
     *
     * @param idCode            单位主码(必填)
     * @param tradeId           所属行业(必填,可通过接口103/104 获取)
     * @param address           企业地址(必填)
     * @param name              企业名称
     * @param nameEn            企业英文名称
     * @param addressEn         企业英文地址
     * @param email             企业邮箱
     * @param linkman           联系人
     * @param linkmanEn         联系人英文名称
     * @param fax               单位电话
     * @param workAddress       组织/单位办公地址
     * @param workAddressEn     组织/单位办公英文地址
     * @param sizeType          公司规模 (1、50人以下 2、51-100人 3、101-500人 4、500人以上； 必填，默认为1)
     * @param registeredCapital 注册资金(以万为单位)
     * @param unitTypeId        单位性质编码（可通过接口105获取）
     * @param gotoUrl           单位主码解析地址
     * @param linkPhone         联系人手机
     * @param unitLogo          单位logo(非必填，但建议上传。上传图片大小不能超过5M，支持的图片上传格式包括 bmp png jpg jpeg gif)
     * @return
     */
    public static Result m305(String idCode, Integer tradeId, String address, String name, String nameEn, String addressEn, String email, String linkman, String linkmanEn, String fax, String workAddress, String workAddressEn, Integer sizeType, Integer registeredCapital, Integer unitTypeId, String gotoUrl, String linkPhone, File unitLogo) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("company_idcode", idCode);
        params.put("trade_id", tradeId);
        params.put("organunit_address", address);
        params.put("organunit_name", name);
        params.put("organunit_name_en", nameEn);
        params.put("organunit_address_en", addressEn);
        params.put("email", email);
        params.put("linkman", linkman);
        params.put("linkman_en", linkmanEn);
        params.put("fax", fax);
        params.put("unit_workaddress", workAddress);
        params.put("unit_workaddress_en", workAddressEn);
        params.put("unit_size_type", sizeType);
        params.put("registered_capital", registeredCapital);
        params.put("unittype_id", unitTypeId);
        params.put("gotourl", gotoUrl);
        params.put("linkphone", linkPhone);
        params.put("unit_logo", unitLogo);

        Result result = null;
        try {
            result = new IDCodeApiExecute<Result>().execute(Result.class, params, UrlConst.URL_305, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取单位基本信息接口
     *
     * @param idCode 单位主码(必填)
     * @return
     */
    public static OrganUnit m306(String idCode) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("company_idcode", idCode);
        OrganUnit result = null;
        try {
            result = new IDCodeApiExecute<OrganUnit>().execute(OrganUnit.class, params, UrlConst.URL_306, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据单位名称获取单位基本信息接口
     *
     * @param name       企业名称(必填)
     * @param searchType 查询方式（0、模糊 1、精确）
     * @return
     */
    public static OrganUnitResult m307(String name, String searchType) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("company_name", name);
        params.put("search_type", searchType);
        OrganUnitResult result = null;
        try {
            result = new IDCodeApiExecute<OrganUnitResult>().execute(OrganUnitResult.class, params, UrlConst.URL_307, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取单位状态接口
     *
     * @param idCode 单位主码(必填)
     * @return
     */
    public static OrganUnitStatusInfo m308(String idCode) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("company_idcode", idCode);
        OrganUnitStatusInfo result = null;
        try {
            result = new IDCodeApiExecute<OrganUnitStatusInfo>().execute(OrganUnitStatusInfo.class, params, UrlConst.URL_308, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 单位注册信息提交接口2
     *
     * @param loginName   用户名(必填，填写营业执照注册号)
     * @param orgUnitName 组织单位名称(必填)
     * @param email       单位邮箱
     * @param code        单位性质编码(必填) ，可通过接口105获取
     * @param provinceId  所属省(必填) ，可通过接口101/102获取
     * @param cityId      所属市(必填) ，可通过接口101/102获取
     * @param areaId      所属区(必填) ，可通过接口101/102获取
     * @param linkman     联系人
     * @param linkPhone   联系人手机
     * @param unitLogo    单位logo（将单位logo图片转换为base64字符串；目前系统并没有用到此图片，此参数建议不传或传空字符串）
     * @param qrCodeColor 码图颜色 0：彩色 1：黑色
     * @param qrCodeLogo  是否启用带logo的码图 0：否 1：是
     * @param gotoUrl     企业码解析地址
     * @param qrCodeSize  码图尺寸（默认 1） 1：400px 2：600px 3：800px
     * @return
     */
    public static UnitRegistResult m309(String loginName, String orgUnitName, String email, String code, String provinceId, String cityId, String areaId, String linkman, String linkPhone, File unitLogo, String qrCodeColor, File qrCodeLogo, String gotoUrl, String qrCodeSize) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("login_name", loginName);
        params.put("organunit_name", orgUnitName);
        params.put("email", email);
        params.put("code", code);
        params.put("province_id", provinceId);
        params.put("city_id", cityId);
        params.put("area_id", areaId);
        params.put("linkman", linkman);
        params.put("linkphone", linkPhone);
        params.put("unit_icon", unitLogo);
        params.put("qrcode_color", qrCodeColor);
        params.put("qrcode_logo", qrCodeLogo);
        params.put("gotourl", gotoUrl);
        params.put("qrcode_size", qrCodeSize);
        UnitRegistResult result = null;
        try {
            result = new IDCodeApiExecute<UnitRegistResult>().execute(UnitRegistResult.class, params, UrlConst.URL_309, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 修改单位logo或企业码解析地址
     *
     * @param loginName   用户名(必填)
     * @param pwd         密码(明文，必填)
     * @param unitLogo    单位logo（将单位logo图片转换为base64字符串）
     * @param gotoUrl     企业码解析地址
     * @param qrCodeColor 码图颜色 0：彩色 1：黑色
     * @param qrCodeLogo  是否启用带logo的码图 0：否 1：是
     * @param qrCodeSize  码图尺寸（默认 1） 1：400px 2：600px 3：800px
     * @return
     */
    public static UpdateGotoUrlResult m310(String loginName, String pwd, String unitLogo, String gotoUrl, Integer qrCodeColor, Integer qrCodeLogo, Integer qrCodeSize) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("login_name", loginName);
        params.put("login_pwd", pwd);
        params.put("unit_icon", unitLogo);
        params.put("gotourl", gotoUrl);
        params.put("qrcode_color", qrCodeColor);
        params.put("qrcode_logo", qrCodeLogo);
        params.put("qrcode_size", qrCodeSize);
        UpdateGotoUrlResult result = null;
        try {
            result = new IDCodeApiExecute<UpdateGotoUrlResult>().execute(UpdateGotoUrlResult.class, params, UrlConst.URL_310, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
