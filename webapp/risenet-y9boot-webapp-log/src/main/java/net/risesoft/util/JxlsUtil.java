package net.risesoft.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.jxls.builder.xls.XlsCommentAreaBuilder;
import org.jxls.common.Context;
import org.jxls.expression.JexlExpressionEvaluator;
import org.jxls.transform.Transformer;
import org.jxls.util.JxlsHelper;

public class JxlsUtil {
    static {
        // 添加自定义指令（可覆盖jxls原指令）
        XlsCommentAreaBuilder.addCommandMapping("merge", MergeCommand.class);
    }

    public void exportExcel(InputStream is, OutputStream os, Map<String, Object> beans) throws IOException {
        Context context = new Context();
        if (beans != null) {
            for (String key : beans.keySet()) {
                context.putVar(key, beans.get(key));
            }
        }
        JxlsHelper jxlsHelper = JxlsHelper.getInstance();
        Transformer transformer = jxlsHelper.createTransformer(is, os);
        JexlExpressionEvaluator evaluator = (JexlExpressionEvaluator)transformer.getTransformationConfig().getExpressionEvaluator();
        Map<String, Object> funcs = new HashMap<String, Object>();
        funcs.put("jx", new JxlsUtil()); // 添加自定义功能
        // evaluator.getJexlEngine().setFunctions(funcs);
        jxlsHelper.setUseFastFormulaProcessor(false).processTemplate(context, transformer);
    }
}
