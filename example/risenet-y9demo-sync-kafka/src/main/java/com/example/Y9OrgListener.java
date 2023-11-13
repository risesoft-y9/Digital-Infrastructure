package com.example;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.Group;
import net.risesoft.model.platform.Organization;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.PersonsGroups;
import net.risesoft.model.platform.PersonsPositions;
import net.risesoft.model.platform.Position;
import net.risesoft.y9.pubsub.constant.Y9OrgEventConst;
import net.risesoft.y9.pubsub.event.Y9EventOrg;

@Component
public class Y9OrgListener implements ApplicationListener<Y9EventOrg> {
    protected final Logger log = LoggerFactory.getLogger(Y9OrgListener.class);

    public Y9OrgListener() {
        log.info("init SpringEventListener...");
    }

    @Override
    public void onApplicationEvent(Y9EventOrg event) {
        log.info(event.getEventType());

        if (Y9OrgEventConst.RISEORGEVENT_TYPE_ADD_ORGANIZATION.equals(event.getEventType())) {
            Organization org = (Organization)event.getOrgObj();
            log.info("--------------------------添加组织机构-------------------------------");
            log.info("--------------------" + org.getName() + "---------------------");
        } else if (Y9OrgEventConst.RISEORGEVENT_TYPE_ADD_DEPARTMENT.equals(event.getEventType())) {
            Department newDept = (Department)event.getOrgObj();
            log.info("--------------------------添加部门-------------------------------");
            log.info("--------------------" + newDept.getName() + "---------------------");
        } else if (Y9OrgEventConst.RISEORGEVENT_TYPE_ADD_GROUP.equals(event.getEventType())) {
            Group group = (Group)event.getOrgObj();
            log.info("--------------------------添加用户组-------------------------------");
            log.info("--------------------" + group.getName() + "---------------------");
        } else if (Y9OrgEventConst.RISEORGEVENT_TYPE_ADD_POSITION.equals(event.getEventType())) {
            Position position = (Position)event.getOrgObj();
            log.info("--------------------------添加岗位-------------------------------");
            log.info("--------------------" + position.getName() + "---------------------");
        } else if (Y9OrgEventConst.RISEORGEVENT_TYPE_ADD_PERSON.equals(event.getEventType())) {
            Person person = (Person)event.getOrgObj();
            log.info("--------------------------添加人员-------------------------------");
            log.info("--------------------" + person.getName() + "---------------------");
        } else if (Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_ORGANIZATION.equals(event.getEventType())) {
            Organization org = (Organization)event.getOrgObj();
            log.info("--------------------------修改组织机构-------------------------------");
            log.info("--------------------" + org.getName() + "---------------------");
        } else if (Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_DEPARTMENT.equals(event.getEventType())) {
            Department newDept = (Department)event.getOrgObj();
            log.info("--------------------------修改部门-------------------------------");
            log.info("--------------------" + newDept.getName() + "---------------------");
        } else if (Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_GROUP.equals(event.getEventType())) {
            Group group = (Group)event.getOrgObj();
            log.info("--------------------------修改用户组-------------------------------");
            log.info("--------------------" + group.getName() + "---------------------");
        } else if (Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_POSITION.equals(event.getEventType())) {
            Position position = (Position)event.getOrgObj();
            log.info("--------------------------修改岗位-------------------------------");
            log.info("--------------------" + position.getName() + "---------------------");
        } else if (Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_PERSON.equals(event.getEventType())) {
            Person person = (Person)event.getOrgObj();
            log.info("--------------------------修改人员-------------------------------");
            log.info("--------------------" + person.getName() + "---------------------");
        } else if (Y9OrgEventConst.RISEORGEVENT_TYPE_DELETE_ORGANIZATION.equals(event.getEventType())) {
            Organization org = (Organization)event.getOrgObj();
            log.info("--------------------------删除组织机构-------------------------------");
            log.info("--------------------" + org.getName() + "---------------------");
        } else if (Y9OrgEventConst.RISEORGEVENT_TYPE_DELETE_DEPARTMENT.equals(event.getEventType())) {
            Department newDept = (Department)event.getOrgObj();
            log.info("--------------------------删除部门-------------------------------");
            log.info("--------------------" + newDept.getName() + "---------------------");
        } else if (Y9OrgEventConst.RISEORGEVENT_TYPE_DELETE_GROUP.equals(event.getEventType())) {
            Group group = (Group)event.getOrgObj();
            log.info("--------------------------删除用户组-------------------------------");
            log.info("--------------------" + group.getName() + "---------------------");
        } else if (Y9OrgEventConst.RISEORGEVENT_TYPE_DELETE_POSITION.equals(event.getEventType())) {
            Position position = (Position)event.getOrgObj();
            log.info("--------------------------删除岗位-------------------------------");
            log.info("--------------------" + position.getName() + "---------------------");
        } else if (Y9OrgEventConst.RISEORGEVENT_TYPE_DELETE_PERSON.equals(event.getEventType())) {
            Person person = (Person)event.getOrgObj();
            log.info("--------------------------删除人员-------------------------------");
            log.info("--------------------" + person.getName() + "---------------------");
        } else if (Y9OrgEventConst.RISEORGEVENT_TYPE_GROUP_ADDPERSON.equals(event.getEventType())) {
            PersonsGroups personsGroups = (PersonsGroups)event.getOrgObj();
            log.info("--------------------------用户组添加人员-------------------------------");
            log.info("--------------------" + personsGroups.getPersonOrder() + "---------------------");
        } else if (Y9OrgEventConst.RISEORGEVENT_TYPE_GROUP_REMOVEPERSON.equals(event.getEventType())) {
            PersonsGroups personsGroups = (PersonsGroups)event.getOrgObj();
            log.info("--------------------------用户组删除人员-------------------------------");
            log.info("--------------------" + personsGroups.getPersonOrder() + "---------------------");
        } else if (Y9OrgEventConst.RISEORGEVENT_TYPE_GROUP_ORDER.equals(event.getEventType())) {
            PersonsGroups personsGroups = (PersonsGroups)event.getOrgObj();
            log.info("--------------------------用户组-人员排序-------------------------------");
            log.info("--------------------" + personsGroups.getGroupOrder() + "---------------------");
            log.info("--------------------" + personsGroups.getPersonOrder() + "---------------------");
        } else if (Y9OrgEventConst.RISEORGEVENT_TYPE_POSITION_ADDPERSON.equals(event.getEventType())) {
            PersonsPositions personsPositions = (PersonsPositions)event.getOrgObj();
            log.info("--------------------------岗位添加人员-------------------------------");
            log.info("--------------------" + personsPositions.getPersonOrder() + "---------------------");
        } else if (Y9OrgEventConst.RISEORGEVENT_TYPE_POSITION_REMOVEPERSON.equals(event.getEventType())) {
            PersonsPositions personsPositions = (PersonsPositions)event.getOrgObj();
            log.info("--------------------------岗位删除人员-------------------------------");
            log.info("--------------------" + personsPositions.getPersonOrder() + "---------------------");
        } else if (Y9OrgEventConst.RISEORGEVENT_TYPE_POSITION_ORDER.equals(event.getEventType())) {
            PersonsPositions personsPositions = (PersonsPositions)event.getOrgObj();
            log.info("--------------------------岗位-人员排序-------------------------------");
            log.info("--------------------" + personsPositions.getPositionOrder() + "---------------------");
            log.info("--------------------" + personsPositions.getPersonOrder() + "---------------------");
        } else if (Y9OrgEventConst.RISEORGEVENT_TYPE_SYNC.equals(event.getEventType())) {
            @SuppressWarnings("unchecked")
            Map<String, Object> dataMap = (HashMap<String, Object>)event.getOrgObj();
            String ORG_TYPE = (String)dataMap.get(Y9OrgEventConst.ORG_TYPE);
            String SYNC_ID = (String)dataMap.get(Y9OrgEventConst.SYNC_ID);
            OrgTypeEnum orgTypeEnum = OrgTypeEnum.getByEnName(ORG_TYPE);
            switch (orgTypeEnum) {
                case ORGANIZATION:
                    log.info("--------------------------同步-组织机构-------------------------------");
                    OrganizationUtil.syncOrganization(dataMap);
                    break;
                case DEPARTMENT:
                    log.info("--------------------------同步-部门-------------------------------");
                    DepartmentUtil.syncDepartment(dataMap);
                    break;
                case PERSON:
                    log.info("--------------------------同步-人员-------------------------------");
                    Person person = (Person)dataMap.get(SYNC_ID);
                    try {
                        PersonUtil.syncPerson(person);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case GROUP:
                    log.info("--------------------------同步-用户组-------------------------------");
                    GroupUtil.syncGroup(dataMap);
                    break;
                case POSITION:
                    log.info("--------------------------同步-岗位-------------------------------");
                    PositionUtil.syncPosition(dataMap);
                    break;
                default:
                    break;
            }
        }
    }

}
