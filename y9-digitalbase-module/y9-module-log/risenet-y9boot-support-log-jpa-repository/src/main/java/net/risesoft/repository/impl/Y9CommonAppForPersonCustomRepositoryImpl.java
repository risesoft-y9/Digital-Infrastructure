package net.risesoft.repository.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import net.risesoft.log.constant.Y9LogSearchConsts;
import net.risesoft.log.domain.Y9CommonAppForPersonDO;
import net.risesoft.log.repository.Y9CommonAppForPersonCustomRepository;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.Y9ClickedApp;
import net.risesoft.y9public.entity.Y9CommonAppForPerson;
import net.risesoft.y9public.repository.Y9ClickedAppRepository;
import net.risesoft.y9public.repository.Y9CommonAppForPersonRepository;
import net.risesoft.y9public.repository.Y9LogAccessLogRepository;

/**
 * 个人常用应用
 *
 * @author mengjuhua
 *
 */
@Component
@RequiredArgsConstructor
public class Y9CommonAppForPersonCustomRepositoryImpl implements Y9CommonAppForPersonCustomRepository {

    private final Y9ClickedAppRepository y9ClickedAppRepository;

    private final Y9LogAccessLogRepository y9logAccessLogRepository;
    private final Y9CommonAppForPersonRepository commonAppForPersonRepository;

    @Override
    public List<String> getAppNamesByPersonId(String personId) {
        return y9ClickedAppRepository.findByTenantIdAndPersonId(Y9LoginUserHolder.getTenantId(), personId)
            .stream()
            .map(Y9ClickedApp::getAppName)
            .collect(Collectors.toList());
    }

    @Override
    public long getCount() {
        // 最近半年
        Calendar c = Calendar.getInstance();
        Date endTime = c.getTime();
        c.add(Calendar.MONTH, -6);
        Date startTime = c.getTime();
        return y9logAccessLogRepository.countByMethodNameAndLogTimeBetween(Y9LogSearchConsts.APP_METHODNAME, startTime,
            endTime);
    }

    @Override
    public Y9CommonAppForPersonDO findByPersonId(String personId) {
        Y9CommonAppForPerson y9CommonAppForPerson = commonAppForPersonRepository.findByPersonId(personId);
        return Y9ModelConvertUtil.convert(y9CommonAppForPerson, Y9CommonAppForPersonDO.class);
    }

    @Override
    public void save(Y9CommonAppForPersonDO cafp) {
        Y9CommonAppForPerson y9CommonAppForPerson = Y9ModelConvertUtil.convert(cafp, Y9CommonAppForPerson.class);
        commonAppForPersonRepository.save(y9CommonAppForPerson);
    }
}
