package net.risesoft.dataio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.jxls.builder.xls.XlsCommentAreaBuilder;
import org.jxls.common.Context;
import org.jxls.transform.Transformer;
import org.jxls.util.JxlsHelper;

public class JxlsUtil {
    static {
        // 添加自定义指令（可覆盖jxls原指令）
        XlsCommentAreaBuilder.addCommandMapping("merge", MergeCommand.class);
    }

    public void exportExcel(InputStream templateInputstream, OutputStream os, Map<String, Object> beans)
        throws IOException {
        Context context = new Context();
        if (beans != null) {
            for (Map.Entry<String, Object> entry : beans.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                context.putVar(key, value);
            }
        }
        JxlsHelper jxlsHelper = JxlsHelper.getInstance();
        Transformer transformer = jxlsHelper.createTransformer(templateInputstream, os);
        Map<String, Object> funcs = new HashMap<>();
        funcs.put("jx", new JxlsUtil());
        // 添加自定义功能
        jxlsHelper.setUseFastFormulaProcessor(false).processTemplate(context, transformer);
    }
}
