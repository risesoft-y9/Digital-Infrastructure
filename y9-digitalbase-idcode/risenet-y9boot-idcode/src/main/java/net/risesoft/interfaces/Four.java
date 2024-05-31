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
        Map<String, Object> params = new HashMap<>();
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

    /**
     * 批量注册/备案品类接口
     *
     * @param jsonStr 提交的json数据
     * @return
     */
    public static BatchRegistResult m402(String jsonStr) {
        Map<String, Object> params = new HashMap<>();
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

    /**
     * 查询注册品类
     *
     * @param idCode           单位主码(必填)
     * @param idCodeOfCategory 品类编码(非必填)
     * @param modelNumberCode  型号编码(非必填)
     * @param categoryRegId    品类注册ID(主键ID，非必填)
     * @return
     */
    public static BaseIdCodeInfo m403(String idCode, String idCodeOfCategory, String modelNumberCode, String categoryRegId) {
        Map<String, Object> params = new HashMap<>();
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

    /**
     * 查询所有注册品类
     *
     * @param idCode     单位主码(必填)
     * @param searchType 查询类型(默认0) 0：全部 1：注册 2：备案
     * @return
     */
    public static BaseIdCodeInfo m404(String idCode, Integer searchType) {
        Map<String, Object> params = new HashMap<>();
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
        Map<String, Object> params = new HashMap<>();
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

    /**
     * 注册/备案产品品类IDcode码接口
     *
     * @param idCode             单位主码（必填）
     * @param codeUseId          用途(固定值：10；必填)
     * @param industryCategoryId 品类ID（必填）
     * @param categoryCode       品类编码(必填)
     * @param modelNumber        型号名称(必填)
     * @param modelNumberCode    型号编码(必填)
     * @param codePayType        申请码类型（必填） 2：注册 5：备案
     * @param gotoUrl            解析地址（必填）
     * @param sampleUrl          示例地址（必填）
     * @return
     */
    public static IdcodeRegResult m406(String idCode, String codeUseId, Integer industryCategoryId, String categoryCode, String modelNumber, String modelNumberCode, Integer codePayType, String gotoUrl, String sampleUrl) {
        Map<String, Object> params = new HashMap<>();
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

    /**
     * 注册/备案非产品品类IDcode码接口
     *
     * @param idCode             单位主码（必填）
     * @param codeUseId          用途(非10；必填)
     * @param industryCategoryId 品类ID（必填）
     * @param categoryCode       品类码号(必填)
     * @param modelNumber        名称(必填)
     * @param modelNumberEn      英文名称
     * @param introduction       简介
     * @param codePayType        申请码类型（必填） 2：注册 5：备案
     * @param gotoUrl            解析地址（必填）
     * @param sampleUrl          示例地址（必填）
     * @return
     */
    public static IdcodeRegResult m407(String idCode, String codeUseId, Integer industryCategoryId, String categoryCode, String modelNumber, String modelNumberEn, String introduction, Integer codePayType, String gotoUrl, String sampleUrl) {
        Map<String, Object> params = new HashMap<>();
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
        Map<String, Object> params = new HashMap<>();
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

    /**
     * 查询注册品类（细化申请码类型）
     *
     * @param idCode           单位主码(必填)
     * @param idCodeOfCategory 品类编码(非必填)
     * @param modelNumberCode  型号编码(非必填)
     * @param categoryRegId    品类注册ID(主键ID，非必填)
     * @return
     */
    public static BaseIdCodeInfo m409(String idCode, String idCodeOfCategory, String modelNumberCode, String categoryRegId) {
        Map<String, Object> params = new HashMap<>();
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

    /**
     * 查询所有注册品类（细化申请码类型）
     *
     * @param idCode     单位主码(必填)
     * @param searchType 查询类型(默认0)0：全部1：注册2：备案
     * @return
     */
    public static BaseIdCodeInfo m410(String idCode, Integer searchType) {
        Map<String, Object> params = new HashMap<>();
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
