package net.risesoft.dataio.org;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.type.TypeFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9DepartmentProp;
import net.risesoft.entity.Y9Group;
import net.risesoft.entity.Y9Organization;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9PersonExt;
import net.risesoft.entity.Y9Position;
import net.risesoft.entity.relation.Y9PersonsToGroups;
import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.entity.relation.Y9PositionsToGroups;
import net.risesoft.enums.IdentityEnum;
import net.risesoft.enums.OrgTypeEnum;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9DepartmentPropService;
import net.risesoft.service.org.Y9DepartmentService;
import net.risesoft.service.org.Y9GroupService;
import net.risesoft.service.org.Y9OrganizationService;
import net.risesoft.service.org.Y9PersonExtService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.service.relation.Y9PersonsToGroupsService;
import net.risesoft.service.relation.Y9PersonsToPositionsService;
import net.risesoft.service.relation.Y9PositionsToGroupsService;
import net.risesoft.util.StringUtil;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.base64.Y9Base64Util;

@Service(value = "y9OrgTreeXmlDataHandler")
@Slf4j
@RequiredArgsConstructor
public class Y9OrgTreeXmlDataHandlerImpl implements Y9OrgTreeDataHandler {

    private final Y9OrganizationService y9OrganizationService;
    private final Y9DepartmentService y9DepartmentService;
    private final Y9DepartmentPropService y9DepartmentPropService;
    private final Y9GroupService y9GroupService;
    private final Y9PersonsToGroupsService y9PersonsToGroupsService;
    private final Y9PositionsToGroupsService y9PositionsToGroupsService;
    private final Y9PersonService y9PersonService;
    private final Y9PersonsToPositionsService y9PersonsToPositionsService;
    private final Y9PositionService y9PositionService;
    private final Y9PersonExtService y9PersonExtService;
    private final CompositeOrgBaseService compositeOrgBaseService;
    private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private final SimpleDateFormat fmt2 = new SimpleDateFormat("yyyy-MM-dd");
    String errorMsg = "";
    private String createTime = "";
    private String updateTime = "";

    /**
     * 构造子节点信息
     */
    private Element buildDeptElement(Element rootElement, Y9Department orgdepartment) {
        Element orgDepartment = rootElement.addElement(OrgTypeEnum.DEPARTMENT.getEnName());
        orgDepartment.addAttribute("uid", orgdepartment.getId());
        orgDepartment.addAttribute("name", orgdepartment.getName());
        orgDepartment.addElement("UID").addText(orgdepartment.getId());
        createTime = fmt.format(orgdepartment.getCreateTime());
        updateTime = fmt.format(orgdepartment.getUpdateTime() == null ? new Date() : orgdepartment.getUpdateTime());
        orgDepartment.addElement("createTime").addText(createTime);
        orgDepartment.addElement("updateTime").addText(updateTime);
        orgDepartment.addElement("description").addText(orgdepartment.getDescription() == null ? "" : orgdepartment.getDescription());
        orgDepartment.addElement("customId").addText(orgdepartment.getCustomId() == null ? "" : orgdepartment.getCustomId());
        orgDepartment.addElement("disabled").addText(Boolean.toString(orgdepartment.getDisabled()));
        orgDepartment.addElement("dn").addText(orgdepartment.getDn() == null ? "" : orgdepartment.getDn());
        orgDepartment.addElement("orgType").addText(orgdepartment.getOrgType() == null ? "" : orgdepartment.getOrgType());
        orgDepartment.addElement("tabIndex").addText(orgdepartment.getTabIndex() == null ? "" : orgdepartment.getTabIndex() + "");

        orgDepartment.addElement("aliasName").addText(orgdepartment.getAliasName() == null ? "" : orgdepartment.getAliasName());
        orgDepartment.addElement("deptAddress").addText(orgdepartment.getDeptAddress() == null ? "" : orgdepartment.getDeptAddress());
        orgDepartment.addElement("deptFax").addText(orgdepartment.getDeptFax() == null ? "" : orgdepartment.getDeptFax());
        orgDepartment.addElement("deptGivenName").addText(orgdepartment.getDeptGivenName() == null ? "" : orgdepartment.getDeptGivenName());
        orgDepartment.addElement("deptOffice").addText(orgdepartment.getDeptOffice() == null ? "" : orgdepartment.getDeptOffice());
        orgDepartment.addElement("deptPhone").addText(orgdepartment.getDeptPhone() == null ? "" : orgdepartment.getDeptPhone());
        orgDepartment.addElement("deptType").addText(orgdepartment.getDeptType() == null ? "" : orgdepartment.getDeptType());
        orgDepartment.addElement("divisionCode").addText(orgdepartment.getDivisionCode() == null ? "" : orgdepartment.getDivisionCode());
        orgDepartment.addElement("enName").addText(orgdepartment.getEnName() == null ? "" : orgdepartment.getEnName());
        orgDepartment.addElement("establishDate").addText(orgdepartment.getEstablishDate() == null ? "" : fmt2.format(orgdepartment.getEstablishDate()));
        orgDepartment.addElement("gradeCode").addText(orgdepartment.getGradeCode() == null ? "" : orgdepartment.getGradeCode());
        orgDepartment.addElement("tenantId").addText(orgdepartment.getTenantId() == null ? "" : orgdepartment.getTenantId());
        orgDepartment.addElement("version").addText(orgdepartment.getVersion() == null ? "" : orgdepartment.getVersion() + "");
        orgDepartment.addElement("zipCode").addText(orgdepartment.getZipCode() == null ? "" : orgdepartment.getZipCode());
        orgDepartment.addElement("parentId").addText(orgdepartment.getParentId() == null ? "" : orgdepartment.getParentId());
        orgDepartment.addElement("deptTypeName").addText(orgdepartment.getDeptTypeName() == null ? "" : orgdepartment.getDeptTypeName());
        orgDepartment.addElement("gradeCodeName").addText(orgdepartment.getGradeCodeName() == null ? "" : orgdepartment.getGradeCodeName());
        orgDepartment.addElement("bureau").addText(Boolean.toString(orgdepartment.getBureau()));

        Element pElements = orgDepartment.addElement("properties");
        String properties = orgdepartment.getProperties();
        if (StringUtils.isNotBlank(properties)) {
            buildPropertyElement(properties, pElements);
        }
        return orgDepartment;
    }

