package net.risesoft.service.org;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import net.risesoft.consts.PasswordHashAlgorithmConsts;
import net.risesoft.entity.org.Y9Department;
import net.risesoft.entity.org.Y9Person;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.model.platform.Message;
import net.risesoft.repository.org.Y9DepartmentRepository;
import net.risesoft.repository.org.Y9PersonRepository;
import net.risesoft.service.org.impl.AuthServiceImpl;
import net.risesoft.util.Y9PlatformUtil;
import net.risesoft.y9.exception.Y9BusinessException;
import net.risesoft.y9.util.base64.Y9Base64Util;
import net.risesoft.y9.util.signing.Y9MessageDigestUtil;
import net.risesoft.y9public.entity.Y9User;
import net.risesoft.y9public.repository.Y9UserRepository;

class AuthServicePasswordAlgorithmTest {

    private static final String PASSWORD = Y9Base64Util.encode("Chosen1!");
    private AuthServiceImpl service;
    private Y9Person person;
    private Y9User agent;
    private Object previousJdbcTemplate;

    @BeforeEach
    void setUp() {
        Y9PersonRepository persons = mock(Y9PersonRepository.class);
        Y9UserRepository users = mock(Y9UserRepository.class);
        Y9DepartmentRepository departments = mock(Y9DepartmentRepository.class);
        CompositeOrgBaseManager orgs = mock(CompositeOrgBaseManager.class);
        service = new AuthServiceImpl(users, departments, persons, orgs);
        person = new Y9Person();
        person.setId("person1");
        person.setParentId("dept1");
        person.setPassword(Y9MessageDigestUtil.bcrypt("Chosen1!"));
        person.setPasswordHashAlgorithm(PasswordHashAlgorithmConsts.BCRYPT);
        when(persons.findByLoginNameAndOriginalTrue(anyString())).thenReturn(Optional.of(person));
        when(persons.findByDisabledFalseAndMobileAndOriginal(anyString(), eq(true)))
            .thenReturn(Optional.of(person));
        Y9Department dept = new Y9Department();
        dept.setName("department");
        when(orgs.getOrgUnitAsParent(anyString())).thenReturn(dept);
        when(orgs.getOrgUnitBureau(anyString())).thenReturn(dept);
        when(departments.findById(anyString())).thenReturn(Optional.of(dept));
        agent = new Y9User();
        agent.setPassword(person.getPassword());
        agent.setPasswordHashAlgorithm(PasswordHashAlgorithmConsts.BCRYPT);
        when(users.findByLoginNameAndTenantIdAndOriginalTrue("agent", "operation-id"))
            .thenReturn(Optional.of(agent));
        previousJdbcTemplate = ReflectionTestUtils.getField(Y9PlatformUtil.class, "jdbcTemplate4Public");
        JdbcTemplate jdbc = mock(JdbcTemplate.class);
        when(jdbc.queryForList(anyString(), eq(String.class))).thenReturn(List.of("operation-id"));
        ReflectionTestUtils.setField(Y9PlatformUtil.class, "jdbcTemplate4Public", jdbc);
    }

    @AfterEach
    void tearDown() {
        ReflectionTestUtils.setField(Y9PlatformUtil.class, "jdbcTemplate4Public", previousJdbcTemplate);
    }

    @Test
    void ordinaryLoginAndMobileEntrypointsAcceptStoredBcryptAlgorithm() {
        assertEquals(Message.STATUS_SUCCESS, service.authenticate("person", PASSWORD).getStatus());
        assertEquals(Message.STATUS_SUCCESS, service.authenticate2("tenant", "person", PASSWORD).getStatus());
        assertDoesNotThrow(() -> service.authenticate3("person", PASSWORD));
        assertEquals(Message.STATUS_SUCCESS, service.authenticate3("tenant", "person", PASSWORD).getStatus());
        assertDoesNotThrow(() -> service.authenticate5("13800138000", PASSWORD));
        assertEquals(Message.STATUS_SUCCESS, service.authenticate5("tenant", "13800138000", PASSWORD).getStatus());
        assertEquals(Message.STATUS_SUCCESS, service.authenticate6("tenant", "person", PASSWORD, "dept1").getStatus());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"unknown"})
    void ordinaryLoginAndMobileEntrypointsRejectMissingOrUnsupportedAlgorithm(String algorithm) {
        person.setPasswordHashAlgorithm(algorithm);
        assertEquals(Message.STATUS_FAIL, service.authenticate("person", PASSWORD).getStatus());
        assertEquals(Message.STATUS_FAIL, service.authenticate2("tenant", "person", PASSWORD).getStatus());
        assertThrows(Y9BusinessException.class, () -> service.authenticate3("person", PASSWORD));
        assertEquals(Message.STATUS_FAIL, service.authenticate3("tenant", "person", PASSWORD).getStatus());
        assertThrows(Y9BusinessException.class, () -> service.authenticate5("13800138000", PASSWORD));
        assertEquals(Message.STATUS_FAIL, service.authenticate5("tenant", "13800138000", PASSWORD).getStatus());
        assertEquals(Message.STATUS_FAIL, service.authenticate6("tenant", "person", PASSWORD, "dept1").getStatus());
    }

    @Test
    void proxyLoginUsesAgentAccountAlgorithm() {
        person.setPasswordHashAlgorithm("unknown");
        assertDoesNotThrow(() -> service.authenticate3("person&agent", PASSWORD));
        assertEquals(Message.STATUS_SUCCESS, service.authenticate3("tenant", "person&agent", PASSWORD).getStatus());
        assertEquals(Message.STATUS_SUCCESS,
            service.authenticate6("tenant", "person&agent", PASSWORD, "dept1").getStatus());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"unknown"})
    void proxyLoginRejectsMissingOrUnsupportedAgentAlgorithm(String algorithm) {
        agent.setPasswordHashAlgorithm(algorithm);
        assertThrows(Y9BusinessException.class, () -> service.authenticate3("person&agent", PASSWORD));
        assertEquals(Message.STATUS_FAIL, service.authenticate3("tenant", "person&agent", PASSWORD).getStatus());
        assertEquals(Message.STATUS_FAIL, service.authenticate6("tenant", "person&agent", PASSWORD, "dept1").getStatus());
    }
}
