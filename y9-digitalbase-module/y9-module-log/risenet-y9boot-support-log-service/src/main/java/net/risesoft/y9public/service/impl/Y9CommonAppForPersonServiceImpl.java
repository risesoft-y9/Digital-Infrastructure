package net.risesoft.y9public.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.log.domain.Y9CommonAppForPersonDO;
import net.risesoft.log.repository.Y9CommonAppForPersonCustomRepository;
import net.risesoft.y9public.service.Y9CommonAppForPersonService;

/**
 * 
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
@Service
@RequiredArgsConstructor
public class Y9CommonAppForPersonServiceImpl implements Y9CommonAppForPersonService {

    private final Y9CommonAppForPersonCustomRepository y9CommonAppForPersonCustomRepository;

    @Override
    public List<String> getAppNamesByPersonId(String personId) {
        return y9CommonAppForPersonCustomRepository.getAppNamesByPersonId(personId);
    }

    @Override
    public Y9CommonAppForPersonDO getCommonAppForPersonByPersonId(String personId) {
        return y9CommonAppForPersonCustomRepository.findByPersonId(personId);
    }

    @Override
    public long getCount() {
        return y9CommonAppForPersonCustomRepository.getCount();
    }

    @Override
    public void saveOrUpdate(Y9CommonAppForPersonDO cafp) {
        y9CommonAppForPersonCustomRepository.save(cafp);
    }

}
