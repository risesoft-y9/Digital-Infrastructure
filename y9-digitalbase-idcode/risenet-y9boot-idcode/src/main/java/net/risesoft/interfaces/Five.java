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
    /**
     * 上传码接口（方式一：上传TXT文件）
     *
     * @param idCode        单位主码(必填)
     * @param categoryRegId 品类注册ID(必填)
     * @param codeFile      码文件(必填)
     * @param generateType  简码生成方式(选填)1 按顺序生成，0 随机生成；默认值：1
     * @return
     */
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

    /**
     * 上传码接口（方式二：参数列表传递）
     *
     * @param idCode        单位主码(必填)
     * @param categoryRegId 品类注册ID(必填)
     * @param codeListStr   码号列表(“1,2,3”)
     * @param generateType  简码生成方式(选填)1 按顺序生成，0 随机生成；默认值：1
     * @return
     */
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

    /**
     * 上传码接口（方式三：前缀 + 起始号、终止号）
     *
     * @param idCode        单位主码(必填)
     * @param categoryRegId 品类注册ID(必填)
     * @param prefixStr     前缀
     * @param startNum      起始号(必填)
     * @param endNum        终止号(必填)
     * @param generateType  简码生成方式(选填)1 按顺序生成，0 随机生成；默认值：1
     * @return
     */
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

    /**
     * 上传码接口（方式四：前缀 + 起始号、终止号、数值长度，数值位数不够规定长度时高位补零）
     *
     * @param idCode        单位主码(必填)
     * @param categoryRegId 品类注册ID(必填)
     * @param prefixStr     前缀
     * @param startNum      起始号(必填)
     * @param endNum        终止号(必填)
     * @param length        数值长度(必填)
     * @param generateType  简码生成方式(选填)1 按顺序生成，0 随机生成；默认值：1
     * @return
     */
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

    /**
     * 按品类查询上传的二维码接口
     *
     * @param idCode           单位主码(必填)
     * @param idCodeOfCategory 品类码(必填)
     * @param modelNumberCode  型号码(必填)
     * @return
     */
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

    /**
     * 按上传批次ID查询上传的二维码接口
     *
     * @param idCode       单位主码(必填)
     * @param uploadCodeId 上传码批次id (必填，可通过接口502获取)
     * @return
     */
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

    /**
     * 获取一个IDcode码内容文件的下载地址
     *
     * @param idCode       单位主码(必填)
     * @param uploadCodeId 上传码ID(必填)
     * @param password     解压包解压密码（6~16位字符，非必填）
     * @param codeType     下载原码还是简码(选填)1 原码，0 简码；默认值：1
     * @return
     */
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

    /**
     * 根据上传批次ID获取IDcode码
     *
     * @param idCode       单位主码(必填)
     * @param uploadCodeId 上传码ID(必填)
     * @param codeType     下载原码还是简码(选填)1 原码，0 简码；默认值：1
     * @return
     */
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

    /**
     * 根据原码查询简码
     *
     * @param code 原码(必填)
     * @return
     */
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
