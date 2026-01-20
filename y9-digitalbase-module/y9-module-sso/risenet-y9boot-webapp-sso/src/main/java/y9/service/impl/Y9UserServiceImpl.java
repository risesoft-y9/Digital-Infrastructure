package y9.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import y9.entity.Y9User;
import y9.repository.Y9UserRepository;
import y9.service.Y9UserService;

@Service("y9UserService")
@RequiredArgsConstructor
public class Y9UserServiceImpl implements Y9UserService {
    private final Y9UserRepository y9UserRepository;

    @Override
    public Y9User save(Y9User y9User) {
        return y9UserRepository.save(y9User);
    }

    @Override
    public Y9User findByPersonIdAndTenantId(String id, String tenantId) {
        return y9UserRepository.findByPersonIdAndTenantId(id, tenantId);
    }

    @Override
    public List<Y9User> findByLoginNameAndOriginal(String loginName, Boolean original) {
        return y9UserRepository.findByLoginNameAndOriginal(loginName, original);
    }

    @Override
    public List<Y9User> findByMobileAndOriginal(String mobile, Boolean original) {
        return y9UserRepository.findByMobileAndOriginal(mobile, original);
    }

    @Override
    public List<Y9User> findByTenantShortNameAndLoginNameAndOriginal(String tenantShortName, String loginName,
        Boolean original) {
        return y9UserRepository.findByTenantShortNameAndLoginNameAndOriginal(tenantShortName, loginName, original);
    }

    @Override
    public List<Y9User> findByTenantShortNameAndLoginNameAndParentId(String tenantShortName, String loginName,
        String parentId) {
        return y9UserRepository.findByTenantShortNameAndLoginNameAndParentId(tenantShortName, loginName, parentId);
    }

    @Override
    public List<Y9User> findByTenantShortNameAndMobileAndOriginal(String tenantShortName, String mobile,
        Boolean original) {
        return y9UserRepository.findByTenantShortNameAndMobileAndOriginal(tenantShortName, mobile, original);
    }

    @Override
    public List<Y9User> findByTenantShortNameAndMobileAndParentId(String tenantShortName, String mobile,
        String parentId) {
        return y9UserRepository.findByTenantShortNameAndMobileAndParentId(tenantShortName, mobile, parentId);
    }

    @Override
    public List<Y9User> findByPersonIdAndOriginal(String personId, Boolean original) {
        return y9UserRepository.findByPersonIdAndOriginal(personId, original);
    }

}
