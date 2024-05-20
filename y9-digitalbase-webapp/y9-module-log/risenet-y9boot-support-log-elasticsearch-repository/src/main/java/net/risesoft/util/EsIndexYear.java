package net.risesoft.util;

import cn.hutool.core.date.DateTime;

public class EsIndexYear {

    public String getYearStr() {
        return String.valueOf(DateTime.now().year());
    }

}
