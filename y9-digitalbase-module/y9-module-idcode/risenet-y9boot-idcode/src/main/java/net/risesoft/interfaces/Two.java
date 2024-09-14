package net.risesoft.interfaces;

import java.util.HashMap;
import java.util.Map;

import net.risesoft.consts.UrlConst;
import net.risesoft.model.CodeUseInfoResult;
import net.risesoft.model.IndustryCategoryResult;
import net.risesoft.util.Config;
import net.risesoft.util.IdCodeApiExecute;

/**
 * 品类分类相关基础数据接口
 */
public class Two {

    /**
     * 获取人、事、物所有用途接口
     *
     * @return
     */
    public static CodeUseInfoResult m201() {
        Map<String, Object> params = new HashMap<>();
        params.put("access_token", Config.API_KEY);
        params.put("time", System.currentTimeMillis());
        CodeUseInfoResult result = null;
        try {
            result = new IdCodeApiExecute<CodeUseInfoResult>().execute(CodeUseInfoResult.class, params,
                UrlConst.URL_201, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取所有品类接口
     *
     * @return
     */
    public static IndustryCategoryResult m202() {
        Map<String, Object> params = new HashMap<>();
        params.put("access_token", Config.API_KEY);
        params.put("time", System.currentTimeMillis());
        IndustryCategoryResult result = null;
        try {
            result = new IdCodeApiExecute<IndustryCategoryResult>().execute(IndustryCategoryResult.class, params,
                UrlConst.URL_202, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取某一级品类接口
     *
     * @param parentId 父级ID，为0时获取到第一级品类
     * @return
     */
    public static IndustryCategoryResult m203(Integer parentId) {
        Map<String, Object> params = new HashMap<>();
        params.put("access_token", Config.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("industrycategory_id_parent", parentId);
        IndustryCategoryResult result = null;
        try {
            result = new IdCodeApiExecute<IndustryCategoryResult>().execute(IndustryCategoryResult.class, params,
                UrlConst.URL_203, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取产品所有品类接口
     * 
     * @return
     */
    public static IndustryCategoryResult m204() {
        Map<String, Object> params = new HashMap<>();
        params.put("access_token", Config.API_KEY);
        params.put("time", System.currentTimeMillis());
        IndustryCategoryResult result = null;
        try {
            result = new IdCodeApiExecute<IndustryCategoryResult>().execute(IndustryCategoryResult.class, params,
                UrlConst.URL_204, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
