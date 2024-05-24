package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class IndustryCategoryResult extends Result{

	@JsonProperty("industrycategory_list")
	private List<IndustryCategory> list;

	public List<IndustryCategory> getList() {
		return list;
	}

	public void setList(List<IndustryCategory> list) {
		this.list = list;
	}
	
}