    /**
     * Element中include节点构造
     */
    private Element buildIncludeElement(Element rootElement, Element element, String orgBaseId) {
        /**
         * Element中include节点构造
         */
        Element include = element.addElement("include");
        List<Y9Department> departmentList = y9DepartmentService.listByParentId(orgBaseId);
        for (Y9Department department : departmentList) {
            include.addElement(OrgTypeEnum.DEPARTMENT.getEnName()).addText(department.getId());
        }

        /**
         * Element部门配置信息构造
         */
        List<Y9DepartmentProp> propList = y9DepartmentPropService.listByDeptId(orgBaseId);
        for (Y9DepartmentProp prop : propList) {
            include.addElement("DepartmentProp").addText(prop.getId());
            Element propElement = rootElement.addElement("DepartmentProp");
            propElement.addAttribute("uid", prop.getId());
            propElement.addElement("UID").addText(prop.getId());
            propElement.addElement("deptId").addText(prop.getDeptId() == null ? "" : prop.getDeptId());
            propElement.addElement("orgBaseId").addText(prop.getOrgBaseId() == null ? "" : prop.getOrgBaseId());
            propElement.addElement("category").addText(Integer.toString(prop.getCategory()));
            propElement.addElement("tabIndex").addText(prop.getTabIndex() == null ? "" : prop.getTabIndex() + "");
        }

        /**
         * Element用户组或岗位组构造
         */
        List<Y9Group> y9GroupList = y9GroupService.listByParentId(orgBaseId);
        for (Y9Group y9Group : y9GroupList) {
            include.addElement(OrgTypeEnum.GROUP.getEnName()).addText(y9Group.getId());
            Element groupElement = rootElement.addElement(OrgTypeEnum.GROUP.getEnName());
            groupElement.addAttribute("uid", y9Group.getId());
            groupElement.addAttribute("name", y9Group.getName() == null ? "" : y9Group.getName());
            groupElement.addElement("UID").addText(y9Group.getId());
            createTime = fmt.format(y9Group.getCreateTime() == null ? new Date() : y9Group.getCreateTime());
            updateTime = fmt.format(y9Group.getUpdateTime() == null ? new Date() : y9Group.getUpdateTime());
            groupElement.addElement("createTime").addText(createTime);
            groupElement.addElement("updateTime").addText(updateTime);
            groupElement.addElement("description").addText(y9Group.getDescription() == null ? "" : y9Group.getDescription());
            groupElement.addElement("customId").addText(y9Group.getCustomId() == null ? "" : y9Group.getCustomId());
            groupElement.addElement("disabled").addText(Boolean.toString(y9Group.getDisabled()));
            groupElement.addElement("dn").addText(y9Group.getDn() == null ? "" : y9Group.getDn());
            groupElement.addElement("orgType").addText(y9Group.getOrgType() == null ? "" : y9Group.getOrgType());
            groupElement.addElement("type").addText(y9Group.getType() == null ? "" : y9Group.getType());
            groupElement.addElement("tabIndex").addText(y9Group.getTabIndex() == null ? "" : y9Group.getTabIndex() + "");

            groupElement.addElement("parentId").addText(y9Group.getParentId() == null ? "" : y9Group.getParentId());

            Element pElements = groupElement.addElement("properties");
            String properties = y9Group.getProperties();
            if (StringUtils.isNotBlank(properties)) {
                buildPropertyElement(properties, pElements);
            }

            Element groupInclude = groupElement.addElement("include");
            List<Y9PersonsToGroups> orgPersonsGroupsList = y9PersonsToGroupsService.listByGroupId(y9Group.getId());
            for (Y9PersonsToGroups y9PersonsToGroups : orgPersonsGroupsList) {
                groupInclude.addElement(OrgTypeEnum.PERSON.getEnName()).addText(y9PersonsToGroups.getPersonId());
            }

            List<Y9PositionsToGroups> y9PositionsToGroupsList = y9PositionsToGroupsService.listByGroupId(y9Group.getId());
            for (Y9PositionsToGroups positionsToGroup : y9PositionsToGroupsList) {
                groupInclude.addElement(OrgTypeEnum.POSITION.getEnName()).addText(positionsToGroup.getPositionId());
            }
        }
        /**
         * Element岗位构造
         */
        List<Y9Position> y9PositionList = y9PositionService.listByParentId(orgBaseId);
        for (Y9Position y9Position : y9PositionList) {
            include.addElement(OrgTypeEnum.POSITION.getEnName()).addText(y9Position.getId());
            Element positionElement = rootElement.addElement(OrgTypeEnum.POSITION.getEnName());
            positionElement.addAttribute("uid", y9Position.getId());
            positionElement.addAttribute("name", y9Position.getName() == null ? "" : y9Position.getName());
            positionElement.addElement("UID").addText(y9Position.getId());
            createTime = fmt.format(y9Position.getCreateTime() == null ? new Date() : y9Position.getCreateTime());
            updateTime = fmt.format(y9Position.getUpdateTime() == null ? new Date() : y9Position.getUpdateTime());
            positionElement.addElement("createTime").addText(createTime);
            positionElement.addElement("updateTime").addText(updateTime);
            positionElement.addElement("description").addText(y9Position.getDescription() == null ? "" : y9Position.getDescription());
            positionElement.addElement("customId").addText(y9Position.getCustomId() == null ? "" : y9Position.getCustomId());
            positionElement.addElement("disabled").addText(Boolean.toString(y9Position.getDisabled()));
            positionElement.addElement("dn").addText(y9Position.getDn() == null ? "" : y9Position.getDn());
            positionElement.addElement("orgType").addText(y9Position.getOrgType() == null ? "" : y9Position.getOrgType());
            positionElement.addElement("tabIndex").addText(y9Position.getTabIndex() == null ? "" : y9Position.getTabIndex() + "");

            positionElement.addElement("duty").addText(y9Position.getDuty() == null ? "" : y9Position.getDuty());
            positionElement.addElement("dutyLevel").addText(y9Position.getDutyLevel() == null ? "" : y9Position.getDutyLevel() + "");
            positionElement.addElement("dutyLevelName").addText(y9Position.getDutyLevelName() == null ? "" : y9Position.getDutyLevelName());
            positionElement.addElement("dutyType").addText(y9Position.getDutyType() == null ? "" : y9Position.getDutyType());
            positionElement.addElement("parentId").addText(y9Position.getParentId() == null ? "" : y9Position.getParentId());

            Element pElements = positionElement.addElement("properties");
            String properties = y9Position.getProperties();
            if (StringUtils.isNotBlank(properties)) {
                buildPropertyElement(properties, pElements);
            }

            Element positionInclude = positionElement.addElement("include");
            List<Y9PersonsToPositions> orgPositionsPersonsList = y9PersonsToPositionsService.listByPositionId(y9Position.getId());
            for (Y9PersonsToPositions y9PersonsToPositions : orgPositionsPersonsList) {
                positionInclude.addElement(OrgTypeEnum.PERSON.getEnName()).addText(y9PersonsToPositions.getPersonId());
            }
        }
        /**
         * Element人员构造
         */
        List<Y9Person> orgPersonList = y9PersonService.listByParentId(orgBaseId);
        for (Y9Person person : orgPersonList) {
            include.addElement(OrgTypeEnum.PERSON.getEnName()).addText(person.getId());
            Element personElement = rootElement.addElement(OrgTypeEnum.PERSON.getEnName());
            personElement.addAttribute("uid", person.getId());
            personElement.addAttribute("name", person.getName() == null ? "" : person.getName());
            personElement.addElement("UID").addText(person.getId());
            createTime = fmt.format(person.getCreateTime() == null ? new Date() : person.getCreateTime());
            updateTime = fmt.format(person.getUpdateTime() == null ? new Date() : person.getUpdateTime());
            personElement.addElement("createTime").addText(createTime);
            personElement.addElement("updateTime").addText(updateTime);
            personElement.addElement("description").addText(person.getDescription() == null ? "" : person.getDescription());
            personElement.addElement("customId").addText(person.getCustomId() == null ? "" : person.getCustomId());
            personElement.addElement("disabled").addText(Boolean.toString(person.getDisabled()));
            personElement.addElement("dn").addText(person.getDn() == null ? "" : person.getDn());
            personElement.addElement("orgType").addText(person.getOrgType() == null ? "" : person.getOrgType());
            personElement.addElement("tabIndex").addText(person.getTabIndex() == null ? "" : person.getTabIndex() + "");
            personElement.addElement("avator").addText(person.getAvator() == null ? "" : person.getAvator());
            personElement.addElement("caid").addText(person.getCaid() == null ? "" : person.getCaid());
            personElement.addElement("email").addText(person.getEmail() == null ? "" : person.getEmail());
            personElement.addElement("loginName").addText(person.getLoginName() == null ? "" : person.getLoginName());
            personElement.addElement("mobile").addText(person.getMobile() == null ? "" : person.getMobile());
            personElement.addElement("officeAddress").addText(person.getOfficeAddress() == null ? "" : person.getOfficeAddress());
            personElement.addElement("officeFax").addText(person.getOfficeFax() == null ? "" : person.getOfficeFax());
            personElement.addElement("officePhone").addText(person.getOfficePhone() == null ? "" : person.getOfficePhone());
            personElement.addElement("official").addText(person.getOfficial() == null ? "" : person.getOfficial() + "");
            personElement.addElement("officialType").addText(person.getOfficialType() == null ? "" : person.getOfficialType());
            personElement.addElement("password").addText(person.getPassword() == null ? "" : person.getPassword());
            personElement.addElement("sex").addText(person.getSex() == null ? "" : person.getSex() + "");
            personElement.addElement("tenantId").addText(person.getTenantId() == null ? "" : person.getTenantId());
            personElement.addElement("version").addText(person.getVersion() == null ? "" : person.getVersion() + "");
            personElement.addElement("parentId").addText(person.getParentId() == null ? "" : person.getParentId());
            personElement.addElement("guidPath").addText(person.getGuidPath() == null ? "" : person.getParentId());

            personElement.addElement("original").addText(person.getOriginal() == null ? "true" : person.getOriginal() + "");
            personElement.addElement("originalId").addText(person.getOriginalId() == null ? "" : person.getOriginalId());

            Optional<Y9PersonExt> optionalY9PersonExt = y9PersonExtService.findByPersonId(person.getId());
            if (optionalY9PersonExt.isPresent()) {
                Y9PersonExt ext = optionalY9PersonExt.get();
                personElement.addElement("city").addText(ext.getCity() == null ? "" : ext.getCity());
                personElement.addElement("country").addText(ext.getCountry() == null ? "" : ext.getCountry());
                personElement.addElement("education").addText(ext.getEducation() == null ? "" : ext.getEducation());
                personElement.addElement("homeAddress").addText(ext.getHomeAddress() == null ? "" : ext.getHomeAddress());
                personElement.addElement("homePhone").addText(ext.getHomePhone() == null ? "" : ext.getHomePhone());
                personElement.addElement("idNum").addText(ext.getIdNum() == null ? "" : ext.getIdNum());
                personElement.addElement("idType").addText(ext.getIdType() == null ? "" : ext.getIdType());
                personElement.addElement("maritalStatus").addText(ext.getMaritalStatus() == null ? "" : ext.getMaritalStatus() + "");
                personElement.addElement("policitalStatus").addText(ext.getPoliticalStatus() == null ? "" : ext.getPoliticalStatus());
                personElement.addElement("professional").addText(ext.getProfessional() == null ? "" : ext.getProfessional());
                personElement.addElement("province").addText(ext.getProvince() == null ? "" : ext.getProvince());
                personElement.addElement("sign").addText(ext.getSign() == null ? "" : new String(ext.getSign()));
                personElement.addElement("photo").addText(ext.getPhoto() == null ? "" : Y9Base64Util.encode(ext.getPhoto()));
                if (null != ext.getWorkTime()) {
                    personElement.addElement("worktime").addText(fmt2.format(ext.getWorkTime()));
                }
                if (null != ext.getBirthday()) {
                    personElement.addElement("birthday").addText(fmt2.format(ext.getBirthday()));
                }
            }
            Element pElements = personElement.addElement("properties");
            String properties = person.getProperties();
            if (StringUtils.isNotBlank(properties)) {
                buildPropertyElement(properties, pElements);
            }
        }
        return rootElement;
    }

