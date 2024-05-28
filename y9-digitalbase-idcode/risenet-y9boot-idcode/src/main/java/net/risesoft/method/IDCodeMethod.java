package net.risesoft.method;

import net.risesoft.consts.UrlConst;
import net.risesoft.model.Category;
import net.risesoft.model.CategoryResult;
import net.risesoft.model.CodePicResult;
import net.risesoft.model.IdcodeRegResult;
import net.risesoft.model.IndustryCategoryResult;
import net.risesoft.model.Result;
import net.risesoft.model.StatusResult;
import net.risesoft.util.ConfigReader;
import net.risesoft.util.IDCodeApiExecute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IDCodeMethod {

    public IdcodeRegResult signBackProductCategory(String model_number, String model_number_code, String code_pay_type,
                                                   String gotourl, String sample_url, String type) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("company_idcode", ConfigReader.MAIN_CODE);
        params.put("codeuse_id", "10");
        if (type.equals("1")) {//网站
            params.put("industrycategory_id", 10222);
            params.put("category_code", "46100000");
        } else {//其它
            params.put("industrycategory_id", 10223);
            params.put("category_code", "46101000");
        }
        params.put("model_number", model_number);
        params.put("model_number_code", model_number_code);
        params.put("code_pay_type", code_pay_type);
        params.put("gotourl", gotourl);
        params.put("sample_url", sample_url);
        IdcodeRegResult result = null;
        try {
            result = new IDCodeApiExecute<IdcodeRegResult>().execute(IdcodeRegResult.class, params, UrlConst.URL_406, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public IdcodeRegResult signBackPersonCategory(String model_number, String model_number_code, String code_pay_type,
                                                  String gotourl, String sample_url) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("company_idcode", ConfigReader.MAIN_CODE);
        params.put("codeuse_id", "73");
        params.put("industrycategory_id", 10127);
        params.put("category_code", "10000000");
        params.put("model_number", model_number);
        params.put("model_number_code", "rise" + model_number_code);
        params.put("code_pay_type", code_pay_type);
        params.put("gotourl", gotourl);
        params.put("sample_url", sample_url);
        IdcodeRegResult result = null;
        try {
            result = new IDCodeApiExecute<IdcodeRegResult>().execute(IdcodeRegResult.class, params, UrlConst.URL_407, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public IndustryCategoryResult getProductCategory() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        IndustryCategoryResult result = null;
        try {
            result = new IDCodeApiExecute<IndustryCategoryResult>()
                    .execute(IndustryCategoryResult.class, params, UrlConst.URL_204, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public CategoryResult queryAllCategory(String search_type) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("company_idcode", ConfigReader.MAIN_CODE);
        params.put("search_type", search_type);
        CategoryResult result = null;
        try {
            result = new IDCodeApiExecute<CategoryResult>().execute(CategoryResult.class, params, UrlConst.URL_404, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public Result modifyUrl(String gotourl, String sample_url, String reg_id) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("company_idcode", ConfigReader.MAIN_CODE);
        params.put("gotourl", gotourl);
        params.put("sample_url", sample_url);
        params.put("reg_id", reg_id);
        Result result = null;
        try {
            result = new IDCodeApiExecute<Result>().execute(Result.class, params, UrlConst.URL_602, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public CodePicResult createCodePic(String code, String pic_size, String code_type) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("code", code);
        params.put("pic_size", pic_size);
        params.put("code_type", code_type);
        CodePicResult result = null;
        try {
            result = new IDCodeApiExecute<CodePicResult>().execute(CodePicResult.class, params, UrlConst.URL_603, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 获取审核状态
     *
     * @param type            审核类型，参考{@link net.risesoft.enums.AuditTypeEnum}
     * @param category_reg_id 品类申请ID(如果type={@link net.risesoft.enums.AuditTypeEnum#RESOLVE_ADDRESS}，则必填)
     * @returns
     */
    public StatusResult queryStatus(String type, String category_reg_id) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("company_idcode", ConfigReader.MAIN_CODE);
        params.put("type", type); // 1单位 2 解析地址
        params.put("category_reg_id", category_reg_id); // type=2必填 品类申请ID
        StatusResult result = null;
        try {
            result = new IDCodeApiExecute<StatusResult>().execute(StatusResult.class, params, UrlConst.URL_608, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public CategoryResult getIDCodeList(String search_type) {
        List<Map<String, Object>> listMap1 = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> listMap2 = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> listMap3 = new ArrayList<Map<String, Object>>();
        CategoryResult result = this.queryAllCategory(search_type);
        if (result.getResultMsg().equals("成功")) {
            List<Category> list = result.getList();
            for (Category category : list) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("model_number", category.getModel_number());
                map.put("model_number_code", category.getModel_number_code());
                map.put("idcode", category.getComplete_code());
                map.put("time", category.getBill_number());

                String category_reg_id = category.getCategory_reg_idstr();
                StatusResult status = this.queryStatus("2", category_reg_id);
                if (status.getResultCode() == 1) {
                    String s = status.getExamine_status();
                    if (s.equals("-1")) {
                        map.put("examine_remarks", "审核不通过");
                    } else if (s.equals("0")) {
                        map.put("examine_remarks", "待审核");
                    } else if (s.equals("100")) {
                        map.put("examine_remarks", "审核通过");
                    } else {
                        map.put("examine_remarks", "审核中");
                    }
                } else {
                    map.put("examine_remarks", status.getResultMsg());
                }

                Integer code_pay_type = category.getCode_pay_type();
                if (code_pay_type == 2) {
                    map.put("type", "注册类型：" + category.getCategory_memo());
                } else {
                    map.put("type", "备案类型：" + category.getCategory_memo());
                }

                String IDCode = category.getComplete_code();
                CodePicResult codePic = this.createCodePic(IDCode, "600", "1");
                if (codePic.getResultCode() == 1) {
                    map.put("imgUrl", codePic.getCode_pic_address());
                }
                if (category.getCodeuse_id() == 10 && category.getCategory_code().equals("46100000")) {
                    listMap1.add(map);
                } else if (category.getCodeuse_id() == 10 && category.getCategory_code().equals("46101000")) {
                    listMap3.add(map);
                } else if (category.getCodeuse_id() == 73 && category.getCategory_code().equals("10000000")) {
                    listMap2.add(map);
                }
            }
        }
        listMap1.stream().forEach(map -> {
            map.forEach((k, v) -> System.out.println(k + ":" + v));
        });
        listMap2.stream().forEach(map -> {
            map.forEach((k, v) -> System.out.println(k + ":" + v));
        });
        listMap3.stream().forEach(map -> {
            map.forEach((k, v) -> System.out.println(k + ":" + v));
        });
        return result;
    }
}
