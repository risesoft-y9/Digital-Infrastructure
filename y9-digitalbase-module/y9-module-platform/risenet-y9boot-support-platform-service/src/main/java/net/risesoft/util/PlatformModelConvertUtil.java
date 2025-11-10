package net.risesoft.util;

import java.util.ArrayList;
import java.util.List;

import net.risesoft.entity.org.Y9Department;
import net.risesoft.entity.org.Y9Group;
import net.risesoft.entity.org.Y9Manager;
import net.risesoft.entity.org.Y9OrgBase;
import net.risesoft.entity.org.Y9Organization;
import net.risesoft.entity.org.Y9Person;
import net.risesoft.entity.org.Y9Position;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.model.platform.Role;
import net.risesoft.model.platform.org.Department;
import net.risesoft.model.platform.org.Group;
import net.risesoft.model.platform.org.Manager;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.Organization;
import net.risesoft.model.platform.org.Person;
import net.risesoft.model.platform.org.Position;
import net.risesoft.model.platform.resource.App;
import net.risesoft.model.platform.resource.DataCatalog;
import net.risesoft.model.platform.resource.Menu;
import net.risesoft.model.platform.resource.Operation;
import net.risesoft.model.platform.resource.Resource;
import net.risesoft.model.platform.tenant.Tenant;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.Y9Role;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9DataCatalog;
import net.risesoft.y9public.entity.resource.Y9Menu;
import net.risesoft.y9public.entity.resource.Y9Operation;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;
import net.risesoft.y9public.entity.tenant.Y9Tenant;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public class PlatformModelConvertUtil extends Y9ModelConvertUtil {

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
            orgUnit = y9PersonToPerson(person);
        } else if (OrgTypeEnum.MANAGER.equals(base.getOrgType())) {
            Y9Manager manager = (Y9Manager)base;
            orgUnit = y9ManagerToManager(manager);
        } else if (OrgTypeEnum.DEPARTMENT.equals(base.getOrgType())) {
            Y9Department dept = (Y9Department)base;
            orgUnit = y9DepartmentToDepartment(dept);
        } else if (OrgTypeEnum.GROUP.equals(base.getOrgType())) {
            Y9Group group = (Y9Group)base;
            orgUnit = y9GroupToGroup(group);
        } else if (OrgTypeEnum.POSITION.equals(base.getOrgType())) {
            Y9Position position = (Y9Position)base;
            orgUnit = y9PositionToPosition(position);
        } else {
            Y9Organization org = (Y9Organization)base;
            orgUnit = y9OrganizationToOrganization(org);
        }
        return orgUnit;
    }

    public static Organization y9OrganizationToOrganization(Y9Organization y9Organization) {
        return convert(y9Organization, Organization.class);
    }

    public static List<Organization> y9OrganizationToOrganization(List<Y9Organization> y9OrganizationList) {
        List<Organization> organizationList = new ArrayList<>();
        for (Y9Organization y9Organization : y9OrganizationList) {
            organizationList.add(y9OrganizationToOrganization(y9Organization));
        }
        return organizationList;
    }

    public static Position y9PositionToPosition(Y9Position y9Position) {
        return convert(y9Position, Position.class);
    }

    public static List<Position> y9PositionToPosition(List<Y9Position> y9PositionList) {
        List<Position> positionList = new ArrayList<>();
        for (Y9Position y9Position : y9PositionList) {
            positionList.add(y9PositionToPosition(y9Position));
        }
        return positionList;
    }

    public static Group y9GroupToGroup(Y9Group group) {
        return convert(group, Group.class);
    }

    public static List<Group> y9GroupToGroup(List<Y9Group> y9GroupList) {
        List<Group> groupList = new ArrayList<>();
        for (Y9Group group : y9GroupList) {
            groupList.add(y9GroupToGroup(group));
        }
        return groupList;
    }

    public static Department y9DepartmentToDepartment(Y9Department y9Department) {
        return convert(y9Department, Department.class);
    }

    public static List<Department> y9DepartmentToDepartment(List<Y9Department> y9DepartmentList) {
        List<Department> departmentList = new ArrayList<>();
        for (Y9Department y9Department : y9DepartmentList) {
            departmentList.add(y9DepartmentToDepartment(y9Department));
        }
        return departmentList;
    }

    public static Manager y9ManagerToManager(Y9Manager manager) {
        return convert(manager, Manager.class);
    }

    public static List<Manager> y9ManagerToManager(List<Y9Manager> y9ManagerList) {
        List<Manager> managerList = new ArrayList<>();
        for (Y9Manager y9Manager : y9ManagerList) {
            managerList.add(y9ManagerToManager(y9Manager));
        }
        return managerList;
    }

    public static Person y9PersonToPerson(Y9Person y9Person) {
        return convert(y9Person, Person.class);
    }

    public static List<Person> y9PersonToPerson(List<Y9Person> y9PersonList) {
        List<Person> personList = new ArrayList<>();
        for (Y9Person y9Person : y9PersonList) {
            personList.add(y9PersonToPerson(y9Person));
        }
        return personList;
    }

    public static List<Resource> resourceBaseToResource(List<? extends Y9ResourceBase> y9ResourceBaseList) {
        List<Resource> resourceList = new ArrayList<>();

        for (Y9ResourceBase y9ResourceBase : y9ResourceBaseList) {
            resourceList.add(resourceBaseToResource(y9ResourceBase));
        }

        return resourceList;
    }

    public static Resource resourceBaseToResource(Y9ResourceBase y9ResourceBase) {
        if (y9ResourceBase == null) {
            return null;
        }

        if (y9ResourceBase instanceof Y9App) {
            Y9App app = (Y9App)y9ResourceBase;
            return y9AppToApp(app);
        } else if (y9ResourceBase instanceof Y9Menu) {
            Y9Menu menu = (Y9Menu)y9ResourceBase;
            return y9MenuToMenu(menu);
        } else if (y9ResourceBase instanceof Y9Operation) {
            Y9Operation operation = (Y9Operation)y9ResourceBase;
            return y9OperationToOperation(operation);
        } else if (y9ResourceBase instanceof Y9DataCatalog) {
            Y9DataCatalog dataCatalog = (Y9DataCatalog)y9ResourceBase;
            return y9DataCatalogToDataCatalog(dataCatalog);
        }

        return null;
    }

    public static List<DataCatalog> y9DataCatalogToDataCatalog(List<Y9DataCatalog> y9DataCatalogList) {
        return convert(y9DataCatalogList, DataCatalog.class);
    }

    public static DataCatalog y9DataCatalogToDataCatalog(Y9DataCatalog dataCatalog) {
        return convert(dataCatalog, DataCatalog.class);
    }

    public static List<Operation> y9OperationToOperation(List<Y9Operation> y9OperationList) {
        return convert(y9OperationList, Operation.class);
    }

    public static Operation y9OperationToOperation(Y9Operation operation) {
        return convert(operation, Operation.class);
    }

    public static List<Menu> y9MenuToMenu(List<Y9Menu> y9MenuList) {
        return convert(y9MenuList, Menu.class);
    }

    public static Menu y9MenuToMenu(Y9Menu menu) {
        return convert(menu, Menu.class);
    }

    public static List<App> y9AppToApp(List<Y9App> y9AppList) {
        return convert(y9AppList, App.class);
    }

    public static App y9AppToApp(Y9App app) {
        return convert(app, App.class);
    }

    public static List<Role> y9RoleToRole(List<Y9Role> y9RoleList) {
        List<Role> roleList = new ArrayList<>();
        for (Y9Role y9Role : y9RoleList) {
            roleList.add(y9RoleToRole(y9Role));
        }
        return roleList;
    }

    public static Role y9RoleToRole(Y9Role y9Role) {
        if (y9Role == null) {
            return null;
        }

        Role role = new Role();
        Y9BeanUtil.copyProperties(y9Role, role);
        // if (StringUtils.isNotEmpty(y9Role.getProperties())) {
        // Map<String, String> values = Y9JsonUtil.readHashMap(y9Role.getProperties(), String.class, String.class);
        // role.setValues(values);
        // }
        return role;
    }

    public static List<Tenant> y9TenantToTenant(List<Y9Tenant> y9TenantList) {
        return convert(y9TenantList, Tenant.class);
    }

    public static Tenant y9TenantToTenant(Y9Tenant y9Tenant) {
        return convert(y9Tenant, Tenant.class);
    }

}
