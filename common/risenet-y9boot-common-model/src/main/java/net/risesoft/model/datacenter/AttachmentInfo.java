package net.risesoft.model.datacenter;

import lombok.Data;

/**
 * 附件信息
 * 
 */
@Data
public class AttachmentInfo {

	/**
	 * 附件ID
	 */
	private String fileId;

	/**
	 * 附件名称
	 */
	private String fileName;

	/**
	 * 附件类型
	 */
	private String fileType;

	/**
	 * 附件URL
	 */
	private String fileUrl;

	/**
	 * 附件内容
	 */
	private String fileContent;

}
