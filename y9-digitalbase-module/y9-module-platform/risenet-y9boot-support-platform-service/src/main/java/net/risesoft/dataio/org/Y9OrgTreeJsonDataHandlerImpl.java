package net.risesoft.dataio.org;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.dataio.org.model.DepartmentJsonModel;
import net.risesoft.dataio.org.model.DepartmentPropJsonModel;
import net.risesoft.dataio.org.model.GroupJsonModel;
import net.risesoft.dataio.org.model.JobJsonModel;
import net.risesoft.dataio.org.model.OptionClassJsonModel;
import net.risesoft.dataio.org.model.OptionValueJsonModel;
import net.risesoft.dataio.org.model.OrganizationJsonModel;
import net.risesoft.dataio.org.model.PersonExtJsonModel;
import net.risesoft.dataio.org.model.PersonJsonModel;
import net.risesoft.dataio.org.model.PersonsToGroupsJsonModel;
import net.risesoft.dataio.org.model.PersonsToPositionsJsonModel;
import net.risesoft.dataio.org.model.PositionJsonModel;
import net.risesoft.model.platform.dictionary.OptionClass;
import net.risesoft.model.platform.dictionary.OptionValue;
import net.risesoft.model.platform.org.Department;
import net.risesoft.model.platform.org.DepartmentProp;
import net.risesoft.model.platform.org.Group;
import net.risesoft.model.platform.org.Job;
import net.risesoft.model.platform.org.Organization;
import net.risesoft.model.platform.org.Person;
import net.risesoft.model.platform.org.PersonExt;
import net.risesoft.model.platform.org.PersonsGroups;
import net.risesoft.model.platform.org.PersonsPositions;
import net.risesoft.model.platform.org.Position;
import net.risesoft.service.dictionary.Y9OptionClassService;
import net.risesoft.service.dictionary.Y9OptionValueService;
import net.risesoft.service.org.Y9DepartmentPropService;
import net.risesoft.service.org.Y9DepartmentService;
import net.risesoft.service.org.Y9GroupService;
import net.risesoft.service.org.Y9JobService;
import net.risesoft.service.org.Y9OrganizationService;
import net.risesoft.service.org.Y9PersonExtService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.service.relation.Y9PersonsToGroupsService;
import net.risesoft.service.relation.Y9PersonsToPositionsService;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9.exception.Y9NotFoundException;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * 组织架构树 JSON 数据的导入导出
 *
 * @author shidaobang
 * @date 2025/02/10
 * @since 9.6.8
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class Y9OrgTreeJsonDataHandlerImpl implements Y9OrgTreeDataHandler {

    private final String[] ignorePersonExtProperties = new String[] {"photo", "sign"};

    private final Y9OrganizationService y9OrganizationService;
    private final Y9DepartmentService y9DepartmentService;
    private final Y9DepartmentPropService y9DepartmentPropService;
    private final Y9GroupService y9GroupService;
    private final Y9PositionService y9PositionService;
    private final Y9PersonService y9PersonService;
    private final Y9PersonExtService y9PersonExtService;

    private final Y9PersonsToGroupsService y9PersonsToGroupsService;
    private final Y9PersonsToPositionsService y9PersonsToPositionsService;

    private final Y9OptionClassService y9OptionClassService;
    private final Y9OptionValueService y9OptionValueService;

    private final Y9JobService y9JobService;

    @Override
    public void exportOrgTree(String orgId, OutputStream outputStream) {
        OrganizationJsonModel organizationJsonModel = this.buildOrganization(orgId);
        String jsonStr = Y9JsonUtil.writeValueAsStringWithDefaultPrettyPrinter(organizationJsonModel);

        try {
            IOUtils.write(jsonStr, outputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    @Override
    public void importOrgTree(OrganizationJsonModel organizationJsonModel) {
        importJobList(organizationJsonModel.getJobList());
        importOptionClassList(organizationJsonModel.getOptionClassList());

        Organization y9Organization = PlatformModelConvertUtil.convert(organizationJsonModel, Organization.class);
        y9OrganizationService.saveOrUpdate(y9Organization);
        importDepartmentList(organizationJsonModel.getDepartmentList());
        importPersonList(organizationJsonModel.getPersonList());
        importPositionList(organizationJsonModel.getPositionList());
        importGroupList(organizationJsonModel.getGroupList());
    }

    private void importGroupList(List<GroupJsonModel> groupList) {
        for (GroupJsonModel groupJsonModel : groupList) {
            Group y9Group = PlatformModelConvertUtil.convert(groupJsonModel, Group.class);
            y9GroupService.saveOrUpdate(y9Group);
            List<PersonsToGroupsJsonModel> personsToGroupsList = groupJsonModel.getPersonsToGroupsList();
            importPersonsToGroupsList(personsToGroupsList);
        }
    }

    private void importPersonsToGroupsList(List<PersonsToGroupsJsonModel> personsToGroupsList) {
        for (PersonsToGroupsJsonModel personsToGroupsJsonModel : personsToGroupsList) {
            PersonsGroups y9PersonsToGroups =
                PlatformModelConvertUtil.convert(personsToGroupsJsonModel, PersonsGroups.class);
            try {
                y9PersonsToGroupsService.saveOrUpdate(y9PersonsToGroups);
            } catch (Y9NotFoundException ignored) {
                // 只有人员和用户组同时存在时关联关系才会添加成功
            }
        }
    }

    private void importPositionList(List<PositionJsonModel> positionList) {
        for (PositionJsonModel positionJsonModel : positionList) {
            Position y9Position = PlatformModelConvertUtil.convert(positionJsonModel, Position.class);
            y9PositionService.saveOrUpdate(y9Position);
            List<PersonsToPositionsJsonModel> personsToPositionsList = positionJsonModel.getPersonsToPositionsList();
            importPersonsToPositionsList(personsToPositionsList);
        }
    }

    private void importPersonsToPositionsList(List<PersonsToPositionsJsonModel> personsToPositionsList) {
        for (PersonsToPositionsJsonModel personsToPositionsJsonModel : personsToPositionsList) {
            PersonsPositions y9PersonsToPositions =
                PlatformModelConvertUtil.convert(personsToPositionsJsonModel, PersonsPositions.class);
            try {
                y9PersonsToPositionsService.saveOrUpdate(y9PersonsToPositions);
            } catch (Y9NotFoundException ignored) {
                // 只有人员和岗位同时存在时关联关系才会添加成功
            }
        }
    }

    private void importPersonList(List<PersonJsonModel> personList) {
        for (PersonJsonModel personJsonModel : personList) {
            Person y9Person = PlatformModelConvertUtil.convert(personJsonModel, Person.class);
            PersonExt y9PersonExt = PlatformModelConvertUtil.convert(personJsonModel.getY9PersonExt(), PersonExt.class);
            y9PersonService.saveOrUpdate(y9Person, y9PersonExt);

            importPersonsToGroupsList(personJsonModel.getPersonsToGroupsList());
            importPersonsToPositionsList(personJsonModel.getPersonsToPositionsList());
        }
    }

    private void importDepartmentList(List<DepartmentJsonModel> departmentList) {
        for (DepartmentJsonModel departmentJsonModel : departmentList) {
            Department y9Department = PlatformModelConvertUtil.convert(departmentJsonModel, Department.class);
            y9DepartmentService.saveOrUpdate(y9Department);
            importDepartmentPropList(departmentJsonModel.getDepartmentPropList());
            importDepartmentList(departmentJsonModel.getSubDepartmentList());
            importGroupList(departmentJsonModel.getGroupList());
            importPositionList(departmentJsonModel.getPositionList());
            importPersonList(departmentJsonModel.getPersonList());
        }
    }

    private void importDepartmentPropList(List<DepartmentPropJsonModel> departmentPropList) {
        for (DepartmentPropJsonModel departmentPropJsonModel : departmentPropList) {
            DepartmentProp y9DepartmentProp =
                PlatformModelConvertUtil.convert(departmentPropJsonModel, DepartmentProp.class);
            y9DepartmentPropService.saveOrUpdate(y9DepartmentProp);
        }
    }

    private void importOptionClassList(List<OptionClassJsonModel> optionClassList) {
        for (OptionClassJsonModel optionClassJsonModel : optionClassList) {
            OptionClass y9OptionClass = PlatformModelConvertUtil.convert(optionClassJsonModel, OptionClass.class);
            y9OptionClassService.saveOptionClass(y9OptionClass);
            for (OptionValueJsonModel optionValueJsonModel : optionClassJsonModel.getOptionValueList()) {
                OptionValue y9OptionValue = PlatformModelConvertUtil.convert(optionValueJsonModel, OptionValue.class);
                y9OptionValueService.saveOptionValue(y9OptionValue);
            }
        }
    }

    private void importJobList(List<JobJsonModel> jobList) {
        for (JobJsonModel jobJsonModel : jobList) {
            Job y9Job = PlatformModelConvertUtil.convert(jobJsonModel, Job.class);
            y9JobService.saveOrUpdate(y9Job);
        }
    }

    private OrganizationJsonModel buildOrganization(String orgId) {
        Organization y9Organization = y9OrganizationService.getById(orgId);
        OrganizationJsonModel organizationJsonModel =
            PlatformModelConvertUtil.convert(y9Organization, OrganizationJsonModel.class);
        organizationJsonModel.setOptionClassList(this.buildOptionClassList());
        organizationJsonModel.setJobList(this.buildJobList());
        organizationJsonModel.setDepartmentList(this.buildDepartmentList(orgId));
        organizationJsonModel.setGroupList(this.buildGroupList(orgId));
        organizationJsonModel.setPersonList(this.buildPersonList(orgId));
        organizationJsonModel.setPositionList(this.buildPositionList(orgId));
        return organizationJsonModel;
    }

    private List<PositionJsonModel> buildPositionList(String parentId) {
        List<PositionJsonModel> positionJsonModelList = new ArrayList<>();
        List<Position> y9PositionList = y9PositionService.listByParentId(parentId, null);
        for (Position y9Position : y9PositionList) {
            positionJsonModelList.add(this.buildPosition(y9Position));
        }
        return positionJsonModelList;
    }

    private PositionJsonModel buildPosition(Position y9Position) {
        PositionJsonModel positionJsonModel = PlatformModelConvertUtil.convert(y9Position, PositionJsonModel.class);
        positionJsonModel.setPersonsToPositionsList(this.buildPersonsToPositionsListByPositionId(y9Position.getId()));
        return positionJsonModel;
    }

    private List<PersonsToPositionsJsonModel> buildPersonsToPositionsListByPositionId(String positionId) {
        List<PersonsPositions> y9PersonsToPositionsList = y9PersonsToPositionsService.listByPositionId(positionId);
        return PlatformModelConvertUtil.convert(y9PersonsToPositionsList, PersonsToPositionsJsonModel.class);
    }

    private List<PersonJsonModel> buildPersonList(String parentId) {
        List<PersonJsonModel> personJsonModelList = new ArrayList<>();
        List<Person> y9PersonList = y9PersonService.listByParentId(parentId, null);
        for (Person y9Person : y9PersonList) {
            personJsonModelList.add(this.buildPerson(y9Person));
        }
        return personJsonModelList;
    }

    private PersonJsonModel buildPerson(Person y9Person) {
        PersonJsonModel personJsonModel = PlatformModelConvertUtil.convert(y9Person, PersonJsonModel.class);
        personJsonModel.setY9PersonExt(this.buildPersonExt(y9Person.getId()));
        personJsonModel.setPersonsToGroupsList(this.buildPersonsToGroupsListByPersonId(y9Person.getId()));
        personJsonModel.setPersonsToPositionsList(this.buildPersonsToPositionsListByPersonId(y9Person.getId()));
        return personJsonModel;
    }

    private List<PersonsToPositionsJsonModel> buildPersonsToPositionsListByPersonId(String personId) {
        List<PersonsPositions> y9PersonsToPositionsList = y9PersonsToPositionsService.listByPersonId(personId);
        return PlatformModelConvertUtil.convert(y9PersonsToPositionsList, PersonsToPositionsJsonModel.class);
    }

    private List<PersonsToGroupsJsonModel> buildPersonsToGroupsListByPersonId(String personId) {
        List<PersonsGroups> y9PersonsToGroupsList = y9PersonsToGroupsService.listByPersonId(personId);
        return PlatformModelConvertUtil.convert(y9PersonsToGroupsList, PersonsToGroupsJsonModel.class);
    }

    private PersonExtJsonModel buildPersonExt(String personId) {
        Optional<PersonExt> y9PersonExtOptional = y9PersonExtService.findByPersonId(personId);
        return y9PersonExtOptional.map(y9PersonExt -> PlatformModelConvertUtil.convert(y9PersonExt,
            PersonExtJsonModel.class, ignorePersonExtProperties)).orElse(null);
    }

    private List<GroupJsonModel> buildGroupList(String parentId) {
        List<GroupJsonModel> groupJsonModelList = new ArrayList<>();
        List<Group> y9GroupList = y9GroupService.listByParentId(parentId, null);
        for (Group y9Group : y9GroupList) {
            groupJsonModelList.add(this.buildGroup(y9Group));
        }
        return groupJsonModelList;
    }

    private GroupJsonModel buildGroup(Group y9Group) {
        GroupJsonModel groupJsonModel = PlatformModelConvertUtil.convert(y9Group, GroupJsonModel.class);
        groupJsonModel.setPersonsToGroupsList(this.buildPersonsToGroupsListByGroupId(y9Group.getId()));
        return groupJsonModel;
    }

    private List<PersonsToGroupsJsonModel> buildPersonsToGroupsListByGroupId(String groupId) {
        List<PersonsGroups> y9PersonsToGroupsList = y9PersonsToGroupsService.listByGroupId(groupId);
        return PlatformModelConvertUtil.convert(y9PersonsToGroupsList, PersonsToGroupsJsonModel.class);
    }

    private List<DepartmentJsonModel> buildDepartmentList(String parentId) {
        List<DepartmentJsonModel> departmentJsonModelList = new ArrayList<>();
        List<Department> y9DepartmentList = y9DepartmentService.listByParentId(parentId, null);
        for (Department y9Department : y9DepartmentList) {
            departmentJsonModelList.add(this.buildDepartment(y9Department));
        }
        return departmentJsonModelList;
    }

    private DepartmentJsonModel buildDepartment(Department y9Department) {
        DepartmentJsonModel departmentJsonModel =
            PlatformModelConvertUtil.convert(y9Department, DepartmentJsonModel.class);
        departmentJsonModel.setDepartmentPropList(this.buildDepartmentPropList(y9Department.getId()));
        departmentJsonModel.setSubDepartmentList(this.buildDepartmentList(y9Department.getId()));
        departmentJsonModel.setGroupList(this.buildGroupList(y9Department.getId()));
        departmentJsonModel.setPositionList(this.buildPositionList(y9Department.getId()));
        departmentJsonModel.setPersonList(this.buildPersonList(y9Department.getId()));
        return departmentJsonModel;
    }

    private List<DepartmentPropJsonModel> buildDepartmentPropList(String departmentId) {
        List<DepartmentProp> y9DepartmentPropList = y9DepartmentPropService.listByDeptId(departmentId);
        return PlatformModelConvertUtil.convert(y9DepartmentPropList, DepartmentPropJsonModel.class);
    }

    private List<JobJsonModel> buildJobList() {
        List<Job> y9JobList = y9JobService.listAll();
        return PlatformModelConvertUtil.convert(y9JobList, JobJsonModel.class);
    }

    private List<OptionClassJsonModel> buildOptionClassList() {
        List<OptionClassJsonModel> optionClassJsonModelList = new ArrayList<>();
        List<OptionClass> optionClassList = y9OptionClassService.list();
        for (OptionClass optionClass : optionClassList) {
            optionClassJsonModelList.add(this.buildOptionClass(optionClass));
        }
        return optionClassJsonModelList;
    }

    private OptionClassJsonModel buildOptionClass(OptionClass optionClass) {
        OptionClassJsonModel optionClassJsonModel =
            PlatformModelConvertUtil.convert(optionClass, OptionClassJsonModel.class);
        optionClassJsonModel.setOptionValueList(this.buildOptionValueList(optionClass));
        return optionClassJsonModel;
    }

    private List<OptionValueJsonModel> buildOptionValueList(OptionClass optionClass) {
        List<OptionValue> optionValueList = y9OptionValueService.listByType(optionClass.getType());
        return PlatformModelConvertUtil.convert(optionValueList, OptionValueJsonModel.class);
    }

}
