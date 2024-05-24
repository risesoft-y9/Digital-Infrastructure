package net.risesoft.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 针对于idcode平台接口返回值的定义
 * 
 */
public class Result {

	@JsonProperty("result_code")
	private int resultCode;

	@JsonProperty("result_msg")
	private String resultMsg;

	@JsonProperty("ResultCode")
	private int resultCodeError;

	@JsonProperty("ResultMsg")
	private String resultMsgError;

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}

	public int getResultCodeError() {
		return resultCodeError;
	}

	public void setResultCodeError(int resultCodeError) {
		this.resultCodeError = resultCodeError;
	}

	public String getResultMsgError() {
		return resultMsgError;
	}

	public void setResultMsgError(String resultMsgError) {
		this.resultMsgError = resultMsgError;
	}
}
