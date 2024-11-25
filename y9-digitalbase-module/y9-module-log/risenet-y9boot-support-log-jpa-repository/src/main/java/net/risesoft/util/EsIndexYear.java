package net.risesoft.util;

import java.util.Date;
import java.text.SimpleDateFormat;

public class EsIndexYear {

    public String getDateStr() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        return sdf.format(date);
    }

}
