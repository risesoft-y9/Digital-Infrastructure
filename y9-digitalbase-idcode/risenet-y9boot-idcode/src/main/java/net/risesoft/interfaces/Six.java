package net.risesoft.interfaces;

import net.risesoft.consts.UrlConst;
import net.risesoft.model.CodeAddress;
import net.risesoft.model.CodePicBase64;
import net.risesoft.util.ConfigReader;
import net.risesoft.util.IDCodeApiExecute;

import java.util.HashMap;
import java.util.Map;

public class Six {

    public static CodeAddress m603(String code, Integer picSize, Integer codeType) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("code", code);
        params.put("pic_size", picSize);
        params.put("code_type", codeType);
        CodeAddress result = null;
        try {
            result = new IDCodeApiExecute<CodeAddress>().execute(CodeAddress.class, params, UrlConst.URL_603, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static CodePicBase64 m604(String code, Integer isMargin, String unitIcon, Integer qrCodeSize, Integer color) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("code", code);
        params.put("is_margin", isMargin);
        params.put("unit_icon", unitIcon);
        params.put("qrcode_size", qrCodeSize);
        params.put("color", color);
        CodePicBase64 result = null;
        try {
            result = new IDCodeApiExecute<CodePicBase64>().execute(CodePicBase64.class, params, UrlConst.URL_604, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static CodePicBase64 m605(String idCode, String code, Integer useLogo, String unitLogo, Integer marginType, Integer categoryId, Integer marginTypeLv2, Integer codeType, Integer codeSize, String codeColor) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("company_idcode", idCode);
        params.put("code", code);
        params.put("use_logo", useLogo);
        params.put("unit_logo", unitLogo);
        params.put("margin_type", marginType);
        params.put("category_id", categoryId);
        params.put("margin_type_lv2", marginTypeLv2);
        params.put("code_type", codeType);
        params.put("code_size", codeSize);
        params.put("code_color", codeColor);
        CodePicBase64 result = null;
        try {
            result = new IDCodeApiExecute<CodePicBase64>().execute(CodePicBase64.class, params, UrlConst.URL_605, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}