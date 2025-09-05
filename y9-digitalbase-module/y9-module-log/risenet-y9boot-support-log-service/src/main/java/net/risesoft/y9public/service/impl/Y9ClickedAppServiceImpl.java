package net.risesoft.y9public.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.log.domain.Y9ClickedAppDO;
import net.risesoft.log.repository.Y9ClickedAppCustomRepository;
import net.risesoft.y9public.service.Y9ClickedAppService;

/**
 *
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
@Service
@RequiredArgsConstructor
public class Y9ClickedAppServiceImpl implements Y9ClickedAppService {

    private final Y9ClickedAppCustomRepository y9ClickedAppCustomRepository;

    @Override
    public void save(Y9ClickedAppDO clickedApp) {
        y9ClickedAppCustomRepository.save(clickedApp);
    }

}
