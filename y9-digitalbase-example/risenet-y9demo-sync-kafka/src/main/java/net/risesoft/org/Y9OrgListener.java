package net.risesoft.org;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.model.platform.SyncOrgUnits;
import net.risesoft.model.platform.org.Department;
import net.risesoft.model.platform.org.Group;
import net.risesoft.model.platform.org.Organization;
import net.risesoft.model.platform.org.Person;
import net.risesoft.model.platform.org.PersonsGroups;
import net.risesoft.model.platform.org.PersonsPositions;
import net.risesoft.model.platform.org.Position;
import net.risesoft.y9.pubsub.constant.Y9OrgEventTypeConst;
import net.risesoft.y9.pubsub.event.Y9EventOrg;

@Component
@Slf4j
public class Y9OrgListener implements ApplicationListener<Y9EventOrg> {

    public Y9OrgListener() {
        LOGGER.info("init SpringEventListener...");
    }

    @Override
    public void onApplicationEvent(Y9EventOrg event) {
        LOGGER.info(event.getEventType());

        if (Y9OrgEventTypeConst.ORGANIZATION_ADD.equals(event.getEventType())) {
            Organization org = (Organization)event.getOrgObj();
            LOGGER.info("--------------------------添加组织机构-------------------------------");
            LOGGER.info("--------------------" + org.getName() + "---------------------");
        } else if (Y9OrgEventTypeConst.DEPARTMENT_ADD.equals(event.getEventType())) {
            Department newDept = (Department)event.getOrgObj();
            LOGGER.info("--------------------------添加部门-------------------------------");
            LOGGER.info("--------------------" + newDept.getName() + "---------------------");
        } else if (Y9OrgEventTypeConst.GROUP_ADD.equals(event.getEventType())) {
            Group group = (Group)event.getOrgObj();
            LOGGER.info("--------------------------添加用户组-------------------------------");
            LOGGER.info("--------------------" + group.getName() + "---------------------");
        } else if (Y9OrgEventTypeConst.POSITION_ADD.equals(event.getEventType())) {
            Position position = (Position)event.getOrgObj();
            LOGGER.info("--------------------------添加岗位-------------------------------");
            LOGGER.info("--------------------" + position.getName() + "---------------------");
        } else if (Y9OrgEventTypeConst.PERSON_ADD.equals(event.getEventType())) {
            Person person = (Person)event.getOrgObj();
            LOGGER.info("--------------------------添加人员-------------------------------");
            LOGGER.info("--------------------" + person.getName() + "---------------------");
        } else if (Y9OrgEventTypeConst.ORGANIZATION_UPDATE.equals(event.getEventType())) {
            Organization org = (Organization)event.getOrgObj();
            LOGGER.info("--------------------------修改组织机构-------------------------------");
            LOGGER.info("--------------------" + org.getName() + "---------------------");
        } else if (Y9OrgEventTypeConst.DEPARTMENT_UPDATE.equals(event.getEventType())) {
            Department newDept = (Department)event.getOrgObj();
            LOGGER.info("--------------------------修改部门-------------------------------");
            LOGGER.info("--------------------" + newDept.getName() + "---------------------");
        } else if (Y9OrgEventTypeConst.GROUP_UPDATE.equals(event.getEventType())) {
            Group group = (Group)event.getOrgObj();
            LOGGER.info("--------------------------修改用户组-------------------------------");
            LOGGER.info("--------------------" + group.getName() + "---------------------");
        } else if (Y9OrgEventTypeConst.POSITION_UPDATE.equals(event.getEventType())) {
            Position position = (Position)event.getOrgObj();
            LOGGER.info("--------------------------修改岗位-------------------------------");
            LOGGER.info("--------------------" + position.getName() + "---------------------");
        } else if (Y9OrgEventTypeConst.PERSON_UPDATE.equals(event.getEventType())) {
            Person person = (Person)event.getOrgObj();
            LOGGER.info("--------------------------修改人员-------------------------------");
            LOGGER.info("--------------------" + person.getName() + "---------------------");
        } else if (Y9OrgEventTypeConst.ORGANIZATION_DELETE.equals(event.getEventType())) {
            Organization org = (Organization)event.getOrgObj();
            LOGGER.info("--------------------------删除组织机构-------------------------------");
            LOGGER.info("--------------------" + org.getName() + "---------------------");
        } else if (Y9OrgEventTypeConst.DEPARTMENT_DELETE.equals(event.getEventType())) {
            Department newDept = (Department)event.getOrgObj();
            LOGGER.info("--------------------------删除部门-------------------------------");
            LOGGER.info("--------------------" + newDept.getName() + "---------------------");
        } else if (Y9OrgEventTypeConst.GROUP_DELETE.equals(event.getEventType())) {
            Group group = (Group)event.getOrgObj();
            LOGGER.info("--------------------------删除用户组-------------------------------");
            LOGGER.info("--------------------" + group.getName() + "---------------------");
        } else if (Y9OrgEventTypeConst.POSITION_DELETE.equals(event.getEventType())) {
            Position position = (Position)event.getOrgObj();
            LOGGER.info("--------------------------删除岗位-------------------------------");
            LOGGER.info("--------------------" + position.getName() + "---------------------");
        } else if (Y9OrgEventTypeConst.PERSON_DELETE.equals(event.getEventType())) {
            Person person = (Person)event.getOrgObj();
            LOGGER.info("--------------------------删除人员-------------------------------");
            LOGGER.info("--------------------" + person.getName() + "---------------------");
        } else if (Y9OrgEventTypeConst.GROUP_ADD_PERSON.equals(event.getEventType())) {
            PersonsGroups personsGroups = (PersonsGroups)event.getOrgObj();
            LOGGER.info("--------------------------用户组添加人员-------------------------------");
            LOGGER.info("--------------------" + personsGroups.getPersonOrder() + "---------------------");
        } else if (Y9OrgEventTypeConst.GROUP_REMOVE_PERSON.equals(event.getEventType())) {
            PersonsGroups personsGroups = (PersonsGroups)event.getOrgObj();
            LOGGER.info("--------------------------用户组删除人员-------------------------------");
            LOGGER.info("--------------------" + personsGroups.getPersonOrder() + "---------------------");
        } else if (Y9OrgEventTypeConst.GROUP_ORDER.equals(event.getEventType())) {
            PersonsGroups personsGroups = (PersonsGroups)event.getOrgObj();
            LOGGER.info("--------------------------用户组-人员排序-------------------------------");
            LOGGER.info("--------------------" + personsGroups.getGroupOrder() + "---------------------");
            LOGGER.info("--------------------" + personsGroups.getPersonOrder() + "---------------------");
        } else if (Y9OrgEventTypeConst.POSITION_ADD_PERSON.equals(event.getEventType())) {
            PersonsPositions personsPositions = (PersonsPositions)event.getOrgObj();
            LOGGER.info("--------------------------岗位添加人员-------------------------------");
            LOGGER.info("--------------------" + personsPositions.getPersonOrder() + "---------------------");
        } else if (Y9OrgEventTypeConst.POSITION_REMOVE_PERSON.equals(event.getEventType())) {
            PersonsPositions personsPositions = (PersonsPositions)event.getOrgObj();
            LOGGER.info("--------------------------岗位删除人员-------------------------------");
            LOGGER.info("--------------------" + personsPositions.getPersonOrder() + "---------------------");
        } else if (Y9OrgEventTypeConst.POSITION_ORDER.equals(event.getEventType())) {
            PersonsPositions personsPositions = (PersonsPositions)event.getOrgObj();
            LOGGER.info("--------------------------岗位-人员排序-------------------------------");
            LOGGER.info("--------------------" + personsPositions.getPositionOrder() + "---------------------");
            LOGGER.info("--------------------" + personsPositions.getPersonOrder() + "---------------------");
        } else if (Y9OrgEventTypeConst.SYNC.equals(event.getEventType())) {
            SyncOrgUnits syncOrgUnits = (SyncOrgUnits)event.getOrgObj();
            OrgTypeEnum orgType = syncOrgUnits.getOrgTypeEnum();
            switch (orgType) {
                case ORGANIZATION:
                    LOGGER.info("--------------------------同步-组织机构-------------------------------");
                    OrganizationUtil.syncOrganization(syncOrgUnits);
                    break;
                case DEPARTMENT:
                    LOGGER.info("--------------------------同步-部门-------------------------------");
                    DepartmentUtil.syncDepartment(syncOrgUnits);
                    break;
                case PERSON:
                    LOGGER.info("--------------------------同步-人员-------------------------------");
                    PersonUtil.syncPerson(syncOrgUnits.getPersons().get(0));
                    break;
                case GROUP:
                    LOGGER.info("--------------------------同步-用户组-------------------------------");
                    GroupUtil.syncGroup(syncOrgUnits);
                    break;
                case POSITION:
                    LOGGER.info("--------------------------同步-岗位-------------------------------");
                    PositionUtil.syncPosition(syncOrgUnits);
                    break;
                default:
                    break;
            }
        }
    }
}
