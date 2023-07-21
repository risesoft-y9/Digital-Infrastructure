package net.risesoft.util;

import org.joda.time.DateTime;

public class EsIndexDate {
	
	public String getDateStr() {
		return String.valueOf(new DateTime().getYear());
	}
	
}