    /**
     * 构造子节点信息
     */
    private Element buildOrgElement(Element rootElement, String orgId) {
        Y9Organization y9Organization = y9OrganizationService.getById(orgId);

        Element orgElement = rootElement.addElement(OrgTypeEnum.ORGANIZATION.getEnName());
        orgElement.addAttribute("uid", y9Organization.getId());
        orgElement.addAttribute("name", y9Organization.getName());
        orgElement.addElement("UID").addText(y9Organization.getId());
        createTime = fmt.format(y9Organization.getCreateTime() == null ? new Date() : y9Organization.getCreateTime());
        updateTime = fmt.format(y9Organization.getUpdateTime() == null ? new Date() : y9Organization.getUpdateTime());
        orgElement.addElement("createTime").addText(createTime);
        orgElement.addElement("updateTime").addText(updateTime);
        orgElement.addElement("description").addText(y9Organization.getDescription() != null ? y9Organization.getDescription() : "");
        orgElement.addElement("customId").addText(y9Organization.getCustomId() != null ? y9Organization.getCustomId() : "");
        orgElement.addElement("disabled").addText(Boolean.toString(y9Organization.getDisabled()));
        orgElement.addElement("dn").addText(y9Organization.getDn() != null ? y9Organization.getDn() : "");
        orgElement.addElement("orgType").addText(y9Organization.getOrgType() != null ? y9Organization.getOrgType() : "");
        orgElement.addElement("tabIndex").addText(y9Organization.getTabIndex() != null ? y9Organization.getTabIndex() + "" : "");

        orgElement.addElement("enName").addText(y9Organization.getEnName() != null ? y9Organization.getEnName() : "");
        orgElement.addElement("organizationCode").addText(y9Organization.getOrganizationCode() != null ? y9Organization.getOrganizationCode() : "");
        orgElement.addElement("organizationType").addText(y9Organization.getOrganizationType() != null ? y9Organization.getOrganizationType() : "");
        orgElement.addElement("tenantId").addText(y9Organization.getTenantId() != null ? y9Organization.getTenantId() : "");
        orgElement.addElement("version").addText(y9Organization.getVersion() != null ? y9Organization.getVersion() + "" : "");
        orgElement.addElement("virtual").addText(Boolean.toString(y9Organization.getVirtual()));

        Element pElements = orgElement.addElement("properties");
        String properties = y9Organization.getProperties();
        if (StringUtils.isNotBlank(properties)) {
            buildPropertyElement(properties, pElements);
        }
        return orgElement;
    }

