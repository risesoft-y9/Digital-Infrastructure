package net.risesoft.dataio.role;

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
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.permission.Y9Authorization;
import net.risesoft.entity.relation.Y9OrgBasesToRoles;
import net.risesoft.enums.ResourceTypeEnum;
import net.risesoft.enums.Y9RoleTypeEnum;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.authorization.Y9AuthorizationService;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.relation.Y9OrgBasesToRolesService;
import net.risesoft.util.StringUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;
import net.risesoft.y9public.entity.resource.Y9System;
import net.risesoft.y9public.entity.role.Y9Role;
import net.risesoft.y9public.service.resource.CompositeResourceService;
import net.risesoft.y9public.service.resource.Y9AppService;
import net.risesoft.y9public.service.resource.Y9SystemService;
import net.risesoft.y9public.service.role.Y9RoleService;

@Service
@Slf4j
@RequiredArgsConstructor
public class Y9RoleXmlDataHandlerImpl implements Y9RoleDataHandler {

    private final Y9OrgBasesToRolesService y9OrgBasesToRolesService;
    private final Y9AuthorizationService y9AuthorizationService;
    private final Y9RoleService y9RoleService;
    private final Y9AppService y9AppService;
    private final Y9SystemService y9SystemService;
    private final CompositeOrgBaseService compositeOrgBaseService;
    private final CompositeResourceService compositeResourceService;
    private final Y9Properties y9Config;

    private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private final List<Y9Result<Object>> y9Results = new ArrayList<>();
    private String createDateTime = "";

    public Element buildChildElement(Element resources, String roleId) {
        List<Y9Role> roleList = y9RoleService.listByParentId(roleId);
        if (roleList != null && !roleList.isEmpty()) {
            for (Y9Role role : roleList) {
                Element element = null;
                element = resources.addElement("roleNode");

                element.addAttribute("id", role.getId());
                createDateTime = fmt.format(role.getCreateTime() == null ? new Date() : role.getCreateTime());
                element.addElement("name").addText(role.getName() != null ? role.getName() : "");
                element.addElement("description").addText(role.getDescription() != null ? role.getDescription() : "");
                element.addElement("customId").addText(role.getCustomId() != null ? role.getCustomId() : "");
                element.addElement("createTime").addText(createDateTime);
                element.addElement("dn").addText(role.getDn() != null ? role.getDn() : "");
                element.addElement("type").addText(role.getType() != null ? role.getType() : "");
                element.addElement("tabIndex").addText(role.getTabIndex() != null ? role.getTabIndex() + "" : "");
                element.addElement("properties").addText(role.getProperties() != null ? role.getProperties() : "");
                element.addElement("parentId").addText(role.getParentId() != null ? role.getParentId() : "");
                element.addElement("appId").addText(role.getAppId() != null ? role.getAppId() : "");
                element.addElement("appCnName").addText(role.getAppCnName() != null ? role.getAppCnName() : "");
                element.addElement("systemName").addText(role.getSystemName() != null ? role.getSystemName() : "");
                element.addElement("systemCnName")
                    .addText(role.getSystemCnName() != null ? role.getSystemCnName() : "");
                buildChildElement(element, role.getId());
            }
        }
        return resources;
    }

    public Element buildIncludeElement(Element twoElement, String roleId) {
        List<Y9OrgBasesToRoles> roleMappingList = y9OrgBasesToRolesService.listByRoleId(roleId);
        for (Y9OrgBasesToRoles acRoleNodeMapping : roleMappingList) {
            Element roleElement = twoElement.addElement("RoleNodeMapping");
            roleElement.addAttribute("orgId", acRoleNodeMapping.getOrgId());
            roleElement.addAttribute("roleId", acRoleNodeMapping.getRoleId());
            roleElement.addAttribute("orgOrder",
                acRoleNodeMapping.getOrgOrder() != null ? acRoleNodeMapping.getOrgOrder() + "" : "");
        }
        List<Y9Authorization> permissionList = y9AuthorizationService.listByPrincipalId(roleId);
        for (Y9Authorization permission : permissionList) {
            Element roleElement = twoElement.addElement("RolePermission");
            roleElement.addAttribute("id", permission.getId());
            roleElement.addAttribute("resourceId", permission.getResourceId());
            roleElement.addAttribute("roleId", permission.getPrincipalId());
            createDateTime = fmt.format(permission.getCreateTime() == null ? new Date() : permission.getCreateTime());
            roleElement.addAttribute("createDateTime", createDateTime);
            roleElement.addAttribute("authority",
                permission.getAuthority() != null ? permission.getAuthority().toString() : "");
            roleElement.addAttribute("tenantId", permission.getTenantId() != null ? permission.getTenantId() : "");
            roleElement.addAttribute("authorizer",
                permission.getAuthorizer() != null ? permission.getAuthorizer() : "");
        }
        return twoElement;
    }

