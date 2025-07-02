package y9.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import y9.entity.Y9Tenant;
import y9.repository.Y9TenantRepository;
import y9.service.Y9TenantService;

@Service
@RequiredArgsConstructor
public class Y9TenantServiceImpl implements Y9TenantService {

    private final Y9TenantRepository y9TenantRepository;

    @Override
    public List<Y9Tenant> listByEnabled(Boolean enabled) {
        return y9TenantRepository.findByEnabledOrderByTabIndex(enabled);
    }
}
