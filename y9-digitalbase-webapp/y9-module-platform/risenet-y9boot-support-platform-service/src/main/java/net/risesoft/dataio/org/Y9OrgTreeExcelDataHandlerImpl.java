package net.risesoft.dataio.org;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jxls.reader.ReaderBuilder;
import org.jxls.reader.XLSReadStatus;
import org.jxls.reader.XLSReader;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.OrgLevelConsts;
import net.risesoft.dataio.JxlsUtil;
import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9Group;
import net.risesoft.entity.Y9Job;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Organization;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.enums.platform.SexEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.pojo.ObjectSheet;
import net.risesoft.pojo.PersonInformation;
import net.risesoft.pojo.PersonSheet;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9DepartmentService;
import net.risesoft.service.org.Y9GroupService;
import net.risesoft.service.org.Y9JobService;
import net.risesoft.service.org.Y9OrganizationService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.y9.Y9LoginUserHolder;

import cn.hutool.core.io.resource.ClassPathResource;

@Service(value = "y9OrgTreeExcelDataHandler")
@Slf4j
@RequiredArgsConstructor
public class Y9OrgTreeExcelDataHandlerImpl implements Y9OrgTreeDataHandler {

    private static final String SPLITTER = ",";

    private final CompositeOrgBaseService compositeOrgBaseService;
    private final Y9DepartmentService y9DepartmentService;
    private final Y9PersonService y9PersonService;
    private final Y9OrganizationService y9OrganizationService;
    private final Y9PositionService y9PositionService;
    private final Y9GroupService y9GroupService;
    private final Y9JobService y9JobService;

    private void executeDepartment(String parentId, List<ObjectSheet> departmentList) {
        List<Y9Department> departments = y9DepartmentService.listByParentId(parentId, false);
        if (!departments.isEmpty()) {
            for (Y9Department department : departments) {
                ObjectSheet departmentSheet = new ObjectSheet();
                List<PersonSheet> personSheetList = new ArrayList<>();
                List<Y9Person> personList = y9PersonService.listByParentId(department.getId(), false);
                if (personList != null) {
                    for (Y9Person y9Person : personList) {
                        PersonSheet person = new PersonSheet();
                        person.setDn(y9Person.getDn());
                        person.setEmail(y9Person.getEmail());
                        person.setLoginName(y9Person.getLoginName());
                        person.setMobile(y9Person.getMobile());
                        person.setName(y9Person.getName());
                        person.setOfficeFax(y9Person.getOfficeFax());
                        person.setOfficePhone(y9Person.getOfficePhone());
                        person.setTabIndex(y9Person.getTabIndex());
                        personSheetList.add(person);
                    }
                } else {
                    PersonSheet person = new PersonSheet();
                    person.setNum(1);
                    personSheetList.add(person);
                }
                departmentSheet.setDn(department.getDn());
                departmentSheet.setName(department.getName());
                departmentSheet.setNum(departmentList.size() + 1);
                departmentSheet.setPersonList(personSheetList);
                departmentList.add(departmentSheet);
            }
            for (Y9Department department : departments) {
                executeDepartment(department.getId(), departmentList);
            }
        }
    }

    private List<ObjectSheet> executeGroup(List<Y9Group> groupList) {
        List<ObjectSheet> list = new ArrayList<>();
        if (!groupList.isEmpty()) {
            int i = 1;
            for (Y9Group group : groupList) {
                ObjectSheet groupSheet = new ObjectSheet();
                List<PersonSheet> personSheetList = new ArrayList<>();
                List<Y9Person> personList = y9PersonService.listByGroupId(group.getId(), Boolean.FALSE);
                if (personList != null) {
                    for (Y9Person y9Person : personList) {
                        PersonSheet person = new PersonSheet();
                        person.setDn(y9Person.getDn());
                        person.setName(y9Person.getName());
                        personSheetList.add(person);
                    }
                } else {
                    PersonSheet person = new PersonSheet();
                    personSheetList.add(person);
                }
                groupSheet.setDn(group.getDn());
                groupSheet.setName(group.getName());
                groupSheet.setPersonList(personSheetList);
                groupSheet.setNum(i++);
                list.add(groupSheet);
            }
        }
        return list;
    }

