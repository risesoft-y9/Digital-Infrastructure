package net.risesoft.util;

import org.springframework.stereotype.Component;

import cn.hutool.core.date.DateTime;

@Component
public class EsIndexYear {

    public String getYearStr() {
        return String.valueOf(DateTime.now().year());
    }

}
