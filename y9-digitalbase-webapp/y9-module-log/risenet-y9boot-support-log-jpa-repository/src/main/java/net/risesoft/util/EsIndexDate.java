package net.risesoft.util;

import cn.hutool.core.date.DateTime;

public class EsIndexDate {

    public String getDateStr() {
        return String.valueOf(new DateTime().getYear());
    }

}