    public Element buildSubElement(Element resources, String roleId) {
        Y9App y9App = y9AppService.getById(roleId);
        Y9System y9System = y9SystemService.getById(y9App.getSystemId());
        Element oneElement = null;
        if (null != y9System) {
            oneElement = resources.addElement("system");
            oneElement.addAttribute("id", y9System.getId());
            createDateTime = fmt.format(y9System.getCreateTime() == null ? new Date() : y9System.getCreateTime());
            oneElement.addElement("name").addText(y9System.getName() != null ? y9System.getName() : "");
            oneElement.addElement("description")
                .addText(y9System.getDescription() != null ? y9System.getDescription() : "");
            oneElement.addElement("customId").addText("");
            oneElement.addElement("createTime").addText(createDateTime);
            oneElement.addElement("dn").addText("");
            oneElement.addElement("type").addText("");
            oneElement.addElement("tabIndex")
                .addText(y9System.getTabIndex() != null ? y9System.getTabIndex() + "" : "");
            oneElement.addElement("properties").addText("");
            oneElement.addElement("parentId").addText("");
            oneElement.addElement("systemName").addText(y9System.getName());
            oneElement.addElement("systemCnName").addText(y9System.getCnName());
            if (y9App != null) {
                if (y9App.getParentId() == null || "".equals(y9App.getParentId())) {
                    Element twoElement = oneElement.addElement("app");
                    twoElement.addAttribute("id", y9App.getId());
                    createDateTime = fmt.format(y9App.getCreateTime() == null ? new Date() : y9App.getCreateTime());
                    twoElement.addElement("name").addText(y9App.getName() != null ? y9App.getName() : "");
                    twoElement.addElement("description")
                        .addText(y9App.getDescription() != null ? y9App.getDescription() : "");
                    twoElement.addElement("customId").addText(y9App.getCustomId() != null ? y9App.getCustomId() : "");
                    twoElement.addElement("createTime").addText(createDateTime);
                    twoElement.addElement("dn").addText("");
                    twoElement.addElement("type").addText(y9App.getType() != null ? y9App.getType().toString() : "");
                    twoElement.addElement("tabIndex")
                        .addText(y9App.getTabIndex() != null ? y9App.getTabIndex() + "" : "");
                    twoElement.addElement("properties").addText("");
                    twoElement.addElement("parentId").addText(y9App.getSystemId() != null ? y9App.getSystemId() : "");
                    Y9System system = y9SystemService.getById(y9App.getSystemId());
                    twoElement.addElement("systemName").addText(system != null ? system.getName() : "");
                    twoElement.addElement("systemCnName").addText(system != null ? system.getCnName() : "");

                    buildChildElement(twoElement, roleId);
                }
            }
        }
        return resources;
    }

    void checkData(String id, Element element) {
        Y9System y9System = y9SystemService.getById(id);
        if (null == y9System && StringUtils.isNotBlank(y9System.getId())) {
            String systemName = element.elementText("name");
            String systemCnName = element.elementText("systemCnName");
            String tabIndex = element.elementText("tabIndex");
            y9System = new Y9System();
            y9System.setId(id);
            y9System.setContextPath(systemName);
            y9System.setName(systemName);
            y9System.setCnName(systemCnName);
            y9System.setEnabled(true);
            y9System.setTabIndex(Integer.valueOf(tabIndex));
            y9System = y9SystemService.saveOrUpdate(y9System);
        }

        List<Element> appNodes = element.elements("app");
        String appId = appNodes.get(0).attributeValue("id");
        Y9App y9App = y9AppService.getById(appId);
        if (null == y9App && StringUtils.isNotBlank(y9App.getId())) {
            String name = appNodes.get(0).elementText("");
            String parentId = appNodes.get(0).elementText("parentId");
            y9App = new Y9App();
            y9App.setId(appId);
            y9App.setSystemId(parentId);
            y9App.setName(name);
            y9App.setAliasName(name);
            y9App.setEnabled(true);
            y9App.setHidden(false);
            y9App.setUrl(y9Config.getCommon().getOrgBaseUrl());
            y9App.setResourceType(ResourceTypeEnum.APP.getValue());
            y9App.setInherit(false);
            y9App.setChecked(false);
            y9App.setShowNumber(false);
            y9App = y9AppService.saveOrUpdate(y9App);
        }

        List<Element> roleNodes = appNodes.get(0).elements("roleNode");
        if (null != roleNodes && !roleNodes.isEmpty()) {
            for (Element e : roleNodes) {
                recursiveRoleNode(e);
            }
        }

    }

