package net.risesoft.model.itemadmin;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class SearchModel implements Serializable {

    private static final long serialVersionUID = 5956404014190341862L;

    private String taskId;

    private String taskName;

    private String userName;

    private String itembox;

    private String processInstanceId;

    private String processDefinitionId;

    private Date time;

    private String processSerialNumber;

    private String documentTitle;

    private String level;

    private String number;

    private String itemId;

    private String itemName;

}