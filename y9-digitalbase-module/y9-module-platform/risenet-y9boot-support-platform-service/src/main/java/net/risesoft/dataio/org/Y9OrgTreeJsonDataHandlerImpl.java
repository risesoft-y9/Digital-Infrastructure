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

import net.risesoft.dataio.org.model.Y9DepartmentJsonModel;
import net.risesoft.dataio.org.model.Y9DepartmentPropJsonModel;
import net.risesoft.dataio.org.model.Y9GroupJsonModel;
import net.risesoft.dataio.org.model.Y9JobJsonModel;
import net.risesoft.dataio.org.model.Y9OptionClassJsonModel;
import net.risesoft.dataio.org.model.Y9OptionValueJsonModel;
import net.risesoft.dataio.org.model.Y9OrganizationJsonModel;
import net.risesoft.dataio.org.model.Y9PersonExtJsonModel;
import net.risesoft.dataio.org.model.Y9PersonJsonModel;
import net.risesoft.dataio.org.model.Y9PersonsToGroupsJsonModel;
import net.risesoft.dataio.org.model.Y9PersonsToPositionsJsonModel;
import net.risesoft.dataio.org.model.Y9PositionJsonModel;
import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9DepartmentProp;
import net.risesoft.entity.Y9Group;
import net.risesoft.entity.Y9Job;
import net.risesoft.entity.Y9OptionClass;
import net.risesoft.entity.Y9OptionValue;
import net.risesoft.entity.Y9Organization;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9PersonExt;
import net.risesoft.entity.Y9Position;
import net.risesoft.entity.relation.Y9PersonsToGroups;
import net.risesoft.entity.relation.Y9PersonsToPositions;
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
import net.risesoft.y9.exception.Y9NotFoundException;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9ModelConvertUtil;

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
        Y9OrganizationJsonModel y9OrganizationJsonModel = this.buildOrganization(orgId);
        String jsonStr = Y9JsonUtil.writeValueAsStringWithDefaultPrettyPrinter(y9OrganizationJsonModel);

        try {
            IOUtils.write(jsonStr, outputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    @Override
    public void importOrgTree(Y9OrganizationJsonModel y9OrganizationJsonModel) {
        importJobList(y9OrganizationJsonModel.getJobList());
        importOptionClassList(y9OrganizationJsonModel.getOptionClassList());

        Y9Organization y9Organization = Y9ModelConvertUtil.convert(y9OrganizationJsonModel, Y9Organization.class);
        y9OrganizationService.saveOrUpdate(y9Organization);
        importDepartmentList(y9OrganizationJsonModel.getDepartmentList());
        importPersonList(y9OrganizationJsonModel.getPersonList());
        importPositionList(y9OrganizationJsonModel.getPositionList());
        importGroupList(y9OrganizationJsonModel.getGroupList());
    }

    private void importGroupList(List<Y9GroupJsonModel> groupList) {
        for (Y9GroupJsonModel y9GroupJsonModel : groupList) {
            Y9Group y9Group = Y9ModelConvertUtil.convert(y9GroupJsonModel, Y9Group.class);
            y9GroupService.saveOrUpdate(y9Group);
            List<Y9PersonsToGroupsJsonModel> personsToGroupsList = y9GroupJsonModel.getPersonsToGroupsList();
            importPersonsToGroupsList(personsToGroupsList);
        }
    }

    private void importPersonsToGroupsList(List<Y9PersonsToGroupsJsonModel> personsToGroupsList) {
        for (Y9PersonsToGroupsJsonModel personsToGroupsJsonModel : personsToGroupsList) {
            Y9PersonsToGroups y9PersonsToGroups =
                Y9ModelConvertUtil.convert(personsToGroupsJsonModel, Y9PersonsToGroups.class);
            try {
                y9PersonsToGroupsService.saveOrUpdate(y9PersonsToGroups);
            } catch (Y9NotFoundException ignored) {
                // 只有人员和用户组同时存在时关联关系才会添加成功
            }
        }
    }

    private void importPositionList(List<Y9PositionJsonModel> positionList) {
        for (Y9PositionJsonModel y9PositionJsonModel : positionList) {
            Y9Position y9Position = Y9ModelConvertUtil.convert(y9PositionJsonModel, Y9Position.class);
            y9PositionService.saveOrUpdate(y9Position);
            List<Y9PersonsToPositionsJsonModel> personsToPositionsList =
                y9PositionJsonModel.getPersonsToPositionsList();
            importPersonsToPositionsList(personsToPositionsList);
        }
    }

    private void importPersonsToPositionsList(List<Y9PersonsToPositionsJsonModel> personsToPositionsList) {
        for (Y9PersonsToPositionsJsonModel y9PersonsToPositionsJsonModel : personsToPositionsList) {
            Y9PersonsToPositions y9PersonsToPositions =
                Y9ModelConvertUtil.convert(y9PersonsToPositionsJsonModel, Y9PersonsToPositions.class);
            try {
                y9PersonsToPositionsService.saveOrUpdate(y9PersonsToPositions);
            } catch (Y9NotFoundException ignored) {
                // 只有人员和岗位同时存在时关联关系才会添加成功
            }
        }
    }

    private void importPersonList(List<Y9PersonJsonModel> personList) {
        for (Y9PersonJsonModel y9PersonJsonModel : personList) {
            Y9Person y9Person = Y9ModelConvertUtil.convert(y9PersonJsonModel, Y9Person.class);
            Y9PersonExt y9PersonExt = Y9ModelConvertUtil.convert(y9PersonJsonModel.getY9PersonExt(), Y9PersonExt.class);
            y9PersonService.saveOrUpdate(y9Person, y9PersonExt);

            importPersonsToGroupsList(y9PersonJsonModel.getPersonsToGroupsList());
            importPersonsToPositionsList(y9PersonJsonModel.getPersonsToPositionsList());
        }
    }

    private void importDepartmentList(List<Y9DepartmentJsonModel> departmentList) {
        for (Y9DepartmentJsonModel y9DepartmentJsonModel : departmentList) {
            Y9Department y9Department = Y9ModelConvertUtil.convert(y9DepartmentJsonModel, Y9Department.class);
            y9DepartmentService.saveOrUpdate(y9Department);
            importDepartmentPropList(y9DepartmentJsonModel.getDepartmentPropList());
            importDepartmentList(y9DepartmentJsonModel.getSubDepartmentList());
            importGroupList(y9DepartmentJsonModel.getGroupList());
            importPositionList(y9DepartmentJsonModel.getPositionList());
            importPersonList(y9DepartmentJsonModel.getPersonList());
        }
    }

    private void importDepartmentPropList(List<Y9DepartmentPropJsonModel> departmentPropList) {
        for (Y9DepartmentPropJsonModel y9DepartmentPropJsonModel : departmentPropList) {
            Y9DepartmentProp y9DepartmentProp =
                Y9ModelConvertUtil.convert(y9DepartmentPropJsonModel, Y9DepartmentProp.class);
            y9DepartmentPropService.saveOrUpdate(y9DepartmentProp);
        }
    }

    private void importOptionClassList(List<Y9OptionClassJsonModel> optionClassList) {
        for (Y9OptionClassJsonModel y9OptionClassJsonModel : optionClassList) {
            Y9OptionClass y9OptionClass = Y9ModelConvertUtil.convert(y9OptionClassJsonModel, Y9OptionClass.class);
            y9OptionClassService.saveOptionClass(y9OptionClass);
            for (Y9OptionValueJsonModel y9OptionValueJsonModel : y9OptionClassJsonModel.getOptionValueList()) {
                Y9OptionValue y9OptionValue = Y9ModelConvertUtil.convert(y9OptionValueJsonModel, Y9OptionValue.class);
                y9OptionValueService.saveOptionValue(y9OptionValue);
            }
        }
    }

    private void importJobList(List<Y9JobJsonModel> jobList) {
        for (Y9JobJsonModel y9JobJsonModel : jobList) {
            Y9Job y9Job = Y9ModelConvertUtil.convert(y9JobJsonModel, Y9Job.class);
            y9JobService.saveOrUpdate(y9Job);
        }
    }

    private Y9OrganizationJsonModel buildOrganization(String orgId) {
        Y9Organization y9Organization = y9OrganizationService.getById(orgId);
        Y9OrganizationJsonModel y9OrganizationJsonModel =
            Y9ModelConvertUtil.convert(y9Organization, Y9OrganizationJsonModel.class);
        y9OrganizationJsonModel.setOptionClassList(this.buildOptionClassList());
        y9OrganizationJsonModel.setJobList(this.buildJobList());
        y9OrganizationJsonModel.setDepartmentList(this.buildDepartmentList(orgId));
        y9OrganizationJsonModel.setGroupList(this.buildGroupList(orgId));
        y9OrganizationJsonModel.setPersonList(this.buildPersonList(orgId));
        y9OrganizationJsonModel.setPositionList(this.buildPositionList(orgId));
        return y9OrganizationJsonModel;
    }

    private List<Y9PositionJsonModel> buildPositionList(String parentId) {
        List<Y9PositionJsonModel> y9PositionJsonModelList = new ArrayList<>();
        List<Y9Position> y9PositionList = y9PositionService.listByParentId(parentId, null);
        for (Y9Position y9Position : y9PositionList) {
            y9PositionJsonModelList.add(this.buildPosition(y9Position));
        }
        return y9PositionJsonModelList;
    }

    private Y9PositionJsonModel buildPosition(Y9Position y9Position) {
        Y9PositionJsonModel y9PositionJsonModel = Y9ModelConvertUtil.convert(y9Position, Y9PositionJsonModel.class);
        y9PositionJsonModel.setPersonsToPositionsList(this.buildPersonsToPositionsListByPositionId(y9Position.getId()));
        return y9PositionJsonModel;
    }

    private List<Y9PersonsToPositionsJsonModel> buildPersonsToPositionsListByPositionId(String positionId) {
        List<Y9PersonsToPositions> y9PersonsToPositionsList = y9PersonsToPositionsService.listByPositionId(positionId);
        return Y9ModelConvertUtil.convert(y9PersonsToPositionsList, Y9PersonsToPositionsJsonModel.class);
    }

    private List<Y9PersonJsonModel> buildPersonList(String parentId) {
        List<Y9PersonJsonModel> y9PersonJsonModelList = new ArrayList<>();
        List<Y9Person> y9PersonList = y9PersonService.listByParentId(parentId, null);
        for (Y9Person y9Person : y9PersonList) {
            y9PersonJsonModelList.add(this.buildPerson(y9Person));
        }
        return y9PersonJsonModelList;
    }

    private Y9PersonJsonModel buildPerson(Y9Person y9Person) {
        Y9PersonJsonModel y9PersonJsonModel = Y9ModelConvertUtil.convert(y9Person, Y9PersonJsonModel.class);
        y9PersonJsonModel.setY9PersonExt(this.buildPersonExt(y9Person.getId()));
        y9PersonJsonModel.setPersonsToGroupsList(this.buildPersonsToGroupsListByPersonId(y9Person.getId()));
        y9PersonJsonModel.setPersonsToPositionsList(this.buildPersonsToPositionsListByPersonId(y9Person.getId()));
        return y9PersonJsonModel;
    }

    private List<Y9PersonsToPositionsJsonModel> buildPersonsToPositionsListByPersonId(String personId) {
        List<Y9PersonsToPositions> y9PersonsToPositionsList = y9PersonsToPositionsService.listByPersonId(personId);
        return Y9ModelConvertUtil.convert(y9PersonsToPositionsList, Y9PersonsToPositionsJsonModel.class);
    }

    private List<Y9PersonsToGroupsJsonModel> buildPersonsToGroupsListByPersonId(String personId) {
        List<Y9PersonsToGroups> y9PersonsToGroupsList = y9PersonsToGroupsService.listByPersonId(personId);
        return Y9ModelConvertUtil.convert(y9PersonsToGroupsList, Y9PersonsToGroupsJsonModel.class);
    }

    private Y9PersonExtJsonModel buildPersonExt(String personId) {
        Optional<Y9PersonExt> y9PersonExtOptional = y9PersonExtService.findByPersonId(personId);
        return y9PersonExtOptional.map(y9PersonExt -> Y9ModelConvertUtil.convert(y9PersonExt,
            Y9PersonExtJsonModel.class, ignorePersonExtProperties)).orElse(null);
    }

    private List<Y9GroupJsonModel> buildGroupList(String parentId) {
        List<Y9GroupJsonModel> y9GroupJsonModelList = new ArrayList<>();
        List<Y9Group> y9GroupList = y9GroupService.listByParentId(parentId, null);
        for (Y9Group y9Group : y9GroupList) {
            y9GroupJsonModelList.add(this.buildGroup(y9Group));
        }
        return y9GroupJsonModelList;
    }

    private Y9GroupJsonModel buildGroup(Y9Group y9Group) {
        Y9GroupJsonModel y9GroupJsonModel = Y9ModelConvertUtil.convert(y9Group, Y9GroupJsonModel.class);
        y9GroupJsonModel.setPersonsToGroupsList(this.buildPersonsToGroupsListByGroupId(y9Group.getId()));
        return y9GroupJsonModel;
    }

    private List<Y9PersonsToGroupsJsonModel> buildPersonsToGroupsListByGroupId(String groupId) {
        List<Y9PersonsToGroups> y9PersonsToGroupsList = y9PersonsToGroupsService.listByGroupId(groupId);
        return Y9ModelConvertUtil.convert(y9PersonsToGroupsList, Y9PersonsToGroupsJsonModel.class);
    }

    private List<Y9DepartmentJsonModel> buildDepartmentList(String parentId) {
        List<Y9DepartmentJsonModel> y9DepartmentJsonModelList = new ArrayList<>();
        List<Y9Department> y9DepartmentList = y9DepartmentService.listByParentId(parentId, null);
        for (Y9Department y9Department : y9DepartmentList) {
            y9DepartmentJsonModelList.add(this.buildDepartment(y9Department));
        }
        return y9DepartmentJsonModelList;
    }

    private Y9DepartmentJsonModel buildDepartment(Y9Department y9Department) {
        Y9DepartmentJsonModel y9DepartmentJsonModel =
            Y9ModelConvertUtil.convert(y9Department, Y9DepartmentJsonModel.class);
        y9DepartmentJsonModel.setDepartmentPropList(this.buildDepartmentPropList(y9Department.getId()));
        y9DepartmentJsonModel.setSubDepartmentList(this.buildDepartmentList(y9Department.getId()));
        y9DepartmentJsonModel.setGroupList(this.buildGroupList(y9Department.getId()));
        y9DepartmentJsonModel.setPositionList(this.buildPositionList(y9Department.getId()));
        y9DepartmentJsonModel.setPersonList(this.buildPersonList(y9Department.getId()));
        return y9DepartmentJsonModel;
    }

    private List<Y9DepartmentPropJsonModel> buildDepartmentPropList(String departmentId) {
        List<Y9DepartmentProp> y9DepartmentPropList = y9DepartmentPropService.listByDeptId(departmentId);
        return Y9ModelConvertUtil.convert(y9DepartmentPropList, Y9DepartmentPropJsonModel.class);
    }

    private List<Y9JobJsonModel> buildJobList() {
        List<Y9Job> y9JobList = y9JobService.listAll();
        return Y9ModelConvertUtil.convert(y9JobList, Y9JobJsonModel.class);
    }

    private List<Y9OptionClassJsonModel> buildOptionClassList() {
        List<Y9OptionClassJsonModel> y9OptionClassJsonModelList = new ArrayList<>();
        List<Y9OptionClass> y9OptionClassList = y9OptionClassService.list();
        for (Y9OptionClass y9OptionClass : y9OptionClassList) {
            y9OptionClassJsonModelList.add(this.buildOptionClass(y9OptionClass));
        }
        return y9OptionClassJsonModelList;
    }

    private Y9OptionClassJsonModel buildOptionClass(Y9OptionClass y9OptionClass) {
        Y9OptionClassJsonModel y9OptionClassJsonModel =
            Y9ModelConvertUtil.convert(y9OptionClass, Y9OptionClassJsonModel.class);
        y9OptionClassJsonModel.setOptionValueList(this.buildOptionValueList(y9OptionClass));
        return y9OptionClassJsonModel;
    }

    private List<Y9OptionValueJsonModel> buildOptionValueList(Y9OptionClass y9OptionClass) {
        List<Y9OptionValue> y9OptionValueList = y9OptionValueService.listByType(y9OptionClass.getType());
        return Y9ModelConvertUtil.convert(y9OptionValueList, Y9OptionValueJsonModel.class);
    }

}
