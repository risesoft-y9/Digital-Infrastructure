package net.risesoft.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import net.risesoft.controller.org.OrgController;
import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.model.platform.org.Department;
import net.risesoft.model.platform.org.Organization;
import net.risesoft.model.user.UserInfo;
import net.risesoft.service.org.Y9DepartmentService;
import net.risesoft.service.org.Y9OrganizationService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.y9.Y9LoginUserHolder;

@WebMvcTest(controllers = {OrgController.class})
public class OrgControllerTest {

    @Autowired
    public MockMvc mockMvc;
    public Organization organization = new Organization();
    public Department department = new Department();
    @MockBean
    private Y9DepartmentService y9DepartmentService;
    @MockBean
    private Y9OrganizationService y9OrganizationService;
    @MockBean
    private Y9PersonService y9PersonService;

    @ParameterizedTest
    @EnumSource(value = OrgTypeEnum.class, names = {"ORGANIZATION", "DEPARTMENT"})
    void getAllPersonsCount(OrgTypeEnum orgType) throws Exception {
        when(y9OrganizationService.getById(organization.getId())).thenReturn(organization);
        when(y9DepartmentService.getById(department.getId())).thenReturn(department);
        String id = organization.getId();
        if (orgType.equals(OrgTypeEnum.DEPARTMENT)) {
            id = department.getId();
        }
        mockMvc.perform(post("/api/rest/org/getAllPersonsCount").param("id", id).param("orgType", orgType.toString()))
            .andExpect(status().isOk());
        if (orgType.equals(OrgTypeEnum.ORGANIZATION)) {
            verify(y9OrganizationService, times(1)).getById(organization.getId());
        }
        if (orgType.equals(OrgTypeEnum.DEPARTMENT)) {
            verify(y9DepartmentService, times(1)).getById(department.getId());
        }
        verify(y9PersonService, times(1)).countByGuidPathLikeAndDisabledAndDeletedFalse(id);
    }

    @BeforeEach
    void setUp() {
        UserInfo userInfo = new UserInfo();
        userInfo.setGlobalManager(true);
        userInfo.setManagerLevel(ManagerLevelEnum.SYSTEM_MANAGER);
        userInfo.setName("系统管理员");
        Y9LoginUserHolder.setUserInfo(userInfo);
        Y9LoginUserHolder.setTenantId("11111111-1111-1111-1111-111111111113");

        organization.setName("组织测试");
        organization.setId("8888888888888888888");
        organization.setProperties("{\"key1\":\"value1\", \"key2\":\"value2\"}");

        department.setId("9999999999999999999");
        department.setParentId("8888888888888888888");
        department.setName("部门测试");
    }

    @Test
    void testChangeDisabled() throws Exception {
        when(y9OrganizationService.changeDisabled(organization.getId())).thenReturn(organization);
        mockMvc.perform(post("/api/rest/org/changeDisabled").param("id", organization.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value(organization.getId()))
            .andExpect(jsonPath("$.msg").value("组织禁用状态修改成功"));
        verify(y9OrganizationService, times(1)).changeDisabled(organization.getId());
    }

    @Test
    public void testGetExtendProperties() throws Exception {
        when(y9OrganizationService.getById(organization.getId())).thenReturn(organization);
        mockMvc.perform(get("/api/rest/org/getExtendProperties").param("orgId", organization.getId())
            .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    void testRemove() throws Exception {
        mockMvc
            .perform(post("/api/rest/org/remove").contentType(MediaType.APPLICATION_JSON)
                .param("orgId", organization.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testSaveOrUpdate() throws Exception {
        when(y9OrganizationService.saveOrUpdate(organization)).thenReturn(organization);
        MvcResult mvcResult = mockMvc
            .perform(post("/api/rest/org/saveOrUpdate").contentType(MediaType.APPLICATION_JSON)
                .param("id", organization.getId())
                .param("name", organization.getName()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString(Charset.defaultCharset()));
    }
}