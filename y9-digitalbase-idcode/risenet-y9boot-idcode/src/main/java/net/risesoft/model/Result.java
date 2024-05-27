package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 针对于idcode平台接口返回值的定义
 * 
 */
@Data
public class Result {

	@JsonProperty("result_code")
	private int resultCode;

	@JsonProperty("result_msg")
	private String resultMsg;

	@JsonProperty("ResultCode")
	private int resultCodeError;

	@JsonProperty("ResultMsg")
	private String resultMsgError;
}
