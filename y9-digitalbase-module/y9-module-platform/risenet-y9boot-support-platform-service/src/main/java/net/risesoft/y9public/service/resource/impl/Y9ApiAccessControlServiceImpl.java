package net.risesoft.y9public.service.resource.impl;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.enums.platform.ApiAccessControlType;
import net.risesoft.exception.GlobalErrorCodeEnum;
import net.risesoft.exception.ResourceErrorCodeEnum;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.util.IpUtil;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9public.entity.resource.Y9ApiAccessControl;
import net.risesoft.y9public.repository.resource.Y9ApiAccessControlRepository;
import net.risesoft.y9public.service.resource.Y9ApiAccessControlService;

import inet.ipaddr.AddressStringException;

/**
 * api 访问控制实现类
 *
 * @author shidaobang
 * @date 2024/11/26
 * @since 9.6.8
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class Y9ApiAccessControlServiceImpl implements Y9ApiAccessControlService {

    private final Y9ApiAccessControlRepository y9ApiAccessControlRepository;

    @Override
    public List<Y9ApiAccessControl> listByType(ApiAccessControlType type) {
        return y9ApiAccessControlRepository.findByTypeOrderByCreateTime(type);
    }

    @Override
    public List<Y9ApiAccessControl> listByTypeAndEnabled(ApiAccessControlType type) {
        return y9ApiAccessControlRepository.findByTypeAndEnabledTrueOrderByCreateTime(type);
    }

    @Override
    public Y9ApiAccessControl saveOrUpdate(Y9ApiAccessControl apiAccessControl) {

        if (ApiAccessControlType.WHITE_LIST.equals(apiAccessControl.getType())
            || ApiAccessControlType.BLACK_LIST.equals(apiAccessControl.getType())) {
            checkIpValidity(apiAccessControl.getValue());
        }

        if (StringUtils.isBlank(apiAccessControl.getId())) {
            apiAccessControl.setId(Y9IdGenerator.genId());
            apiAccessControl.setTabIndex(this.getNextTabIndex(apiAccessControl.getType()));
            return y9ApiAccessControlRepository.save(apiAccessControl);
        }

        Optional<Y9ApiAccessControl> y9ApiAccessControlOptional =
            y9ApiAccessControlRepository.findById(apiAccessControl.getId());
        if (y9ApiAccessControlOptional.isPresent()) {
            Y9ApiAccessControl y9ApiAccessControl = y9ApiAccessControlOptional.get();
            Y9BeanUtil.copyProperties(apiAccessControl, y9ApiAccessControl);
            return y9ApiAccessControlRepository.save(y9ApiAccessControl);
        }
        apiAccessControl.setTabIndex(this.getNextTabIndex(apiAccessControl.getType()));
        return y9ApiAccessControlRepository.save(apiAccessControl);
    }

    @Override
    public Y9ApiAccessControl getById(String id) {
        return y9ApiAccessControlRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(GlobalErrorCodeEnum.ACCESS_CONTROL_NOT_FOUND, id));
    }

    @Override
    public boolean isMatch(String key, String value, Boolean enabled) {
        Optional<Y9ApiAccessControl> y9ApiAccessControlOptional = y9ApiAccessControlRepository.findById(key);
        if (y9ApiAccessControlOptional.isPresent()) {
            Y9ApiAccessControl y9ApiAccessControl = y9ApiAccessControlOptional.get();
            return y9ApiAccessControl.getValue().equals(value) && y9ApiAccessControl.getEnabled().equals(enabled);
        }
        return false;
    }

    @Override
    public void delete(String id) {
        y9ApiAccessControlRepository.deleteById(id);
    }

    @Override
    public Y9ApiAccessControl changeEnabled(String id) {
        Y9ApiAccessControl y9ApiAccessControl = getById(id);
        y9ApiAccessControl.setEnabled(!y9ApiAccessControl.getEnabled());
        return saveOrUpdate(y9ApiAccessControl);
    }

    @Override
    public Y9ApiAccessControl saveAppIdSecret(Y9ApiAccessControl apiAccessControl) {
        if (StringUtils.isBlank(apiAccessControl.getId())) {
            apiAccessControl.setValue(RandomStringUtils.randomAlphanumeric(38));
        } else {
            // 清空前端传来的值，避免被修改了
            apiAccessControl.setValue(null);
        }

        return this.saveOrUpdate(apiAccessControl);
    }

    private void checkIpValidity(String value) {
        String[] ips = StringUtils.split(value, ",");
        for (String ip : ips) {
            try {
                IpUtil.parse(ip);
            } catch (AddressStringException e) {
                throw Y9ExceptionUtil.businessException(ResourceErrorCodeEnum.IP_NOT_VALID, ip);
            }
        }
    }

    private Integer getNextTabIndex(ApiAccessControlType type) {
        return y9ApiAccessControlRepository.findTopByTypeOrderByCreateTime(type).map(Y9ApiAccessControl::getTabIndex)
            .orElse(-1) + 1;
    }
}
