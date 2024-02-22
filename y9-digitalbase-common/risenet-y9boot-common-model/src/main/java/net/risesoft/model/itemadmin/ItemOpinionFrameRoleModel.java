package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

@Data
public class ItemOpinionFrameRoleModel implements Serializable {

    private static final long serialVersionUID = 7079860101823150509L;

    private String id;

    private String itemOpinionFrameId;

    private String roleId;

    private String roleName;

}
