package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * idcode406接口对应的返回对象
 *
 */
public class IdcodeRegResult extends Result{

	@JsonProperty("organunit_idcode")
	private String organunitIdcode;
	
	@JsonProperty("category_reg_id")
	private String categoryRegId;

	public String getOrganunitIdcode() {
		return organunitIdcode;
	}

	public void setOrganunitIdcode(String organunitIdcode) {
		this.organunitIdcode = organunitIdcode;
	}

	public String getCategoryRegId() {
		return categoryRegId;
	}

	public void setCategoryRegId(String categoryRegId) {
		this.categoryRegId = categoryRegId;
	}
	
}
