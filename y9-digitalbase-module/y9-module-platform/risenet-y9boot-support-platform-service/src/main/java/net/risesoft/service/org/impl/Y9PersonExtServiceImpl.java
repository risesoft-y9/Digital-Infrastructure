package net.risesoft.service.org.impl;

import java.util.Base64;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.org.Y9Person;
import net.risesoft.entity.org.Y9PersonExt;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.manager.org.Y9PersonExtManager;
import net.risesoft.manager.org.Y9PersonManager;
import net.risesoft.repository.org.Y9PersonExtRepository;
import net.risesoft.service.org.Y9PersonExtService;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service
@RequiredArgsConstructor
public class Y9PersonExtServiceImpl implements Y9PersonExtService {

    private final Y9PersonExtRepository y9PersonExtRepository;

    private final Y9PersonExtManager y9PersonExtManager;
    private final Y9PersonManager y9PersonManager;

    @Override
    public Optional<Y9PersonExt> findByPersonId(String personId) {
        return y9PersonExtRepository.findByPersonId(personId);
    }

    @Override
    public Y9PersonExt getById(String id) {
        return y9PersonExtRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(OrgUnitErrorCodeEnum.PERSON_EXT_NOT_FOUND, id));
    }

    @Override
    public Y9PersonExt getByPersonId(String personId) {
        return y9PersonExtRepository.findByPersonId(personId)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(OrgUnitErrorCodeEnum.PERSON_EXT_NOT_FOUND, personId));
    }

    @Override
    public String getEncodePhotoByPersonId(String personId) {
        Optional<Y9PersonExt> ext = y9PersonExtRepository.findByPersonId(personId);
        return ext.map(y9PersonExt -> Base64.getEncoder().encodeToString(y9PersonExt.getPhoto())).orElse("");
    }

    @Override
    public byte[] getPhotoByPersonId(String personId) {
        return y9PersonExtRepository.findByPersonId(personId).map(Y9PersonExt::getPhoto).orElse(new byte[0]);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9PersonExt saveOrUpdate(Y9PersonExt y9PersonExt, Y9Person person) {
        return y9PersonExtManager.saveOrUpdate(y9PersonExt, person);
    }

    @Override
    public Y9PersonExt savePersonPhoto(String personId, String photo) {
        Y9Person y9Person = y9PersonManager.getById(personId);
        return this.savePersonPhoto(y9Person, photo);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9PersonExt savePersonPhoto(Y9Person person, byte[] photo) {
        Y9PersonExt ext;
        Optional<Y9PersonExt> optionalY9PersonExt = y9PersonExtRepository.findByPersonId(person.getId());
        if (optionalY9PersonExt.isPresent()) {
            ext = optionalY9PersonExt.get();
            ext.setPhoto(photo);
            return y9PersonExtRepository.save(ext);
        }
        ext = new Y9PersonExt();
        ext.setName(person.getName());
        ext.setPersonId(person.getId());
        ext.setPhoto(photo);
        return y9PersonExtRepository.save(ext);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9PersonExt savePersonPhoto(Y9Person person, String photo) {
        byte[] p = new byte[0];
        if (StringUtils.isNotBlank(photo)) {
            p = Base64.getDecoder().decode(photo);
        }
        Y9PersonExt ext;
        Optional<Y9PersonExt> optionalY9PersonExt = y9PersonExtRepository.findByPersonId(person.getId());
        if (optionalY9PersonExt.isPresent()) {
            ext = optionalY9PersonExt.get();
            ext.setPhoto(p);
            return y9PersonExtRepository.save(ext);
        }
        ext = new Y9PersonExt();
        ext.setName(person.getName());
        ext.setPersonId(person.getId());
        ext.setPhoto(p);
        return y9PersonExtRepository.save(ext);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9PersonExt savePersonSign(Y9Person person, byte[] sign) {
        Y9PersonExt ext;
        Optional<Y9PersonExt> optionalY9PersonExt = y9PersonExtRepository.findByPersonId(person.getId());
        if (optionalY9PersonExt.isPresent()) {
            ext = optionalY9PersonExt.get();
            ext.setSign(sign);
            return y9PersonExtRepository.save(ext);
        }
        ext = new Y9PersonExt();
        ext.setName(person.getName());
        ext.setPersonId(person.getId());
        ext.setSign(sign);
        return y9PersonExtRepository.save(ext);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9PersonExt savePersonSign(Y9Person person, String sign) {
        byte[] s = new byte[0];
        if (StringUtils.isNotBlank(sign)) {
            s = Base64.getDecoder().decode(sign);
        }
        Y9PersonExt ext;
        Optional<Y9PersonExt> optionalY9PersonExt = y9PersonExtRepository.findByPersonId(person.getId());
        if (optionalY9PersonExt.isPresent()) {
            ext = optionalY9PersonExt.get();
            ext.setSign(s);
            return y9PersonExtRepository.save(ext);
        }
        ext = new Y9PersonExt();
        ext.setName(person.getName());
        ext.setPersonId(person.getId());
        ext.setSign(s);
        return y9PersonExtRepository.save(ext);
    }
}