    private List<ObjectSheet> executePosition(List<Y9Position> positionList) {
        List<ObjectSheet> list = new ArrayList<>();
        if (!positionList.isEmpty()) {
            int i = 1;
            for (Y9Position position : positionList) {
                ObjectSheet positionSheet = new ObjectSheet();
                List<PersonSheet> personSheetList = new ArrayList<>();
                List<Y9Person> personList = y9PersonService.listByPositionId(position.getId(), Boolean.FALSE);
                if (personList != null) {
                    for (Y9Person y9Person : personList) {
                        PersonSheet person = new PersonSheet();
                        person.setDn(y9Person.getDn());
                        person.setName(y9Person.getName());
                        personSheetList.add(person);
                    }
                } else {
                    PersonSheet person = new PersonSheet();
                    personSheetList.add(person);
                }
                positionSheet.setDn(position.getDn());
                positionSheet.setName(position.getName());
                positionSheet.setPersonList(personSheetList);
                positionSheet.setNum(i++);
                list.add(positionSheet);
            }
        }
        return list;
    }

    @Override
    public void exportOrgTree(String orgId, OutputStream outputStream) {
        try (InputStream in = new ClassPathResource("/template/exportTemplate.xlsx").getStream()) {
            Map<String, Object> map = xlsOrgTreeData(orgId);
            JxlsUtil jxlsUtil = new JxlsUtil();
            jxlsUtil.exportExcel(in, outputStream, map);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    @Override
    public void exportPerson(String orgBaseId, OutputStream outputStream) {
        try (InputStream in = new ClassPathResource("/template/exportSimpleTemplate.xlsx").getStream()) {
            Map<String, Object> map = this.xlsPersonData(orgBaseId);
            JxlsUtil jxlsUtil = new JxlsUtil();
            jxlsUtil.exportExcel(in, outputStream, map);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    private Map<String, Object> impData2Db(PersonInformation pf, String orgId) {
        String fullPath;
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("isRepeat", "false");
        retMap.put("isMobileRepeat", "false");
        retMap.put("isMobileNull", "false");
        retMap.put("isMobileError", "false");
        if (StringUtils.isBlank(pf.getFullPath())) {
            fullPath = pf.getName();
        } else {
            fullPath = pf.getFullPath() + SPLITTER + pf.getName();
        }
        String[] paths = fullPath.split(SPLITTER);
        Y9OrgBase y9OrgBase = compositeOrgBaseService.getOrgUnit(orgId);
        String dn = y9OrgBase.getDn();
        String parentId = y9OrgBase.getId();
        for (int i = 0, length = paths.length; i < length; i++) {
            if (i == length - 1) {

                String personName = pf.getLoginName().replaceAll("\\s*", "");
                Optional<Y9Person> y9PersonOptional = y9PersonService.findByLoginName(personName);
                if (y9PersonOptional.isPresent()) {
                    // 人员重复
                    retMap.put("isRepeat", "true");
                    retMap.put("name", pf.getLoginName().replaceAll("\\s*", ""));
                    continue;
                }

                if (StringUtils.isBlank(pf.getMobile())) {
                    // 人员号码为空
                    retMap.put("isMobileNull", "true");
                    retMap.put("mobileNullNames", pf.getLoginName().replaceAll("\\s*", ""));
                    continue;
                }

                // 数值类型号码
                if (pf.getMobile().indexOf("E") > 0) {
                    BigDecimal mobileValue = new BigDecimal(pf.getMobile());
                    String mobile = mobileValue.toPlainString();
                    pf.setMobile(mobile);
                }
                String personMobile = pf.getMobile().replaceAll("\\s*", "");
                if (personMobile.length() == 11) {
                    // 手机号可重复 此处暂时移除手机号重复校验
                    // if (y9PersonService.getPersonByMobile(new BigDecimal(personMobile).toString()) != null) {
                    // // 人员号码重复
                    // retMap.put("isMobileRepeat", "true");
                    // retMap.put("mobileNames", pf.getLoginName().replaceAll("\\s*", ""));
                    // retMap.put("mobiles", new BigDecimal(pf.getMobile()).toString());
                    // }
                } else {
                    // 人员号码错误
                    retMap.put("isMobileError", "true");
                    retMap.put("mobileErrorNames", pf.getLoginName().replaceAll("\\s*", ""));
                    continue;
                }

                Y9Person y9Person = new Y9Person();
                y9Person.setName(paths[i].replaceAll("\\s*", ""));
                y9Person.setEmail(pf.getEmail());
                y9Person.setMobile(pf.getMobile().replaceAll("\\s*", ""));
                y9Person.setLoginName(pf.getLoginName().replaceAll("\\s*", ""));
                y9Person.setSex("男".equals(pf.getSex()) ? SexEnum.MALE : SexEnum.FEMALE);
                y9Person.setParentId(parentId);

                String jobs = pf.getJobs();
                if (StringUtils.isNotBlank(jobs)) {
                    String[] jobArray = jobs.split(SPLITTER);
                    List<String> y9JobIdList = new ArrayList<>();
                    for (String job : jobArray) {
                        y9JobIdList.add(y9JobService.create(job, job).getId());
                    }
                    y9PersonService.saveOrUpdate(y9Person, null, null, y9JobIdList);
                } else {
                    y9PersonService.saveOrUpdate(y9Person, null);
                }

            } else {
                dn = OrgLevelConsts.UNIT + paths[i] + SPLITTER + dn;
                List<Y9Department> departmentList = y9DepartmentService.listByDn(dn, false);
                if (!departmentList.isEmpty()) {
                    parentId = departmentList.get(0).getId();
                } else {
                    Y9Department department = new Y9Department();
                    department.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    department.setTenantId(Y9LoginUserHolder.getTenantId());
                    department.setName(paths[i].replaceAll("\\s*", ""));
                    department.setOrgType(OrgTypeEnum.DEPARTMENT);
                    department.setParentId(parentId);
                    Y9Department dept = y9DepartmentService.saveOrUpdate(department);
                    parentId = dept.getId();
                }
            }
        }
        return retMap;
    }

    @Override
    public Y9Result<Object> importOrgTree(InputStream inputStream, String orgId) {
        return null;
    }

    /**
     * 导入XLS组织架构到数据库
     *
     */
    @Override
    public Y9Result<Object> importPerson(InputStream dataInputStream, String orgId) {
        List<PersonInformation> personList = new ArrayList<>();
        Map<String, Object> ret = new HashMap<>(16);
        ret.put("success", false);
        Map<String, Object> map = new HashMap<>(16);
        map.put("personList", personList);
        StringBuilder repeatNames = new StringBuilder();
        StringBuilder repeatMobiles = new StringBuilder();
        StringBuilder mobileNulls = new StringBuilder();
        StringBuilder mobileErrors = new StringBuilder();
        try {

            InputStream xmlInputStream =
                new BufferedInputStream(this.getClass().getResourceAsStream("/template/xmlconfig.xml"));
            XLSReader xlsReader = ReaderBuilder.buildFromXML(xmlInputStream);
            XLSReadStatus readStatus = xlsReader.read(dataInputStream, map);
            if (readStatus.isStatusOK()) {
                LOGGER.info("################成功读取到XLS文件数据##########################");
                for (PersonInformation pf : personList) {
                    Map<String, Object> retMap = impData2Db(pf, orgId);
                    // 有人员重复，提示用户手动修改
                    if ("true".equals(retMap.get("isRepeat"))) {
                        if (StringUtils.isBlank(repeatNames)) {
                            repeatNames.append(retMap.get("name").toString());
                        } else {
                            repeatNames.append(repeatNames + "、" + retMap.get("name").toString());
                        }
                    }
                    // 有人员的号码为空
                    if ("true".equals(retMap.get("isMobileNull"))) {
                        if (StringUtils.isBlank(mobileNulls)) {
                            mobileNulls.append(retMap.get("mobileNullNames").toString());
                        } else {
                            mobileNulls.append(mobileNulls + "、" + retMap.get("mobileNullNames").toString());
                        }
                    }
                    // 有人员的号码错误
                    if ("true".equals(retMap.get("isMobileError"))) {
                        if (StringUtils.isBlank(mobileErrors)) {
                            mobileErrors.append(retMap.get("mobileErrorNames").toString());
                        } else {
                            mobileErrors.append(mobileErrors + "、" + retMap.get("mobileErrorNames").toString());
                        }
                    }
                    // 有手机号码重复，提示用户手动修改
                    if ("true".equals(retMap.get("isMobileRepeat"))) {
                        if (StringUtils.isBlank(repeatMobiles)) {
                            repeatMobiles
                                .append(retMap.get("mobileNames").toString() + ":" + retMap.get("mobiles").toString());
                        } else {
                            repeatMobiles.append(repeatMobiles + "、" + retMap.get("mobileNames").toString() + ":"
                                + retMap.get("mobiles").toString());
                        }
                    }
                }
                ret.put("success", true);
                if (StringUtils.isBlank(repeatNames.toString())) {
                    ret.put("isRepeat", false);
                } else {
                    ret.put("isRepeat", true);
                    ret.put("names", repeatNames.toString());
                }
                if (StringUtils.isBlank(mobileNulls.toString())) {
                    ret.put("isMobileNull", false);
                } else {
                    ret.put("isMobileNull", true);
                    ret.put("mobileNulls", mobileNulls.toString());
                }
                if (StringUtils.isBlank(mobileErrors.toString())) {
                    ret.put("isMobileError", false);
                } else {
                    ret.put("isMobileError", true);
                    ret.put("mobileErrors", mobileErrors.toString());
                }
                if (StringUtils.isBlank(repeatMobiles.toString())) {
                    ret.put("isMobileRepeat", false);
                } else {
                    ret.put("isMobileRepeat", true);
                    ret.put("mobiles", repeatMobiles.toString());
                }
                return Y9Result.success(ret, "上传组织机构XLS成功");
            } else {
                LOGGER.info("################读取XLS文件数据失败！！！！！！！！！##########################");
                ret.put("success", false);
                return Y9Result.failure("读取XLS文件数据失败！");
            }
        } catch (Exception e) {
            LOGGER.warn("导入XLS组织架构发生异常", e);
            return Y9Result.failure("上传失败:" + e.getMessage());
        }
    }

    private String reverseSplit(String path) {
        if (!path.contains(SPLITTER)) {
            return path;
        }
        String[] oldString = path.split(SPLITTER);
        StringBuilder strBuffer = new StringBuilder();
        for (int length = oldString.length; length > 0; length--) {
            strBuffer.append(oldString[length - 1]);
            strBuffer.append(SPLITTER);
        }
        String newString = strBuffer.toString();
        return newString.substring(0, newString.lastIndexOf(SPLITTER));
    }

    /**
     * 获取组织架构数据
     *
     * @param organizationId
     * @return
     */
    private Map<String, Object> xlsOrgTreeData(String organizationId) {
        Map<String, Object> map = new HashMap<>();

        List<Y9Organization> organizationList = new ArrayList<>();
        Y9Organization y9Organization = y9OrganizationService.getById(organizationId);
        organizationList.add(y9Organization);

        List<ObjectSheet> departmentList = new ArrayList<>();
        executeDepartment(y9Organization.getId(), departmentList);
        List<ObjectSheet> groupList = executeGroup(y9GroupService.listAll());
        List<ObjectSheet> positionList = executePosition(y9PositionService.listAll());

        map.put("organizationList", organizationList);
        map.put("departmentList", departmentList);
        map.put("groupList", groupList);
        map.put("positionList", positionList);

        List<String> listSheetNames = new ArrayList<>();
        listSheetNames.add("组织机构");
        listSheetNames.add("部门");
        listSheetNames.add("人员");
        listSheetNames.add("用户组");
        listSheetNames.add("岗位");
        listSheetNames.add("角色");

        map.put("sheetNames", listSheetNames);
        map.put("listSheetNames", listSheetNames);
        return map;
    }

    /**
     * 导出人员信息到Excel
     *
     * @param orgBaseId
     * @return
     */
    private Map<String, Object> xlsPersonData(String orgBaseId) {
        Map<String, Object> map = new HashMap<>();

        List<Y9Person> persons = compositeOrgBaseService.listAllPersonsRecursionDownward(orgBaseId);
        List<PersonInformation> personList = new ArrayList<>();
        for (Y9Person person : persons) {
            PersonInformation personInformation = new PersonInformation();
            String fullPath = person.getDn().replace("cn=", "").replace(",ou=", SPLITTER).replace(",o=", SPLITTER);
            String path = fullPath.substring(0, fullPath.lastIndexOf(SPLITTER));
            if (path.contains(SPLITTER) && fullPath.lastIndexOf(SPLITTER) == fullPath.lastIndexOf(SPLITTER)) {
                path = path.substring(fullPath.indexOf(SPLITTER) + 1);
                personInformation.setFullPath(reverseSplit(path));
            } else if (!path.contains(SPLITTER)) {
                personInformation.setFullPath(null);
            } else {
                path = path.substring(fullPath.indexOf(SPLITTER) + 1, fullPath.lastIndexOf(SPLITTER));
                personInformation.setFullPath(reverseSplit(path));
            }
            personInformation.setName(fullPath.substring(0, fullPath.indexOf(SPLITTER)));
            personInformation.setEmail(person.getEmail());
            personInformation.setLoginName(person.getLoginName());
            personInformation.setMobile(person.getMobile());
            personInformation.setSex(SexEnum.FEMALE.equals(person.getSex()) ? "女" : "男");
            List<Y9Job> y9JobList = y9JobService.findByPersonId(person.getId());
            personInformation.setJobs(y9JobList.stream().map(Y9Job::getName).collect(Collectors.joining(SPLITTER)));
            personList.add(personInformation);
        }
        map.put("personList", personList);
        return map;
    }
}