    @Override
    public void doExport(String resourceId, OutputStream outputStream) {
        Document document = DocumentHelper.createDocument();
        document.setXMLEncoding("utf-8");
        Element resources = document.addElement("roles");
        resources.addAttribute("y9", "true");
        document.setRootElement(buildSubElement(resources, resourceId));
        String xmlString = document.asXML();
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
    public void doImport(InputStream inputStream, String rootRoleId) {
        y9Results.clear();
        DocumentFactory factory = DocumentFactory.getInstance();
        SAXReader saxReader = new SAXReader(factory);
        Document document = null;
        try {
            saxReader.setEncoding("UTF-8");
            document = saxReader.read(inputStream);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            Y9Result<Object> y9Result = Y9Result.failure(getPrintStackTrace(e));
            y9Results.add(y9Result);
        }

        Element root = document.getRootElement();
        List<Element> list = root.elements("system");
        for (Element e : list) {
            String uid = e.attributeValue("id");
            checkData(uid, e);
        }
    }

    private String getPrintStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        e.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
    }

    void recursiveRoleNode(Element element) {
        List<Element> childNodes;
        String id = element.attributeValue("id");
        String name = element.elementText("name");
        String description = element.elementText("description");
        String customId = element.elementText("customId");
        String dn = element.elementText("dn");
        String type = element.elementText("type");
        String tabIndex = element.elementText("tabIndex");
        String properties = element.elementText("properties");
        String parentId = element.elementText("parentId");
        String appId = element.elementText("appId");
        String appCnName = element.elementText("appCnName");
        String systemName = element.elementText("systemName");
        String systemCnName = element.elementText("systemCnName");

        Y9Role roleNode = new Y9Role();
        roleNode.setId(id);
        roleNode.setDescription(description);
        roleNode.setCustomId(customId);
        roleNode.setProperties(properties);
        roleNode.setTabIndex(Integer.valueOf(tabIndex));
        roleNode.setParentId(parentId);
        roleNode.setDn(dn);
        roleNode.setName(name);
        roleNode.setType(type);
        roleNode.setAppId(appId);
        roleNode.setAppCnName(appCnName);
        roleNode.setSystemName(systemName);
        roleNode.setSystemCnName(systemCnName);

        Optional<Y9Role> y9RoleOptional = y9RoleService.findById(id);
        if (y9RoleOptional.isPresent()) {
            Y9Role role = y9RoleOptional.get();
            Y9BeanUtil.copyProperties(roleNode, role);
            y9RoleService.saveOrUpdate(role);
        } else {
            y9RoleService.saveOrUpdate(roleNode);
        }

        if (type.equals(Y9RoleTypeEnum.FOLDER.getValue())) {
            childNodes = element.elements("roleNode");
            for (Element e : childNodes) {
                recursiveRoleNode(e);
            }
        }
    }

    @SuppressWarnings("unused")
    private void roleWith(Element currentNode) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        tenantId = StringUtils.isNotBlank(tenantId) ? tenantId : "当前导入角色信息的操作的ThreadLocalHolder.getTenantId()为空";
        List<Element> includeList = currentNode.elements("RoleNodeMapping");
        for (Element includeElement : includeList) {
            Y9OrgBasesToRoles roleNodeMapping = new Y9OrgBasesToRoles();
            String orgOrder = includeElement.attributeValue("orgOrder");
            String roleId = includeElement.attributeValue("roleId");
            String orgId = includeElement.attributeValue("orgId");

            Y9OrgBase y9OrgBase = compositeOrgBaseService.getOrgUnit(orgId);
            if (null == y9OrgBase) {
                continue;
            }

            roleNodeMapping.setOrgId(orgId);
            roleNodeMapping.setRoleId(roleId);
            roleNodeMapping.setOrgOrder(Integer.valueOf(orgOrder));

            String orgUnitParentId = compositeOrgBaseService.findOrgUnitParent(orgId).map(Y9OrgBase::getId).orElse("");
            roleNodeMapping.setParentId(orgUnitParentId);

            y9OrgBasesToRolesService.save(roleNodeMapping);
        }
        List<Element> permissionList = currentNode.elements("RolePermission");
        for (Element permissionElement : permissionList) {
            Y9Authorization permission = new Y9Authorization();
            String id = permissionElement.attributeValue("id");
            String resourceId = permissionElement.attributeValue("resourceId");
            String roleId = permissionElement.attributeValue("roleId");
            String authority = permissionElement.attributeValue("authority");
            String authorizer = StringUtils.isNotBlank(permissionElement.attributeValue("authorizer"))
                ? permissionElement.attributeValue("authorizer") : "导入的角色授权信息没有授权人节点或者授权人为空";

            Y9ResourceBase acResource = compositeResourceService.findById(resourceId);
            if (null == acResource) {
                continue;
            }
            permission.setId(id);
            // permission.setInherit(inherit == null ? Boolean.FALSE : Boolean.valueOf(inherit));
            permission.setAuthority(Integer.valueOf(authority));
            permission.setResourceId(resourceId);
            permission.setPrincipalId(roleId);
            permission.setTenantId(tenantId);
            permission.setAuthorizer(authorizer);

            y9AuthorizationService.saveOrUpdate(permission);

        }
    }
}
