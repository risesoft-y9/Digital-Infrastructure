package net.risesoft.interfaces;

import net.risesoft.consts.UrlConst;
import net.risesoft.model.BaseIdCodeInfo;
import net.risesoft.model.CodeRecordResult;
import net.risesoft.model.Result;
import net.risesoft.model.ResultObject;
import net.risesoft.util.ConfigReader;
import net.risesoft.util.IDCodeApiExecute;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 一物一码二维码接口
 */
public class Five {

    public static Result m5011(String idCode, String categoryRegId, File codeFile, String generateType) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("company_idcode", idCode);
        params.put("category_reg_id", categoryRegId);
        params.put("code_file", codeFile);
        params.put("generate_type", generateType);
        params.put("version", "1.0");
        Result result = null;
        try {
            result = new IDCodeApiExecute<Result>().execute(Result.class, params, UrlConst.URL_5011, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Result m5012(String idCode, String categoryRegId, String codeListStr, String generateType) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("company_idcode", idCode);
        params.put("category_reg_id", categoryRegId);
        params.put("code_list_str", codeListStr);
        params.put("generate_type", generateType);
        params.put("version", "1.0");
        Result result = null;
        try {
            result = new IDCodeApiExecute<Result>().execute(Result.class, params, UrlConst.URL_5012, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Result m5013(String idCode, String categoryRegId, String prefixStr, Integer startNum, Integer endNum, String generateType) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("company_idcode", idCode);
        params.put("category_reg_id", categoryRegId);
        params.put("prefix_str", prefixStr);
        params.put("start_num", startNum);
        params.put("end_num", endNum);
        params.put("generate_type", generateType);
        params.put("version", "1.0");
        Result result = null;
        try {
            result = new IDCodeApiExecute<Result>().execute(Result.class, params, UrlConst.URL_5013, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Result m5014(String idCode, String categoryRegId, String prefixStr, Integer startNum, Integer endNum, Integer length, String generateType) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("company_idcode", idCode);
        params.put("category_reg_id", categoryRegId);
        params.put("prefix_str", prefixStr);
        params.put("start_num", startNum);
        params.put("end_num", endNum);
        params.put("length", length);
        params.put("generate_type", generateType);
        params.put("version", "1.0");
        Result result = null;
        try {
            result = new IDCodeApiExecute<Result>().execute(Result.class, params, UrlConst.URL_5014, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static CodeRecordResult m502(String idCode, String idCodeOfCategory, String modelNumberCode) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("company_idcode", idCode);
        params.put("idcode_of_category", idCodeOfCategory);
        params.put("model_number_code", modelNumberCode);
        CodeRecordResult result = null;
        try {
            result = new IDCodeApiExecute<CodeRecordResult>().execute(CodeRecordResult.class, params, UrlConst.URL_502, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static CodeRecordResult m503(String idCode, String uploadCodeId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("company_idcode", idCode);
        params.put("uploadcode_id", uploadCodeId);
        CodeRecordResult result = null;
        try {
            result = new IDCodeApiExecute<CodeRecordResult>().execute(CodeRecordResult.class, params, UrlConst.URL_503, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static BaseIdCodeInfo m504(String idCode, String uploadCodeId, String password, String codeType) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("company_idcode", idCode);
        params.put("uploadcode_id", uploadCodeId);
        params.put("password", password);
        params.put("code_type", codeType);
        BaseIdCodeInfo result = null;
        try {
            result = new IDCodeApiExecute<BaseIdCodeInfo>().execute(BaseIdCodeInfo.class, params, UrlConst.URL_504, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ResultObject m505(String idCode, String uploadCodeId, String codeType) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("company_idcode", idCode);
        params.put("uploadcode_id", uploadCodeId);
        params.put("code_type", codeType);
        ResultObject result = null;
        try {
            result = new IDCodeApiExecute<ResultObject>().execute(ResultObject.class, params, UrlConst.URL_505, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ResultObject m506(String code) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("code", code);
        ResultObject result = null;
        try {
            result = new IDCodeApiExecute<ResultObject>().execute(ResultObject.class, params, UrlConst.URL_506, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
