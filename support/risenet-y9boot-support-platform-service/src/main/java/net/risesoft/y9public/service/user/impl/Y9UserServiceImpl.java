package net.risesoft.y9public.service.user.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.y9.Y9LoginUserHolder;
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
    public boolean isCaidAvailable(String personId, String caid) {
        Optional<Y9User> y9UserOptional = y9UserRepository.findByTenantIdAndCaid(Y9LoginUserHolder.getTenantId(), caid);
        if (y9UserOptional.isEmpty()) {
            return true;
        }

        return y9UserOptional.get().getPersonId().equals(personId);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String id) {
        Optional<Y9User> orgUser = y9UserRepository.findById(id);
        if (orgUser.isPresent()) {
            y9UserRepository.deleteById(id);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteByTenantId(String tenantId) {
        List<Y9User> y9UserList = y9UserRepository.findByTenantId(tenantId);
        y9UserRepository.deleteAll(y9UserList);
    }

    @Override
    public Optional<Y9User> findByLoginNameAndTenantId(String loginName, String tenantId) {
        return y9UserRepository.findByTenantIdAndLoginNameAndOriginalTrue(tenantId, loginName);
    }

    @Override
    public Optional<Y9User> findByPersonIdAndTenantId(String personId, String tenantId) {
        return y9UserRepository.findByTenantIdAndPersonId(tenantId, personId);
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
        return y9UserRepository.save(orgUser);
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
