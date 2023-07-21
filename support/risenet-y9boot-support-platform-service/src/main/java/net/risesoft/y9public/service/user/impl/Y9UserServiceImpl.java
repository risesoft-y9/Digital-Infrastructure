package net.risesoft.y9public.service.user.impl;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Person;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.pubsub.constant.Y9OrgEventConst;
import net.risesoft.y9.pubsub.constant.Y9TopicConst;
import net.risesoft.y9.pubsub.message.Y9MessageOrg;
import net.risesoft.y9public.entity.user.Y9User;
import net.risesoft.y9public.repository.user.Y9UserRepository;
import net.risesoft.y9public.service.user.Y9UserService;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
@RequiredArgsConstructor
public class Y9UserServiceImpl implements Y9UserService {

    private final KafkaTemplate<String, Object> y9KafkaTemplate;
    private final Y9UserRepository y9UserRepository;

    @Override
    public boolean checkCaidAvailability(String personId, String caid) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (StringUtils.isBlank(personId)) {
            // 新增用户
            Y9User y9User = y9UserRepository.findByTenantIdAndCaid(tenantId, caid);
            return y9User == null;
        } else {
            Y9User y9User = y9UserRepository.findByTenantIdAndCaidAndPersonIdNot(tenantId, caid, personId);
            return y9User == null;
        }
    }

    @Override
    public boolean checkLoginName(Y9Person y9Person, String loginName) {
        String tenantId = y9Person.getTenantId();
        if (tenantId == null) {
            tenantId = Y9LoginUserHolder.getTenantId();
        }
        if (StringUtils.isNotBlank(loginName)) {
            Y9User orgUser = this.findByMobileAndTenantId(y9Person.getId(), loginName, tenantId);
            return orgUser == null;
        } else {
            return true;
        }
    }

    @Override
    public boolean checkMobile(Y9Person y9Person, String mobile) {
        String tenantId = y9Person.getTenantId();
        if (tenantId == null) {
            tenantId = Y9LoginUserHolder.getTenantId();
        }
        if (StringUtils.isNotBlank(mobile)) {
            Y9User orgUser = this.findByMobileAndTenantId(y9Person.getId(), mobile, tenantId);
            return orgUser == null;
        } else {
            return true;
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String id) {
        Optional<Y9User> orgUser = y9UserRepository.findById(id);
        if (orgUser.isPresent()) {
            y9UserRepository.deleteById(id);

            String json = Y9JsonUtil.writeValueAsString(orgUser.get());
            Y9MessageOrg riseEvent = new Y9MessageOrg(json, Y9OrgEventConst.RISEORGEVENT_TYPE_DELETE_USER, Y9LoginUserHolder.getTenantId());
            y9KafkaTemplate.send(Y9TopicConst.Y9_USER_EVENT, Y9JsonUtil.writeValueAsString(riseEvent));
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteByTenantId(String tenantId) {
        List<Y9User> y9UserList = y9UserRepository.findByTenantId(tenantId);
        y9UserRepository.deleteAll(y9UserList);
    }

    @Override
    public Y9User findByLoginNameAndTenantIdWithoutPersonId(String loginName, String tenantId) {
        return y9UserRepository.findByLoginNameAndTenantId(loginName, tenantId);
    }

    @Override
    public Y9User findByMobileAndTenantId(String personId, String mobile, String tenantId) {
        return y9UserRepository.findByPersonIdIsNotAndMobileAndTenantId(personId, mobile, tenantId);
    }

    @Override
    public Y9User findByPersonId(String personId) {
        return y9UserRepository.findByPersonId(personId);
    }

    @Override
    public Y9User findByPersonIdAndTenantId(String personId, String tenantId) {
        return y9UserRepository.findByPersonIdAndTenantId(personId, tenantId);
    }

    @Override
    public List<Y9User> listAll() {
        return y9UserRepository.findAll();
    }

    @Override
    public List<Y9User> listByGuidPathLike(String guidPath) {
        return y9UserRepository.findByGuidPathContaining(guidPath);
    }

    @Override
    public List<Y9User> listByLoginName(String loginName) {
        return y9UserRepository.findByLoginName(loginName);
    }

    @Override
    public List<Y9User> listByTenantId(String tenantId) {
        return y9UserRepository.findByTenantId(tenantId);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9User save(Y9User orgUser) {
        Y9User saved = y9UserRepository.save(orgUser);

        String json = Y9JsonUtil.writeValueAsString(saved);
        Y9MessageOrg riseEvent = new Y9MessageOrg(json, Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_USER, Y9LoginUserHolder.getTenantId());
        y9KafkaTemplate.send(Y9TopicConst.Y9_USER_EVENT, Y9JsonUtil.writeValueAsString(riseEvent));
        return saved;
    }

    @Override
    public void sync() {
        List<Y9User> orgUsers = y9UserRepository.findAll();
        if (!orgUsers.isEmpty()) {
            String json = Y9JsonUtil.writeValueAsString(orgUsers);
            Y9MessageOrg riseEvent = new Y9MessageOrg(json, Y9OrgEventConst.RISEORGEVENT_TYPE_USER_SYNC, Y9LoginUserHolder.getTenantId());
            y9KafkaTemplate.send(Y9TopicConst.Y9_USER_EVENT, Y9JsonUtil.writeValueAsString(riseEvent));
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void updateByTenantId(String tenantId, String tenantName, String tenantShortName) {
        List<Y9User> list = y9UserRepository.findByTenantId(tenantId);
        for (Y9User orgUser : list) {
            orgUser.setTenantName(tenantName);
            orgUser.setTenantShortName(tenantShortName);
            save(orgUser);
        }
    }
}
