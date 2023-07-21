package net.risesoft.log.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.log.entity.Y9ClickedApp;
import net.risesoft.log.repository.Y9ClickedAppRepository;
import net.risesoft.log.service.Y9ClickedAppService;

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
    
    private final Y9ClickedAppRepository y9ClickedAppRepository;

    @Override
    public void save(Y9ClickedApp clickedApp) {
        y9ClickedAppRepository.save(clickedApp);
    }

}
