package net.risesoft.y9public.repository.custom.impl;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import net.risesoft.log.constant.Y9LogSearchConsts;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.Y9ClickedApp;
import net.risesoft.y9public.repository.Y9ClickedAppRepository;
import net.risesoft.y9public.repository.Y9logAccessLogRepository;
import net.risesoft.y9public.repository.custom.Y9CommonAppForPersonCustomRepository;

/**
 * 个人常用应用
 *
 * @author mengjuhua
 *
 */
@Component
@RequiredArgsConstructor
public class Y9CommonAppForPersonCustomRepositoryImpl implements Y9CommonAppForPersonCustomRepository {

    private final Y9logAccessLogRepository y9logAccessLogRepository;

    private final Y9ClickedAppRepository y9ClickedAppRepository;

    @Override
    public List<String> getAppNamesByPersonId(String personId) {
        List<Y9ClickedApp> y9ClickedAppList =
            y9ClickedAppRepository.findByTenantIdAndPersonId(Y9LoginUserHolder.getTenantId(), personId);
        return y9ClickedAppList.stream().map(Y9ClickedApp::getAppName).collect(Collectors.toList());
    }

    @Override
    public long getCount() {
        // 最近半年
        Calendar c = Calendar.getInstance();
        long endTime = c.getTime().getTime();
        c.add(Calendar.MONTH, -6);
        long startTime = c.getTime().getTime();
        return y9logAccessLogRepository.countByModularNameAndLogTimeBetween(Y9LogSearchConsts.MODULAR_NAME, startTime,
            endTime);
    }
}
