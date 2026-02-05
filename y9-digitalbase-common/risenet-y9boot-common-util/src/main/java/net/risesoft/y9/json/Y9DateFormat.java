package net.risesoft.y9.json;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import tools.jackson.databind.util.StdDateFormat;


/**
 * 时间格式化器
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public class Y9DateFormat extends DateFormat {
    private static final long serialVersionUID = -6176158877433688690L;

    SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    StdDateFormat defaultDf = StdDateFormat.instance;

    @Override
    public Object clone() {
        return this;
    }

    private DateFormat detectFormat(String source) {
        if (source == null) {
            return df1;
        }

        String str = source.trim();
        if (str.length() == "yyyy-MM-dd HH:mm:ss".length() && str.contains("-") && str.contains(" ")
            && str.contains(":")) {
            return df1;
        } else if (str.length() == "yyyy-MM-dd HH:mm".length() && str.contains("-") && str.contains(" ")
            && str.contains(":")) {
            return df2;
        }
        return defaultDf;
    }

    @Override
    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
        return df1.format(date, toAppendTo, fieldPosition);
    }

    @Override
    public Date parse(String source, ParsePosition pos) {
        Date date = detectFormat(source).parse(source, pos);
        return date;
    }
}
