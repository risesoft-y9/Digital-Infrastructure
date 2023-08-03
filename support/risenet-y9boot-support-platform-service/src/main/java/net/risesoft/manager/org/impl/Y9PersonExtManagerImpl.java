package net.risesoft.manager.org.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9PersonExt;
import net.risesoft.manager.org.Y9PersonExtManager;
import net.risesoft.repository.Y9PersonExtRepository;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 *
 * @author shidaobang
 * @date 2022/10/19
 */
@Service
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@RequiredArgsConstructor
public class Y9PersonExtManagerImpl implements Y9PersonExtManager {

    private final Y9PersonExtRepository y9PersonExtRepository;

    @Override
    public Y9PersonExt findByPersonId(String personId) {
        return y9PersonExtRepository.findByPersonId(personId);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9PersonExt saveOrUpdate(Y9PersonExt y9PersonExt, Y9Person person) {
        Y9PersonExt oldext = y9PersonExtRepository.findByPersonId(person.getId());
        if (null != oldext) {
            Y9BeanUtil.copyProperties(y9PersonExt, oldext, "photo");
            return y9PersonExtRepository.save(oldext);
        }
        y9PersonExt.setMaritalStatus(y9PersonExt.getMaritalStatus() == null ? 0 : y9PersonExt.getMaritalStatus());
        y9PersonExt.setName(person.getName());
        y9PersonExt.setPersonId(person.getId());

        return y9PersonExtRepository.save(y9PersonExt);
    }

    @Override
    public List<Y9PersonExt> listByIdTypeAndIdNum(String idType, String idNum) {
        return y9PersonExtRepository.findByIdTypeAndIdNum(idType, idNum);
    }
}
