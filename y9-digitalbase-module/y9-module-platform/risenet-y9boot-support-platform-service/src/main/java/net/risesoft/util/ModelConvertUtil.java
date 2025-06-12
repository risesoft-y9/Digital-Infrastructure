package net.risesoft.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9Group;
import net.risesoft.entity.Y9Manager;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Organization;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.Group;
import net.risesoft.model.platform.Manager;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Organization;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Position;
import net.risesoft.model.platform.Resource;
import net.risesoft.model.platform.Role;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;
import net.risesoft.y9public.entity.role.Y9Role;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public class ModelConvertUtil extends Y9ModelConvertUtil {

    public static List<OrgUnit> orgBaseToOrgUnit(List<? extends Y9OrgBase> y9OrgBaseList) {
        List<OrgUnit> orgUnitList = new ArrayList<>();
        for (Y9OrgBase y9OrgBase : y9OrgBaseList) {
            orgUnitList.add(orgBaseToOrgUnit(y9OrgBase));
        }
        return orgUnitList;
    }

    public static OrgUnit orgBaseToOrgUnit(Y9OrgBase base) {
        if (base == null) {
            return null;
        }
        OrgUnit orgUnit;
        if (OrgTypeEnum.PERSON.equals(base.getOrgType())) {
            Y9Person person = (Y9Person)base;
            orgUnit = orgPersonToPerson(person);
        } else if (OrgTypeEnum.MANAGER.equals(base.getOrgType())) {
            Y9Manager manager = (Y9Manager)base;
            orgUnit = convert(manager, Manager.class);
        } else if (OrgTypeEnum.DEPARTMENT.equals(base.getOrgType())) {
            Y9Department dept = (Y9Department)base;
            orgUnit = convert(dept, Department.class);
        } else if (OrgTypeEnum.GROUP.equals(base.getOrgType())) {
            Y9Group group = (Y9Group)base;
            orgUnit = convert(group, Group.class);
        } else if (OrgTypeEnum.POSITION.equals(base.getOrgType())) {
            Y9Position position = (Y9Position)base;
            orgUnit = convert(position, Position.class);
        } else {
            Y9Organization org = (Y9Organization)base;
            orgUnit = convert(org, Organization.class);
        }
        return orgUnit;
    }

    public static Person orgPersonToPerson(Y9Person y9Person) {
        Person person = convert(y9Person, Person.class);
        person.setPassword(null);
        return person;
    }

    public static Resource resourceBaseToResource(Y9ResourceBase y9ResourceBase) {
        if (y9ResourceBase == null) {
            return null;
        }

        Resource resource = new Resource();
        Y9BeanUtil.copyProperties(y9ResourceBase, resource);
        return resource;
    }

    public static Role y9RoleToRole(Y9Role y9Role) {
        if (y9Role == null) {
            return null;
        }

        Role role = new Role();
        Y9BeanUtil.copyProperties(y9Role, role);
        if (StringUtils.isNotEmpty(y9Role.getProperties())) {
            Map<String, String> values = Y9JsonUtil.readHashMap(y9Role.getProperties(), String.class, String.class);
            role.setValues(values);
        }
        return role;
    }

}
