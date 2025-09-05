package net.risesoft.repository.impl;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.log.domain.Y9ClickedAppDO;
import net.risesoft.log.repository.Y9ClickedAppCustomRepository;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.Y9ClickedApp;
import net.risesoft.y9public.repository.Y9ClickedAppRepository;

/**
 * @author shidaobang
 * @date 2025/09/04
 */
@Component
@RequiredArgsConstructor
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
public class Y9ClickedAppCustomRepositoryImpl implements Y9ClickedAppCustomRepository {

    private final Y9ClickedAppRepository y9ClickedAppRepository;

    @Override
    @Transactional(readOnly = false)
    public void save(Y9ClickedAppDO clickedApp) {
        Y9ClickedApp y9ClickedApp = Y9ModelConvertUtil.convert(clickedApp, Y9ClickedApp.class);
        y9ClickedAppRepository.save(y9ClickedApp);
    }
}
