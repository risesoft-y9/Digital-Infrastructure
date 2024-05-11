package net.risesoft.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.service.Y9CommonAppForPersonService;
import net.risesoft.y9public.entity.Y9CommonAppForPerson;
import net.risesoft.y9public.repository.Y9CommonAppForPersonRepository;
import net.risesoft.y9public.repository.custom.Y9CommonAppForPersonCustomRepository;

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

    private final Y9CommonAppForPersonRepository commonAppForPersonRepository;
    private final Y9CommonAppForPersonCustomRepository y9CommonAppForPersonCustomRepository;

    @Override
    public List<String> getAppNamesByPersonId(String personId) {
        return y9CommonAppForPersonCustomRepository.getAppNamesByPersonId(personId);
    }

    @Override
    public Y9CommonAppForPerson getCommonAppForPersonByPersonId(String personId) {
        return commonAppForPersonRepository.findByPersonId(personId);
    }

    @Override
    public long getCount() {
        return y9CommonAppForPersonCustomRepository.getCount();
    }

    @Override
    public void saveOrUpdate(Y9CommonAppForPerson cafp) {
        commonAppForPersonRepository.save(cafp);
    }

}
