package net.risesoft.service.org.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Person;
import net.risesoft.exception.AuthenticationErrorCodeEnum;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.model.platform.AuthenticateResult;
import net.risesoft.model.platform.Message;
import net.risesoft.model.platform.Person;
import net.risesoft.repository.Y9DepartmentRepository;
import net.risesoft.repository.Y9PersonRepository;
import net.risesoft.service.org.AuthService;
import net.risesoft.util.Y9PlatformUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9.util.base64.Y9Base64Util;
import net.risesoft.y9.util.signing.Y9MessageDigest;
import net.risesoft.y9public.entity.user.Y9User;
import net.risesoft.y9public.repository.user.Y9UserRepository;

/**
 * 认证服务
 *
 * @author shidaobang
 * @date 2024/05/23
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private static final String LOGINNAME_EMPTY = "loginName cannt be empty";
    private static final String PASSWORD_EMPTY = "password cannt be empty";

    private final Y9UserRepository y9UserRepository;
    private final Y9DepartmentRepository y9DepartmentRepository;
    private final Y9PersonRepository y9PersonRepository;

    private final CompositeOrgBaseManager compositeOrgBaseManager;

    @Override
    public Message authenticate(final String loginName, final String password) {
        Message message = new Message();
        if (StringUtils.isEmpty(loginName)) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg(LOGINNAME_EMPTY);
            return message;
        }
        if (StringUtils.isEmpty(password)) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg(PASSWORD_EMPTY);
            return message;
        }
        String newpassword = "";
        try {
            newpassword = Y9Base64Util.decode(password);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        Optional<Y9Person> y9PersonOptional = y9PersonRepository.findByLoginNameAndOriginalTrue(loginName);
        if (y9PersonOptional.isEmpty()
            || !(Y9MessageDigest.checkpw(newpassword, y9PersonOptional.get().getPassword()))) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg("loginName or password is incorrect");
            return message;
        }

        message.setStatus(Message.STATUS_SUCCESS);
        message.setMsg(y9PersonOptional.get().getId());
        return message;
    }

    @Override
    public Message authenticate2(final String tenantName, final String loginName, final String password) {
        Message message = new Message();
        if (StringUtils.isEmpty(tenantName)) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg("tenantName cannt be empty");
            return message;
        }
        if (StringUtils.isEmpty(loginName)) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg(LOGINNAME_EMPTY);
            return message;
        }
        if (StringUtils.isEmpty(password)) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg(PASSWORD_EMPTY);
            return message;
        }
        String newpassword = "";
        try {
            newpassword = Y9Base64Util.decode(password);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        Optional<Y9Person> y9PersonOptional = y9PersonRepository.findByLoginNameAndOriginalTrue(loginName);
        if (y9PersonOptional.isEmpty()
            || !(Y9MessageDigest.checkpw(newpassword, y9PersonOptional.get().getPassword()))) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg("loginName or password is incorrect");
            return message;
        }

        message.setStatus(Message.STATUS_SUCCESS);
        message.setMsg(y9PersonOptional.get().getId());
        return message;
    }

    @Override
    public AuthenticateResult authenticate3(String loginName, String base64EncodedPassword) {
        AuthenticateResult authenticateResult = new AuthenticateResult();
        String newpassword = "";
        try {
            newpassword = Y9Base64Util.decode(base64EncodedPassword);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        Y9Person person = null;
        String tenantId = "";
        if (loginName.contains("&")) {
            String realLoginName = loginName.split("&")[0];
            String fakeLoginName = loginName.split("&")[1];
            List<String> tenantIds = Y9PlatformUtil.getTenantByLoginName("operation");

            if (!tenantIds.isEmpty()) {
                tenantId = tenantIds.get(0);
            }
            Optional<Y9User> y9UserOptional =
                y9UserRepository.findByTenantIdAndLoginNameAndOriginalTrue(tenantId, fakeLoginName);
            Optional<Y9Person> y9PersonOptional = y9PersonRepository.findByLoginNameAndOriginalTrue(realLoginName);
            if (y9UserOptional.isEmpty() || !(Y9MessageDigest.checkpw(newpassword, y9UserOptional.get().getPassword()))
                || y9PersonOptional.isEmpty()) {
                throw Y9ExceptionUtil.businessException(AuthenticationErrorCodeEnum.LOGINNAME_PASSWORD_INCORRECT);
            }
            person = y9PersonOptional.get();
        } else {
            Optional<Y9Person> y9PersonOptional = y9PersonRepository.findByLoginNameAndOriginalTrue(loginName);
            if (y9PersonOptional.isEmpty()
                || !(Y9MessageDigest.checkpw(newpassword, y9PersonOptional.get().getPassword()))) {
                throw Y9ExceptionUtil.businessException(AuthenticationErrorCodeEnum.LOGINNAME_PASSWORD_INCORRECT);
            }
            person = y9PersonOptional.get();
        }

        Y9OrgBase department = compositeOrgBaseManager.getOrgUnitAsParent(person.getParentId());
        Y9OrgBase bureau = compositeOrgBaseManager.getOrgUnitBureau(person.getId());

        authenticateResult.setPerson(Y9ModelConvertUtil.convert(person, Person.class));
        authenticateResult.setTenantId(Y9LoginUserHolder.getTenantId());
        authenticateResult.setDeptName(department.getName());
        authenticateResult.setBureauName(bureau.getName());

        return authenticateResult;
    }

    @Override
    public Message authenticate3(final String tenantName, final String loginName, final String password) {
        Message message = new Message();
        if (StringUtils.isEmpty(tenantName)) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg("tenantName cannt be empty");
            return message;
        }
        if (StringUtils.isEmpty(loginName)) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg(LOGINNAME_EMPTY);
            return message;
        }
        if (StringUtils.isEmpty(password)) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg(PASSWORD_EMPTY);
            return message;
        }
        String newpassword = "";
        try {
            newpassword = Y9Base64Util.decode(password);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        Y9Person person = null;
        String tenantId = "";
        if (loginName.contains("&")) {
            String realLoginName = loginName.split("&")[0];
            String fakeLoginName = loginName.split("&")[1];
            List<String> tenantIds = Y9PlatformUtil.getTenantByLoginName("operation");

            if (!tenantIds.isEmpty()) {
                tenantId = tenantIds.get(0);
            }
            Optional<Y9User> y9UserOptional =
                y9UserRepository.findByTenantIdAndLoginNameAndOriginalTrue(tenantId, fakeLoginName);
            if (y9UserOptional.isEmpty()
                || !(Y9MessageDigest.checkpw(newpassword, y9UserOptional.get().getPassword()))) {
                message.setStatus(Message.STATUS_FAIL);
                message.setMsg("loginName or password is incorrect");
                return message;
            }

            person = y9PersonRepository.findByLoginNameAndOriginalTrue(realLoginName).orElse(null);
        } else {
            Optional<Y9Person> y9PersonOptional = y9PersonRepository.findByLoginNameAndOriginalTrue(loginName);
            if (y9PersonOptional.isEmpty()
                || !(Y9MessageDigest.checkpw(newpassword, y9PersonOptional.get().getPassword()))) {
                message.setStatus(Message.STATUS_FAIL);
                message.setMsg("loginName or password is incorrect");
                return message;
            }
            person = y9PersonOptional.get();
        }
        message.setStatus(Message.STATUS_SUCCESS);

        HashMap<String, Object> map = new HashMap<>(16);
        map.put("person", person);
        map.put("tenantId", Y9LoginUserHolder.getTenantId());
        Y9Department y9Department = y9DepartmentRepository.findById(person.getParentId()).orElse(null);
        Y9OrgBase y9OrgBase = compositeOrgBaseManager.getOrgUnitBureau(person.getId());
        map.put("deptName", y9Department != null ? y9Department.getName() : "");
        map.put("bureauName", y9OrgBase != null ? y9OrgBase.getName() : "");
        message.setMsg(Y9JsonUtil.writeValueAsString(map));
        return message;
    }

    @Override
    public Message authenticate4(final String tenantName, final String loginName) {
        Message message = new Message();
        if (StringUtils.isEmpty(tenantName)) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg("tenantName cannt be empty");
            return message;
        }
        if (StringUtils.isEmpty(loginName)) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg(LOGINNAME_EMPTY);
            return message;
        }

        Optional<Y9Person> y9PersonOptional = y9PersonRepository.findByLoginNameAndOriginalTrue(loginName);
        if (y9PersonOptional.isEmpty()) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg("No matching user according to the loginName");
            return message;
        }

        message.setStatus(Message.STATUS_SUCCESS);
        HashMap<String, Object> jsonObject = new HashMap<>(16);
        jsonObject.put("person", y9PersonOptional.get());
        jsonObject.put("tenantId", Y9LoginUserHolder.getTenantId());
        message.setMsg(jsonObject.toString());
        return message;
    }

    @Override
    public AuthenticateResult authenticate5(String mobile, String base64EncodedPassword) {
        AuthenticateResult authenticateResult = new AuthenticateResult();

        String password = Y9Base64Util.decode(base64EncodedPassword);
        Optional<Y9Person> optionalY9Person =
            y9PersonRepository.findByDisabledFalseAndMobileAndOriginal(mobile, Boolean.TRUE);
        if (optionalY9Person.isEmpty() || !(Y9MessageDigest.checkpw(password, optionalY9Person.get().getPassword()))) {
            throw Y9ExceptionUtil.businessException(AuthenticationErrorCodeEnum.LOGINNAME_PASSWORD_INCORRECT);
        }

        Y9Person person = optionalY9Person.get();

        Y9OrgBase department = compositeOrgBaseManager.getOrgUnitAsParent(person.getParentId());
        Y9OrgBase bureau = compositeOrgBaseManager.getOrgUnitBureau(person.getId());

        authenticateResult.setPerson(Y9ModelConvertUtil.convert(person, Person.class));
        authenticateResult.setTenantId(Y9LoginUserHolder.getTenantId());
        authenticateResult.setDeptName(department.getName());
        authenticateResult.setBureauName(bureau.getName());

        return authenticateResult;
    }

    @Override
    public Message authenticate5(final String tenantShortName, final String mobile, final String password) {
        Message message = new Message();
        if (StringUtils.isEmpty(tenantShortName)) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg("tenantShortName cannt be empty");
            return message;
        }
        if (StringUtils.isEmpty(mobile)) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg("mobile cannt be empty");
            return message;
        }
        if (StringUtils.isEmpty(password)) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg(PASSWORD_EMPTY);
            return message;
        }
        String newpassword = Y9Base64Util.decode(password);
        Optional<Y9Person> optionalY9Person =
            y9PersonRepository.findByDisabledFalseAndMobileAndOriginal(mobile, Boolean.TRUE);
        if (optionalY9Person.isEmpty()
            || !(Y9MessageDigest.checkpw(newpassword, optionalY9Person.get().getPassword()))) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg("mobile or password is incorrect");
            return message;
        }

        Y9Person person = optionalY9Person.get();

        HashMap<String, Object> map = new HashMap<>(16);
        map.put("person", person);
        map.put("tenantId", Y9LoginUserHolder.getTenantId());
        Y9Department y9Department = y9DepartmentRepository.findById(person.getParentId()).orElse(null);
        Y9OrgBase y9OrgBase = compositeOrgBaseManager.getOrgUnitBureau(person.getId());
        map.put("deptName", y9Department != null ? y9Department.getName() : "");
        map.put("bureauName", y9OrgBase != null ? y9OrgBase.getName() : "");
        message.setStatus(Message.STATUS_SUCCESS);
        message.setMsg(Y9JsonUtil.writeValueAsString(map));
        return message;
    }

    @Override
    public Message authenticate6(final String tenantShortName, final String loginName, final String password,
        final String parentId) {
        Message message = new Message();
        if (StringUtils.isEmpty(tenantShortName)) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg("tenantName cannt be empty");
            return message;
        }
        if (StringUtils.isEmpty(loginName)) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg(LOGINNAME_EMPTY);
            return message;
        }
        if (StringUtils.isEmpty(password)) {
            message.setStatus(Message.STATUS_FAIL);
            message.setMsg(PASSWORD_EMPTY);
            return message;
        }
        String newpassword = "";
        try {
            newpassword = Y9Base64Util.decode(password);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        Y9Person person = null;
        String tenantId = "";
        if (loginName.contains("&")) {
            String realLoginName = loginName.split("&")[0];
            String fakeLoginName = loginName.split("&")[1];
            List<String> tenantIds = Y9PlatformUtil.getTenantByLoginName("operation");
            if (!tenantIds.isEmpty()) {
                tenantId = tenantIds.get(0);
            }
            Optional<Y9User> y9UserOptional =
                y9UserRepository.findByTenantIdAndLoginNameAndOriginalTrue(tenantId, fakeLoginName);
            if (y9UserOptional.isEmpty()
                || !(Y9MessageDigest.checkpw(newpassword, y9UserOptional.get().getPassword()))) {
                message.setStatus(Message.STATUS_FAIL);
                message.setMsg("loginName or password is incorrect");
                return message;
            }

            person = y9PersonRepository.findByLoginNameAndOriginalTrue(realLoginName).orElse(null);
        } else {
            Optional<Y9Person> y9PersonOptional = y9PersonRepository.findByLoginNameAndOriginalTrue(loginName);
            if (y9PersonOptional.isEmpty()
                || !(Y9MessageDigest.checkpw(newpassword, y9PersonOptional.get().getPassword()))) {
                message.setStatus(Message.STATUS_FAIL);
                message.setMsg("loginName or password is incorrect");
                return message;
            }
            person = y9PersonOptional.get();
        }

        message.setStatus(Message.STATUS_SUCCESS);

        HashMap<String, Object> map = new HashMap<>(16);
        map.put("person", person);
        map.put("tenantId", Y9LoginUserHolder.getTenantId());
        Y9Department y9Department = y9DepartmentRepository.findById(person.getParentId()).orElse(null);
        Y9OrgBase y9OrgBase = compositeOrgBaseManager.getOrgUnitBureau(person.getId());
        map.put("deptName", y9Department != null ? y9Department.getName() : "");
        map.put("bureauName", y9OrgBase != null ? y9OrgBase.getName() : "");
        message.setMsg(Y9JsonUtil.writeValueAsString(map));
        return message;
    }
}
