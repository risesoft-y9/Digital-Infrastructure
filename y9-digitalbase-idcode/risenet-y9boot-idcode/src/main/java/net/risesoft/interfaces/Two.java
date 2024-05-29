package net.risesoft.interfaces;

import net.risesoft.consts.UrlConst;
import net.risesoft.model.CodeUseInfoResult;
import net.risesoft.model.IndustryCategoryResult;
import net.risesoft.util.ConfigReader;
import net.risesoft.util.IDCodeApiExecute;

import java.util.HashMap;
import java.util.Map;

/**
 * 品类分类相关基础数据接口
 */
public class Two {

    public static CodeUseInfoResult codeUse() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        CodeUseInfoResult result = null;
        try {
            result = new IDCodeApiExecute<CodeUseInfoResult>().execute(CodeUseInfoResult.class, params, UrlConst.URL_201, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static IndustryCategoryResult industryCategory() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        IndustryCategoryResult result = null;
        try {
            result = new IDCodeApiExecute<IndustryCategoryResult>().execute(IndustryCategoryResult.class, params, UrlConst.URL_202, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static IndustryCategoryResult industryCategoryParentId(Integer parentId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("industrycategory_id_parent", parentId);
        IndustryCategoryResult result = null;
        try {
            result = new IDCodeApiExecute<IndustryCategoryResult>().execute(IndustryCategoryResult.class, params, UrlConst.URL_203, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static IndustryCategoryResult industryCategoryProduct() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        IndustryCategoryResult result = null;
        try {
            result = new IDCodeApiExecute<IndustryCategoryResult>().execute(IndustryCategoryResult.class, params, UrlConst.URL_204, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
