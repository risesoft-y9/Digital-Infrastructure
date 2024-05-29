package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class IndustryCategoryResult extends Result{

	@JsonProperty("industrycategory_list")
	private List<IndustryCategory> list;
}