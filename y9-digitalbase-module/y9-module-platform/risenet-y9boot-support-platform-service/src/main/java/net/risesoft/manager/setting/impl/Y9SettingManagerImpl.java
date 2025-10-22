package net.risesoft.manager.setting.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.entity.setting.Y9Setting;
import net.risesoft.manager.setting.Y9SettingManager;
import net.risesoft.repository.setting.Y9SettingRepository;

/**
 * 设置 Manager 实现类
 *
 * @author shidaobang
 * @date 2024/03/28
 */
@Service
@CacheConfig(cacheNames = CacheNameConsts.SETTING)
@RequiredArgsConstructor
public class Y9SettingManagerImpl implements Y9SettingManager {

    private final Y9SettingRepository y9SettingRepository;

    @Override
    @Cacheable(key = "T(net.risesoft.y9.Y9LoginUserHolder).getTenantId()+'_list'", unless = "#result.empty")
    public List<Y9Setting> findAll() {
        return y9SettingRepository.findAll();
    }

    @Override
    @Cacheable(key = "T(net.risesoft.y9.Y9LoginUserHolder).getTenantId()+'_'+#id", condition = "#id!=null",
        unless = "#result==null")
    public Optional<Y9Setting> findById(String id) {
        return y9SettingRepository.findById(id);
    }

    @Override
    @Caching(evict = {
        @CacheEvict(key = "T(net.risesoft.y9.Y9LoginUserHolder).getTenantId()+'_'+#y9Setting.key",
            condition = "#y9Setting.key!=null"),
        @CacheEvict(key = "T(net.risesoft.y9.Y9LoginUserHolder).getTenantId()+'_list'",
            condition = "#y9Setting.key!=null")})
    public Y9Setting save(Y9Setting y9Setting) {
        return y9SettingRepository.save(y9Setting);
    }
}
