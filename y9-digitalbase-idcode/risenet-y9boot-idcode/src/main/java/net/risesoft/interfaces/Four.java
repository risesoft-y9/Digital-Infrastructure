package net.risesoft.interfaces;

import net.risesoft.consts.UrlConst;
import net.risesoft.model.BaseIdCodeInfo;
import net.risesoft.model.BatchRegistResult;
import net.risesoft.model.IdcodeRegResult;
import net.risesoft.util.ConfigReader;
import net.risesoft.util.IDCodeApiExecute;

import java.util.HashMap;
import java.util.Map;

/**
 * 品类注册/备案相关接口
 */
public class Four {

    /*public static Result m401(String idCode, String gotoUrl, String sampleUrl, String callbackUrl) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("company_idcode", idCode);
        params.put("gotourl", gotoUrl);
        params.put("sample_url", sampleUrl);
        params.put("callback_url", callbackUrl);
        Result result = null;
        try {
            result = new IDCodeApiExecute<Result>().execute(Result.class, params, UrlConst.URL_401, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }*/

    public static BatchRegistResult m402(String jsonStr) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("json_str", jsonStr);
        params.put("time", System.currentTimeMillis());
        BatchRegistResult result = null;
        try {
            result = new IDCodeApiExecute<BatchRegistResult>().execute(BatchRegistResult.class, params, UrlConst.URL_402, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static BaseIdCodeInfo m403(String idCode, String idCodeOfCategory, String modelNumberCode, String categoryRegId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("company_idcode", idCode);
        params.put("idcode_of_category", idCodeOfCategory);
        params.put("model_number_code", modelNumberCode);
        params.put("category_reg_id", categoryRegId);

        BaseIdCodeInfo result = null;
        try {
            result = new IDCodeApiExecute<BaseIdCodeInfo>().execute(BaseIdCodeInfo.class, params, UrlConst.URL_403, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static BaseIdCodeInfo m404(String idCode, Integer searchType) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("company_idcode", idCode);
        params.put("search_type", searchType);

        BaseIdCodeInfo result = null;
        try {
            result = new IDCodeApiExecute<BaseIdCodeInfo>().execute(BaseIdCodeInfo.class, params, UrlConst.URL_404, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /*public static Result m405(String idCode, String industryCategoryId, String codePayType, String gotoUrl, String sampleUrl, String callbackUrl) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("company_idcode", idCode);
        params.put("industrycategory_id", industryCategoryId);
        params.put("code_pay_type", codePayType);
        params.put("gotourl", gotoUrl);
        params.put("sample_url", sampleUrl);
        params.put("callback_url", callbackUrl);

        Result result = null;
        try {
            result = new IDCodeApiExecute<Result>().execute(Result.class, params, UrlConst.URL_405, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }*/

    public static IdcodeRegResult m406(String idCode, String codeUseId, Integer industryCategoryId, String categoryCode, String modelNumber, String modelNumberCode, Integer codePayType, String gotoUrl, String sampleUrl) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("company_idcode", idCode);
        params.put("codeuse_id", codeUseId);
        params.put("industrycategory_id", industryCategoryId);
        params.put("category_code", categoryCode);
        params.put("model_number", modelNumber);
        params.put("model_number_code", modelNumberCode);
        params.put("code_pay_type", codePayType);
        params.put("gotourl", gotoUrl);
        params.put("sample_url", sampleUrl);

        IdcodeRegResult result = null;
        try {
            result = new IDCodeApiExecute<IdcodeRegResult>().execute(IdcodeRegResult.class, params, UrlConst.URL_406, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static IdcodeRegResult m407(String idCode, String codeUseId, Integer industryCategoryId, String categoryCode, String modelNumber, String modelNumberEn, String introduction, Integer codePayType, String gotoUrl, String sampleUrl) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("company_idcode", idCode);
        params.put("codeuse_id", codeUseId);
        params.put("industrycategory_id", industryCategoryId);
        params.put("category_code", categoryCode);
        params.put("model_number", modelNumber);
        params.put("model_number_en", modelNumberEn);
        params.put("introduction", introduction);
        params.put("code_pay_type", codePayType);
        params.put("gotourl", gotoUrl);
        params.put("sample_url", sampleUrl);

        IdcodeRegResult result = null;
        try {
            result = new IDCodeApiExecute<IdcodeRegResult>().execute(IdcodeRegResult.class, params, UrlConst.URL_407, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /*public static Result m408(String idCode,String gotoUrl, String sampleUrl, String callbackUrl) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("company_idcode", idCode);
        params.put("gotourl", gotoUrl);
        params.put("sample_url", sampleUrl);
        params.put("callback_url", callbackUrl);

        Result result = null;
        try {
            result = new IDCodeApiExecute<Result>().execute(Result.class, params, UrlConst.URL_408, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }*/

    public static BaseIdCodeInfo m409(String idCode, String idCodeOfCategory, String modelNumberCode, String categoryRegId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("company_idcode", idCode);
        params.put("idcode_of_category", idCodeOfCategory);
        params.put("model_number_code", modelNumberCode);
        params.put("category_reg_id", categoryRegId);

        BaseIdCodeInfo result = null;
        try {
            result = new IDCodeApiExecute<BaseIdCodeInfo>().execute(BaseIdCodeInfo.class, params, UrlConst.URL_409, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static BaseIdCodeInfo m410(String idCode, Integer searchType) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("company_idcode", idCode);
        params.put("search_type", searchType);

        BaseIdCodeInfo result = null;
        try {
            result = new IDCodeApiExecute<BaseIdCodeInfo>().execute(BaseIdCodeInfo.class, params, UrlConst.URL_404, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
