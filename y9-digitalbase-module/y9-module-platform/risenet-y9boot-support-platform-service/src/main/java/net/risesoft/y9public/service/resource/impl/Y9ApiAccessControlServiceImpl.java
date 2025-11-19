package net.risesoft.y9public.service.resource.impl;

import static net.risesoft.consts.JpaPublicConsts.PUBLIC_TRANSACTION_MANAGER;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.enums.platform.ApiAccessControlType;
import net.risesoft.exception.GlobalErrorCodeEnum;
import net.risesoft.exception.ResourceErrorCodeEnum;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.ApiAccessControl;
import net.risesoft.util.IpUtil;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9public.entity.Y9ApiAccessControl;
import net.risesoft.y9public.repository.Y9ApiAccessControlRepository;
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

    private static List<ApiAccessControl> entityToModel(List<Y9ApiAccessControl> y9ApiAccessControlList) {
        return PlatformModelConvertUtil.convert(y9ApiAccessControlList, ApiAccessControl.class);
    }

    private static ApiAccessControl entityToModel(Y9ApiAccessControl save) {
        return PlatformModelConvertUtil.convert(save, ApiAccessControl.class);
    }

    @Override
    public List<ApiAccessControl> listByType(ApiAccessControlType type) {
        List<Y9ApiAccessControl> y9ApiAccessControlList =
            y9ApiAccessControlRepository.findByTypeOrderByCreateTime(type);
        return entityToModel(y9ApiAccessControlList);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public ApiAccessControl saveOrUpdate(ApiAccessControl apiAccessControl) {
        Y9ApiAccessControl y9ApiAccessControl =
            PlatformModelConvertUtil.convert(apiAccessControl, Y9ApiAccessControl.class);

        if (ApiAccessControlType.WHITE_LIST.equals(y9ApiAccessControl.getType())
            || ApiAccessControlType.BLACK_LIST.equals(y9ApiAccessControl.getType())) {
            checkIpValidity(y9ApiAccessControl.getValue());
        }

        if (StringUtils.isBlank(y9ApiAccessControl.getId())) {
            y9ApiAccessControl.setId(Y9IdGenerator.genId());
            y9ApiAccessControl.setTabIndex(this.getNextTabIndex(y9ApiAccessControl.getType()));
            Y9ApiAccessControl savedApiAccessControl = y9ApiAccessControlRepository.save(y9ApiAccessControl);
            return entityToModel(savedApiAccessControl);
        }

        Optional<Y9ApiAccessControl> y9ApiAccessControlOptional =
            y9ApiAccessControlRepository.findById(y9ApiAccessControl.getId());
        if (y9ApiAccessControlOptional.isPresent()) {
            Y9ApiAccessControl originalApiAccessControl = y9ApiAccessControlOptional.get();
            Y9BeanUtil.copyProperties(y9ApiAccessControl, originalApiAccessControl);
            Y9ApiAccessControl savedApiAccessControl = y9ApiAccessControlRepository.save(originalApiAccessControl);
            return entityToModel(savedApiAccessControl);
        }
        y9ApiAccessControl.setTabIndex(this.getNextTabIndex(y9ApiAccessControl.getType()));
        Y9ApiAccessControl savedApiAccessControl = y9ApiAccessControlRepository.save(y9ApiAccessControl);
        return entityToModel(savedApiAccessControl);
    }

    public ApiAccessControl getById(String id) {
        return y9ApiAccessControlRepository.findById(id)
            .map(Y9ApiAccessControlServiceImpl::entityToModel)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(GlobalErrorCodeEnum.ACCESS_CONTROL_NOT_FOUND, id));
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void delete(String id) {

        y9ApiAccessControlRepository.deleteById(id);

    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public ApiAccessControl changeEnabled(String id) {
        ApiAccessControl apiAccessControl = getById(id);
        apiAccessControl.setEnabled(!apiAccessControl.getEnabled());
        return saveOrUpdate(apiAccessControl);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public ApiAccessControl saveAppIdSecret(ApiAccessControl apiAccessControl) {
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
        return y9ApiAccessControlRepository.findTopByTypeOrderByCreateTime(type)
            .map(Y9ApiAccessControl::getTabIndex)
            .orElse(-1) + 1;
    }
}