    private void buildPropertyElement(String properties, Element pElements) {
        HashMap<String, String> map = null;
        try {
            map = Y9JsonUtil.objectMapper.readValue(properties, TypeFactory.defaultInstance().constructMapType(HashMap.class, String.class, String.class));
            for (Map.Entry<String, String> entry : map.entrySet()) {
                Element pElement = pElements.addElement("property");
                pElement.addAttribute("name", entry.getKey());
                pElement.addAttribute("value", entry.getValue());
            }
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    /**
     * 构造子节点信息
     */
    private Element buildSubElement(Element rootElement, String parentId) {
        List<Y9Department> y9DepartmentList = y9DepartmentService.listByParentId(parentId);
        for (Y9Department orgdepartment : y9DepartmentList) {
            Element orgDepartment = buildDeptElement(rootElement, orgdepartment);
            buildIncludeElement(rootElement, orgDepartment, orgdepartment.getId());
            buildSubElement(rootElement, orgdepartment.getId());
        }
        return rootElement;
    }

    @Override
    public void exportOrgTree(String orgBaseId, OutputStream outputStream) {
        String xmlString = this.xmlExport(orgBaseId);
        xmlString = StringUtil.strChangeToXml(xmlString.getBytes(StandardCharsets.UTF_8));

        try (InputStream in = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8))) {
            int len;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf, 0, 1024)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.flush();
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }

    }

    @Override
    public void exportPerson(String orgBaseId, OutputStream outputStream) {

    }

    private List<String> getPersons(Element currentNode) {
        List<String> persons = new ArrayList<>();
        Element include = currentNode.element("include");
        if (include != null) {
            List<Element> nodes = include.elements();
            if (nodes != null && !nodes.isEmpty()) {
                for (Element element : nodes) {
                    String guid = element.getText();
                    if (StringUtils.isNotBlank(guid) && OrgTypeEnum.PERSON.getEnName().equals(element.getName())) {
                        persons.add(guid);
                    }
                }
            }
        }

        return persons;
    }

    private List<String> getPositions(Element currentNode) {
        List<String> positions = new ArrayList<>();
        Element include = currentNode.element("include");
        if (include != null) {
            List<Element> nodes = include.elements();
            if (nodes != null && nodes.size() > 0) {
                for (Element element : nodes) {
                    String guid = element.getText();
                    if (StringUtils.isNotBlank(guid) && OrgTypeEnum.POSITION.getEnName().equals(element.getName())) {
                        positions.add(guid);
                    }
                }
            }
        }
        return positions;
    }

    private String getPrintStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        e.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
    }

    @Override
    public Y9Result<Object> importOrgTree(InputStream inputStream, String orgId) {

        DocumentFactory factory = DocumentFactory.getInstance();
        SAXReader saxReader = new SAXReader(factory);
        Document document = null;
        try {
            errorMsg = "";
            saxReader.setEncoding("UTF-8");
            document = saxReader.read(inputStream);

            String level = "";
            Element root = document.getRootElement();
            String uid = root.attributeValue("uid");
            String y9 = root.attributeValue("y9");
            boolean y9Version = false;
            if ("true".equals(y9)) {
                y9Version = true;
            }
            String[] uids = uid.split(",");
            for (String id : uids) {
                recursiveRun(document, level, id, y9Version);
            }
        } catch (DocumentException e) {
            LOGGER.warn(e.getMessage(), e);
            return Y9Result.failure(getPrintStackTrace(e));
        }
        return Y9Result.success("", errorMsg);
    }

    @Override
    public Y9Result<Object> importPerson(InputStream inputStream, String orgId) {
        return null;
    }

    private void recursiveRun(Document document, String level, String uid, boolean y9Version) {
        String description, customId, disabled, name, tabIndex;
        SimpleDateFormat fmt2 = new SimpleDateFormat("yyyy-MM-dd");
        List<Element> nodes;
        Element currentNode = (Element)document.selectSingleNode("/org/*[@uid=\"" + uid + "\"]");
        String nodeName = currentNode.getName();
        name = currentNode.attributeValue("name");
        description = currentNode.elementText("description");
        customId = currentNode.elementText("customId");
        if (StringUtils.isBlank(customId)) {
            customId = currentNode.elementText("customID");
        }
        disabled = currentNode.elementText("disabled");

        String properties = "";
        Element propertiesElement = currentNode.element("properties");
        if (propertiesElement != null) {
            Map<String, String> map = new HashMap<>();
            nodes = propertiesElement.elements();
            for (Element node : nodes) {
                String key = node.attributeValue("name");
                String value = node.attributeValue("value");
                map.put(key, value);
            }
            properties = Y9JsonUtil.writeValueAsString(map);
        }

        if (OrgTypeEnum.ORGANIZATION.getEnName().equals(nodeName)) {
            String enName, organizationCode, organizationType, virtual;
            Y9Organization org = null;
            try {
                enName = currentNode.elementText("enName");
                organizationCode = currentNode.elementText("organizationCode");
                organizationType = currentNode.elementText("organizationType");
                virtual = currentNode.elementText("virtual");

                Optional<Y9Organization> y9OrganizationOptional = y9OrganizationService.findById(uid);
                if (y9OrganizationOptional.isEmpty()) {
                    org = new Y9Organization();
                    org.setId(uid);
                } else {
                    org = y9OrganizationOptional.get();
                }
                org.setDescription(description == null ? "" : description);
                org.setCustomId(customId == null ? "" : customId);

                org.setName(name == null ? "" : name);
                org.setEnName(enName == null ? "" : enName);
                org.setOrganizationCode(organizationCode == null ? "" : organizationCode);
                org.setOrganizationType(organizationType == null ? "" : organizationType);
                org.setProperties(properties);
                org.setVirtual(virtual == null ? false : Boolean.parseBoolean(virtual));

                y9OrganizationService.saveOrUpdate(org);

                Element include = currentNode.element("include");
                if (include != null) {
                    nodes = include.elements();
                    if (nodes != null && nodes.size() > 0) {
                        for (Element element : nodes) {
                            String guid = element.getText();
                            if (guid != null && guid.trim().length() > 0) {
                                recursiveRun(document, level + "--", element.getText(), y9Version);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
                String message = "导入组织机构实体出错:";
                message += "\r\n组织名称:" + org.getName();
                message += "\r\n错误描述:" + getPrintStackTrace(e);
                errorMsg += message;
            }
        } else if (OrgTypeEnum.DEPARTMENT.getEnName().equals(nodeName)) {
            String aliasName, deptAddress, deptFax, deptGivenName, deptOffice, deptPhone, deptType, divisionCode, enName;
            String establishDate, gradeCode, zipCode, deptTypeName, gradeCodeName, bureau;
            Y9Department dept = null;
            try {
                aliasName = currentNode.elementText("aliasName");
                deptAddress = currentNode.elementText("deptAddress");
                deptFax = currentNode.elementText("deptFax");
                deptGivenName = currentNode.elementText("deptGivenName");
                deptOffice = currentNode.elementText("deptOffice");
                deptPhone = currentNode.elementText("deptPhone");
                deptType = currentNode.elementText("deptType");
                divisionCode = currentNode.elementText("divisionCode");
                enName = currentNode.elementText("enName");
                establishDate = currentNode.elementText("establishDate");
                gradeCode = currentNode.elementText("gradeCode");
                zipCode = currentNode.elementText("zipCode");
                deptTypeName = currentNode.elementText("deptTypeName");
                gradeCodeName = currentNode.elementText("gradeCodeName");
                bureau = currentNode.elementText("bureau");
                tabIndex = currentNode.elementText("tabIndex");

                Element parentNode = (Element)document.selectSingleNode("/org/*[include/Department=\"" + uid + "\"]");
                String pid = parentNode.attributeValue("uid");

                Optional<Y9Department> y9DepartmentOptional = y9DepartmentService.findById(uid);
                if (y9DepartmentOptional.isEmpty()) {
                    dept = new Y9Department();
                    dept.setId(uid);
                } else {
                    dept = y9DepartmentOptional.get();
                }

                dept.setParentId(pid);
                dept.setName(name == null ? "" : name);
                dept.setDescription(description == null ? "" : description);
                dept.setCustomId(customId == null ? "" : customId);

                dept.setAliasName(aliasName == null ? "" : aliasName);
                dept.setDeptAddress(deptAddress);
                dept.setDeptFax(deptFax == null ? "" : deptFax);
                dept.setDeptGivenName(deptGivenName == null ? "" : deptGivenName);
                dept.setDeptOffice(deptOffice == null ? "" : deptOffice);
                dept.setDeptPhone(deptPhone == null ? "" : deptPhone);
                dept.setDeptType(deptTypeName == null ? "" : deptType);
                dept.setDivisionCode(divisionCode == null ? "" : divisionCode);
                dept.setEnName(enName == null ? "" : enName);
                dept.setEstablishDate(StringUtils.isBlank(establishDate) ? new Date() : fmt2.parse(establishDate));
                dept.setGradeCode(gradeCodeName == null ? "" : gradeCode);
                dept.setZipCode(zipCode == null ? "" : zipCode);
                dept.setDeptTypeName(deptTypeName == null ? "" : deptTypeName);
                dept.setGradeCodeName(gradeCodeName == null ? "" : gradeCodeName);
                dept.setProperties(properties);
                dept.setTabIndex(tabIndex != null ? Integer.parseInt(tabIndex) : null);
                if (y9Version) {
                    dept.setBureau(bureau == null ? false : Boolean.valueOf(bureau));
                }
                y9DepartmentService.saveOrUpdate(dept);

                Element include = currentNode.element("include");
                if (include != null) {
                    nodes = include.elements();
                    if (nodes != null && nodes.size() > 0) {
                        for (Element element : nodes) {
                            String guid = element.getText();
                            if (guid != null && guid.trim().length() > 0) {
                                recursiveRun(document, level + "--", guid, y9Version);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
                String message = "导入部门出错:";
                message += "\r\n部门名称:" + dept.getName();
                message += "\r\n错误描述:" + getPrintStackTrace(e);
                errorMsg += message;
            }
        } else if ("DepartmentProp".equals(nodeName)) {
            String deptId, orgBaseId, category;
            Y9DepartmentProp prop = null;
            try {
                deptId = currentNode.elementText("deptId");
                orgBaseId = currentNode.elementText("orgBaseId");
                category = currentNode.elementText("category");
                tabIndex = currentNode.elementText("tabIndex");

                Optional<Y9DepartmentProp> y9DepartmentPropOptional = y9DepartmentPropService.findById(uid);
                if (prop == null) {
                    prop = new Y9DepartmentProp();
                    prop.setId(uid);
                } else {
                    prop = y9DepartmentPropOptional.get();
                }
                prop.setDeptId(deptId == null ? "" : deptId);
                prop.setOrgBaseId(orgBaseId == null ? "" : orgBaseId);
                prop.setCategory(category != null ? Integer.parseInt(category) : null);
                prop.setTabIndex(tabIndex != null ? Integer.parseInt(tabIndex) : null);
                y9DepartmentPropService.saveOrUpdate(prop);
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
                String message = "导入部门配置出错:";
                message += "\r\n部门ID:" + prop.getDeptId();
                message += "\r\norgBaseId:" + prop.getOrgBaseId();
                message += "\r\n错误描述:" + getPrintStackTrace(e);
                errorMsg += message;
            }
        } else if (OrgTypeEnum.GROUP.getEnName().equals(nodeName)) {
            Y9Group group = null;
            String type = "";
            try {
                Element parentNode = (Element)document.selectSingleNode("/org/Organization[include/Group=\"" + uid + "\"] | /org/Department[include/Group=\"" + uid + "\"]");
                tabIndex = currentNode.elementText("tabIndex");
                type = currentNode.elementText("type");

                String pid = parentNode.attributeValue("uid");

                group = y9GroupService.findById(uid).orElse(new Y9Group());
                group.setId(uid);
                group.setParentId(pid);
                group.setType(type == null ? IdentityEnum.POSITION.getName() : type);
                group.setName(name == null ? "" : name);
                group.setDescription(description == null ? "" : description);
                group.setCustomId(customId == null ? "" : customId);
                group.setProperties(properties);
                group.setTabIndex(tabIndex != null ? Integer.parseInt(tabIndex) : null);
                y9GroupService.saveOrUpdate(group);
                /**
                 * 1、保存用户组和人员关系
                 */
                List<String> persons = getPersons(currentNode);
                if (persons.size() > 0) {
                    y9PersonsToGroupsService.addPersons(uid, persons.toArray(new String[] {}));
                }
                /**
                 * 2、保存岗位组和岗位关系
                 */
                List<String> positions = getPositions(currentNode);
                if (positions.size() > 0) {
                    y9PositionsToGroupsService.saveGroupPosition(uid, persons.toArray(new String[] {}));
                }
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
                String message = "导入用户组出错:";
                message += "\r\n用户组名称:" + group.getName();
                message += "\r\n用户组ID:" + group.getId();
                message += "\r\n错误描述:" + getPrintStackTrace(e);
                errorMsg += message;
            }
        } else if (OrgTypeEnum.POSITION.getEnName().equals(nodeName)) {
            String duty, dutyLevel, dutyLevelName, dutyType, parentId;
            Y9Position position = null;
            try {
                duty = currentNode.elementText("duty");
                dutyLevel = currentNode.elementText("dutyLevel");
                dutyLevelName = currentNode.elementText("dutyLevelName");
                dutyType = currentNode.elementText("dutyType");
                parentId = currentNode.elementText("parentId");
                if (StringUtils.isBlank(parentId)) {
                    parentId = currentNode.elementText("parentID");
                }
                tabIndex = currentNode.elementText("tabIndex");

                Element parentNode = (Element)document.selectSingleNode("/org/Organization[include/Position=\"" + uid + "\"] | /org/Department[include/Position=\"" + uid + "\"]");
                String pid = parentNode.attributeValue("uid");

                Optional<Y9Position> y9PositionOptional = y9PositionService.findById(uid);
                if (y9PositionOptional.isEmpty()) {
                    position = new Y9Position();
                    position.setId(uid);
                } else {
                    position = y9PositionOptional.get();
                }
                position.setName(name == null ? "" : name);
                position.setDescription(description == null ? "" : description);
                position.setCustomId(customId == null ? "" : customId);
                position.setTabIndex(tabIndex != null ? Integer.parseInt(tabIndex) : null);

                position.setDuty(duty == null ? "" : duty);
                position.setDutyLevel(dutyLevel == null ? 0 : Integer.valueOf(dutyLevel));
                position.setDutyLevelName(dutyLevelName == null ? "" : dutyLevelName);
                position.setDutyType(dutyType == null ? "" : dutyType);
                position.setProperties(properties);
                position.setParentId(pid);

                // 岗位涉及到职位，暂时不添加
                // y9PositionService.saveOrUpdate(position);
                // y9PersonsToPositionsService.deleteByPositionId(uid);
                //
                // List<String> persons = getPersons(currentNode);
                // if (persons.size() > 0) {
                // y9PersonsToPositionsService.addPersons(uid, persons.toArray(new String[]{}));
                // }
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
                String message = "导入岗位出错:";
                message += "\r\n岗位名称:" + position.getName();
                message += "\r\n岗位ID:" + position.getId();
                message += "\r\n错误描述:" + getPrintStackTrace(e);
                errorMsg += message;
            }
        } else if (OrgTypeEnum.PERSON.getEnName().equals(nodeName)) {
            String avator, birthday, caid, city, country, education, email, homeAddress, homePhone, idNum;
            String idType, loginName, maritalStatus, mobile, officeAddress, officeFax, officePhone, official, officialType, password, photo;
            String policitalStatus, professional, province, sex, sign, worktime, originalId, original;
            Y9Person person = null;
            try {
                avator = currentNode.elementText("avator");
                birthday = currentNode.elementText("birthday");
                caid = currentNode.elementText("caid");
                city = currentNode.elementText("city");
                country = currentNode.elementText("country");
                education = currentNode.elementText("education");
                email = currentNode.elementText("email");
                homeAddress = currentNode.elementText("homeAddress");
                homePhone = currentNode.elementText("homePhone");
                idNum = currentNode.elementText("idNum");
                idType = currentNode.elementText("idType");
                loginName = currentNode.elementText("loginName");
                maritalStatus = currentNode.elementText("maritalStatus");
                mobile = currentNode.elementText("mobile");
                officeAddress = currentNode.elementText("officeAddress");
                officeFax = currentNode.elementText("officeFax");
                officePhone = currentNode.elementText("officePhone");
                official = currentNode.elementText("official");
                officialType = currentNode.elementText("officialType");
                password = currentNode.elementText("password");
                photo = currentNode.elementText("photo");
                policitalStatus = currentNode.elementText("policitalStatus");
                professional = currentNode.elementText("professional");
                province = currentNode.elementText("province");
                sex = currentNode.elementText("sex");
                sign = currentNode.elementText("sign");
                worktime = currentNode.elementText("workTime");
                tabIndex = currentNode.elementText("tabIndex");

                original = currentNode.elementText("original");
                originalId = currentNode.elementText("originalId");
                if (!y9Version) {
                    if ("0".equals(sex)) {
                        sex = "1";
                    } else {
                        sex = "0";
                    }
                }

                Element parentNode = (Element)document.selectSingleNode("/org/Organization[include/Person=\"" + uid + "\"] | /org/Department[include/Person=\"" + uid + "\"]");
                String pid = parentNode.attributeValue("uid");

                Optional<Y9Person> y9PersonOptional = y9PersonService.findById(uid);
                if (y9PersonOptional.isEmpty()) {
                    person = new Y9Person();
                    person.setId(uid);
                } else {
                    person = y9PersonOptional.get();
                }

                person.setParentId(pid);
                person.setName(name == null ? "" : name);
                person.setDescription(description == null ? "" : description);
                person.setCustomId(customId == null ? "" : customId);
                person.setDisabled(Boolean.valueOf(disabled == null ? "false" : disabled));
                person.setAvator(avator == null ? "" : avator);
                person.setCaid(caid == null ? "" : caid);
                person.setEmail(email == null ? null : email);
                person.setLoginName(loginName == null ? "" : loginName);
                person.setMobile(mobile == null ? "" : mobile);
                person.setOfficeAddress(officeAddress == null ? "" : officeAddress);
                person.setOfficeFax(officeFax == null ? "" : officeFax);
                person.setOfficePhone(officePhone == null ? "" : officePhone);
                person.setOfficial(official == null ? 0 : Integer.valueOf(official));
                person.setOfficialType(officialType == null ? "" : officialType);
                person.setPassword(password == null ? "" : password);
                person.setSex(sex == null ? 0 : Integer.valueOf(sex));
                person.setProperties(properties);
                person.setTabIndex(tabIndex != null ? Integer.parseInt(tabIndex) : null);
                if (StringUtils.isBlank(original) || "1".equals(original)) {
                    original = "true";
                } else if ("0".equals(original)) {
                    original = "false";
                }
                person.setOriginal(Boolean.valueOf(original));
                person.setOriginalId(originalId);

                Y9PersonExt ext = y9PersonExtService.findByPersonId(uid).orElse(new Y9PersonExt());

                ext.setBirthday(birthday == null ? null : fmt2.parse(birthday));
                ext.setMaritalStatus(maritalStatus == null ? 0 : Integer.valueOf(maritalStatus));
                ext.setHomeAddress(homeAddress == null ? "" : homeAddress);
                ext.setHomePhone(homePhone == null ? "" : homePhone);
                ext.setIdNum(idNum == null ? "" : idNum);
                ext.setIdType(idType == null ? "" : idType);
                ext.setCountry(country == null ? "" : country);
                ext.setEducation(education == null ? "" : education);
                ext.setCity(city == null ? "" : city);
                ext.setPoliticalStatus(policitalStatus == null ? "" : policitalStatus);
                ext.setProfessional(professional == null ? "" : professional);
                ext.setProvince(province == null ? "" : province);
                if (StringUtils.isNotBlank(photo)) {
                    ext.setPhoto(Y9Base64Util.decodeAsBytes(photo));
                }
                ext.setSign(sign == null ? "".getBytes() : sign.getBytes());
                ext.setWorkTime(worktime == null ? null : fmt2.parse(worktime));
                if (y9Version) {
                    y9PersonService.saveOrUpdate4ImpOrg(person, ext);
                } else {
                    y9PersonService.saveOrUpdate(person, ext);
                }
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
                String message = "导入人员出错:";
                message += "\r\n人员名称:" + person.getName();
                message += "\r\n人员ID:" + person.getId();
                message += "\r\n错误描述:" + getPrintStackTrace(e);
                errorMsg += message;
            }
        } else {
            String message = "y9不能识别的节点:" + nodeName;
            errorMsg += message;
        }
    }

    /**
     * 构造Document xml数据
     */
    private String xmlExport(String orgBaseId) {
        Document document = DocumentHelper.createDocument();
        document.setXMLEncoding("utf-8");
        Element rootElement = document.addElement("org");
        rootElement.addAttribute("y9", "true");
        Y9Organization y9Organization = y9OrganizationService.getById(orgBaseId);
        /**
         * 构造组织机构节点
         */
        if (y9Organization != null) {
            rootElement.addAttribute("uid", y9Organization.getId());
            Element orgElement = buildOrgElement(rootElement, orgBaseId);
            buildIncludeElement(rootElement, orgElement, orgBaseId);
            document.setRootElement(buildSubElement(rootElement, orgBaseId));
        } else {
            y9Organization = compositeOrgBaseService.getOrgUnitOrganization(orgBaseId);
            rootElement.addAttribute("uid", y9Organization.getId());

            Element orgElement = buildOrgElement(rootElement, y9Organization.getId());
            Element include = orgElement.addElement("include");
            include.addElement(OrgTypeEnum.DEPARTMENT.getEnName()).addText(orgBaseId);

            Element deptElement = buildDeptElement(rootElement, y9DepartmentService.getById(orgBaseId));
            document.setRootElement(buildIncludeElement(rootElement, deptElement, orgBaseId));
        }
        return document.asXML();
    }

}
