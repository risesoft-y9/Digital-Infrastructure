package net.risesoft.controller;

import net.risesoft.controller.org.OrgController;
import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9Organization;
import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.model.user.UserInfo;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9DepartmentService;
import net.risesoft.service.org.Y9OrganizationService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.y9.Y9LoginUserHolder;
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

import java.nio.charset.Charset;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = {OrgController.class})
public class OrgControllerTest {

    @Autowired
    public MockMvc mockMvc;

    @MockBean
    private Y9DepartmentService y9DepartmentService;
    @MockBean
    private CompositeOrgBaseService compositeOrgBaseService;
    @MockBean
    private Y9OrganizationService y9OrganizationService;
    @MockBean
    private Y9PersonService y9PersonService;

    public Y9Organization y9Organization = new Y9Organization();

    public Y9Department y9Department = new Y9Department();

    @BeforeEach
    void setUp() {
        UserInfo userInfo = new UserInfo();
        userInfo.setGlobalManager(true);
        userInfo.setManagerLevel(ManagerLevelEnum.SYSTEM_MANAGER);
        userInfo.setName("系统管理员");
        Y9LoginUserHolder.setUserInfo(userInfo);
        Y9LoginUserHolder.setTenantId("11111111-1111-1111-1111-111111111113");

        y9Organization.setName("组织测试");
        y9Organization.setId("8888888888888888888");
        y9Organization.setProperties("{\"key1\":\"value1\", \"key2\":\"value2\"}");

        y9Department.setId("9999999999999999999");
        y9Department.setParentId("8888888888888888888");
        y9Department.setName("部门测试");
    }

    @Test
    void testChangeDisabled() throws Exception {
        when(y9OrganizationService.changeDisabled(y9Organization.getId())).thenReturn(y9Organization);
        mockMvc.perform(post("/api/rest/org/changeDisabled")
                        .param("id", y9Organization.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(y9Organization.getId()))
                .andExpect(jsonPath("$.msg").value("组织禁用状态修改成功"));
        verify(y9OrganizationService, times(1)).changeDisabled(y9Organization.getId());
    }

    @Test
    public void testGetExtendProperties() throws Exception {
        when(y9OrganizationService.getById(y9Organization.getId())).thenReturn(y9Organization);
        mockMvc.perform(get("/api/rest/org/getExtendProperties")
                        .param("orgId", y9Organization.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @EnumSource(value = OrgTypeEnum.class, names = {"ORGANIZATION", "DEPARTMENT"})
    void getAllPersonsCount(OrgTypeEnum orgType) throws Exception {
        when(y9OrganizationService.getById(y9Organization.getId())).thenReturn(y9Organization);
        when(y9DepartmentService.getById(y9Department.getId())).thenReturn(y9Department);
        String id = y9Organization.getId();
        if (orgType.equals(OrgTypeEnum.DEPARTMENT)) {
            id = y9Department.getId();
        }
        mockMvc.perform(post("/api/rest/org/getAllPersonsCount")
                        .param("id", id).param("orgType", orgType.toString()))
                .andExpect(status().isOk());
        if (orgType.equals(OrgTypeEnum.ORGANIZATION)) {
            verify(y9OrganizationService, times(1)).getById(y9Organization.getId());
        }
        if (orgType.equals(OrgTypeEnum.DEPARTMENT)) {
            verify(y9DepartmentService, times(1)).getById(y9Department.getId());
        }
        verify(y9PersonService, times(1)).countByGuidPathLikeAndDisabledAndDeletedFalse(id);
    }

    @Test
    void testSaveOrUpdate() throws Exception {
        when(y9OrganizationService.saveOrUpdate(y9Organization)).thenReturn(y9Organization);
        MvcResult mvcResult = mockMvc.perform(post("/api/rest/org/saveOrUpdate").contentType(MediaType.APPLICATION_JSON).param("id", y9Organization.getId()).param("name", y9Organization.getName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString(Charset.defaultCharset()));
    }

    @Test
    void testRemove() throws Exception {
        mockMvc.perform(post("/api/rest/org/remove").contentType(MediaType.APPLICATION_JSON).param("orgId", y9Organization.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}