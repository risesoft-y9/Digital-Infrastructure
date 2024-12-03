package net.risesoft.y9public.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.enums.platform.ApiAccessControlType;
import net.risesoft.exception.GlobalErrorCodeEnum;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9public.entity.Y9ApiAccessControl;
import net.risesoft.y9public.repository.Y9ApiAccessControlRepository;
import net.risesoft.y9public.service.Y9ApiAccessControlService;

/**
 * api 访问控制实现类
 *
 * @author shidaobang
 * @date 2024/11/26
 * @since 9.6.8
 */
@Service(value = "apiAccessControlService")
@Slf4j
@RequiredArgsConstructor
public class Y9ApiAccessControlServiceImpl implements Y9ApiAccessControlService {

    private final Y9ApiAccessControlRepository y9ApiAccessControlRepository;

    @Override
    public List<Y9ApiAccessControl> listByTypeAndEnabled(ApiAccessControlType type) {
        return y9ApiAccessControlRepository.findByTypeAndEnabledTrueOrderByCreateTime(type);
    }

    @Override
    public Y9ApiAccessControl getById(String id) {
        return y9ApiAccessControlRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(GlobalErrorCodeEnum.ACCESS_CONTROL_NOT_FOUND, id));
    }

}
