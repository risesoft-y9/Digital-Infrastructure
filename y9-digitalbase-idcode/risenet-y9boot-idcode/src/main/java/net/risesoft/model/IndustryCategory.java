package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IndustryCategory {

	@JsonProperty("industrycategory_id")
	private Integer id;
	
	@JsonProperty("industrycategory_name")
	private String name;
	
	@JsonProperty("industrycategory_code")
	private String code;
	
	@JsonProperty("industrycategory_id_parent")
	private Integer parentId;
	
	@JsonProperty("industrycategory_level")
	private Integer level;
	
	@JsonProperty("industrycategory_isdel")
	private Integer isDel;
	
	@JsonProperty("codeuse_id")
	private Integer useId;
	
	@JsonProperty("type_id")
	private Integer typeId;

	@JsonProperty("check_format")
	private String checkStr;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getIsDel() {
		return isDel;
	}

	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}

	public Integer getUseId() {
		return useId;
	}

	public void setUseId(Integer useId) {
		this.useId = useId;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public String getCheckStr() { return checkStr; }

	public void setCheckStr(String checkStr) { this.checkStr = checkStr; }
	
	
}
