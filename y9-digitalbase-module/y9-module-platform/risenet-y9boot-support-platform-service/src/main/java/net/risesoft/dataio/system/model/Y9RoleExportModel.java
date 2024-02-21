package net.risesoft.dataio.system.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.y9public.entity.role.Y9Role;

/**
 * Y9RoleDTO
 *
 * @author shidaobang
 * @date 2022/6/7
 */
@Getter
@Setter
public class Y9RoleExportModel extends Y9Role {

    private static final long serialVersionUID = 5734021385079857067L;

    private List<Y9RoleExportModel> subRoleList;

    public List<Y9RoleExportModel> getSubRoleList() {
        return subRoleList;
    }

    public void setSubRoleList(List<Y9RoleExportModel> subRoleList) {
        this.subRoleList = subRoleList;
    }
}
