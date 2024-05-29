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
