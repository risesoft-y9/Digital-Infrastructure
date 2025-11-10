package net.risesoft.y9public.service.user.impl;

import static net.risesoft.consts.JpaPublicConsts.PUBLIC_TRANSACTION_MANAGER;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.model.user.UserInfo;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9public.entity.Y9User;
import net.risesoft.y9public.repository.Y9UserRepository;
import net.risesoft.y9public.service.user.Y9UserService;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service
@RequiredArgsConstructor
public class Y9UserServiceImpl implements Y9UserService {

    private final Y9UserRepository y9UserRepository;

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void delete(String id) {
        Optional<Y9User> orgUser = y9UserRepository.findById(id);
        if (orgUser.isPresent()) {
            y9UserRepository.deleteById(id);
        }
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void deleteByTenantId(String tenantId) {
        List<Y9User> y9UserList = y9UserRepository.findByTenantId(tenantId);
        y9UserRepository.deleteAll(y9UserList);
    }

    @Override
    public Optional<UserInfo> findByPersonIdAndTenantId(String personId, String tenantId) {
        return y9UserRepository.findByPersonIdAndTenantId(personId, tenantId).map(this::entityToModel);
    }

    @Override
    public List<UserInfo> listAll() {
        List<Y9User> y9UserList = y9UserRepository.findAll();
        return entityToModel(y9UserList);
    }

    @Override
    public List<UserInfo> listByTenantId(String tenantId) {
        List<Y9User> y9UserList = y9UserRepository.findByTenantId(tenantId);
        return entityToModel(y9UserList);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public UserInfo save(UserInfo userInfo) {
        Y9User y9User = PlatformModelConvertUtil.convert(userInfo, Y9User.class);
        return entityToModel(y9UserRepository.save(y9User));
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void updateByTenantId(String tenantId, String tenantName, String tenantShortName) {
        List<UserInfo> list = this.listByTenantId(tenantId);
        for (UserInfo userInfo : list) {
            userInfo.setTenantName(tenantName);
            userInfo.setTenantShortName(tenantShortName);
            save(userInfo);
        }
    }

    private List<UserInfo> entityToModel(List<Y9User> y9UserList) {
        return PlatformModelConvertUtil.convert(y9UserList, UserInfo.class);
    }

    private UserInfo entityToModel(Y9User y9User) {
        return PlatformModelConvertUtil.convert(y9User, UserInfo.class);
    }
}
