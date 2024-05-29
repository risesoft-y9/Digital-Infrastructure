package net.risesoft.interfaces;

import net.risesoft.consts.UrlConst;
import net.risesoft.model.AuthenPicResult;
import net.risesoft.model.ExamineResult;
import net.risesoft.model.OrganUnit;
import net.risesoft.model.Result;
import net.risesoft.util.ConfigReader;
import net.risesoft.util.IDCodeApiExecute;

import java.util.HashMap;
import java.util.Map;

public class Seven {

    public static OrganUnit m601(String loginName, String loginPswd) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("login_name", loginName);
        params.put("login_pswd", loginPswd);
        OrganUnit result = null;
        try {
            result = new IDCodeApiExecute<OrganUnit>().execute(OrganUnit.class, params, UrlConst.URL_601, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Result m602(String idCode, String gotoUrl, String sampleUrl, String regId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("company_idcode", idCode);
        params.put("gotourl", gotoUrl);
        params.put("sample_url", sampleUrl);
        params.put("reg_id", regId);
        Result result = null;
        try {
            result = new IDCodeApiExecute<Result>().execute(Result.class, params, UrlConst.URL_602, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Result m606(String gotoUrl, String sampleUrl) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("gotourl", gotoUrl);
        params.put("sample_url", sampleUrl);
        Result result = null;
        try {
            result = new IDCodeApiExecute<Result>().execute(Result.class, params, UrlConst.URL_606, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /*public static OrganUnit m607(String callbackUrl) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("callback_url", callbackUrl);
        OrganUnit result = null;
        try {
            result = new IDCodeApiExecute<OrganUnit>().execute(OrganUnit.class, params, UrlConst.URL_607, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }*/

    public static ExamineResult m608(String idCode, String categoryRegId, Integer type) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("company_idcode", idCode);
        params.put("category_reg_id", categoryRegId);
        params.put("type", type);
        ExamineResult result = null;
        try {
            result = new IDCodeApiExecute<ExamineResult>().execute(ExamineResult.class, params, UrlConst.URL_608, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static AuthenPicResult m701(Integer bgImage, Integer isMarkName) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("bgimage", bgImage);
        params.put("ismarkname", isMarkName);
        AuthenPicResult result = null;
        try {
            result = new IDCodeApiExecute<AuthenPicResult>().execute(AuthenPicResult.class, params, UrlConst.URL_701, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
