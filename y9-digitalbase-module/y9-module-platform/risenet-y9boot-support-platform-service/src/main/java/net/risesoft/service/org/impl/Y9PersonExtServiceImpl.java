package net.risesoft.service.org.impl;

import java.util.Base64;
import java.util.Optional;

import net.risesoft.util.PlatformModelConvertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.org.Y9PersonExt;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.model.platform.org.Person;
import net.risesoft.model.platform.org.PersonExt;
import net.risesoft.repository.org.Y9PersonExtRepository;
import net.risesoft.service.org.Y9PersonExtService;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service
@RequiredArgsConstructor
public class Y9PersonExtServiceImpl implements Y9PersonExtService {

    private final Y9PersonExtRepository y9PersonExtRepository;

    @Override
    public Optional<PersonExt> findByPersonId(String personId) {
        return y9PersonExtRepository.findByPersonId(personId).map(Y9PersonExtServiceImpl::entityToModel);
    }

    @Override
    public PersonExt getById(String id) {
        return y9PersonExtRepository.findById(id)
            .map(Y9PersonExtServiceImpl::entityToModel)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(OrgUnitErrorCodeEnum.PERSON_EXT_NOT_FOUND, id));
    }

    @Override
    public PersonExt getByPersonId(String personId) {
        Y9PersonExt y9PersonExt = y9PersonExtRepository.findByPersonId(personId)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(OrgUnitErrorCodeEnum.PERSON_EXT_NOT_FOUND, personId));
        return entityToModel(y9PersonExt);
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
    @Transactional
    public PersonExt savePersonPhoto(Person person, byte[] photo) {
        Y9PersonExt ext;
        Optional<Y9PersonExt> optionalY9PersonExt = y9PersonExtRepository.findByPersonId(person.getId());
        if (optionalY9PersonExt.isPresent()) {
            ext = optionalY9PersonExt.get();
            ext.setPhoto(photo);
            return entityToModel(y9PersonExtRepository.save(ext));
        }
        ext = new Y9PersonExt();
        ext.setName(person.getName());
        ext.setPersonId(person.getId());
        ext.setPhoto(photo);
        return entityToModel(y9PersonExtRepository.save(ext));
    }

    @Override
    @Transactional
    public PersonExt savePersonPhoto(Person person, String photo) {
        byte[] p = new byte[0];
        if (StringUtils.isNotBlank(photo)) {
            p = Base64.getDecoder().decode(photo);
        }
        Y9PersonExt ext;
        Optional<Y9PersonExt> optionalY9PersonExt = y9PersonExtRepository.findByPersonId(person.getId());
        if (optionalY9PersonExt.isPresent()) {
            ext = optionalY9PersonExt.get();
            ext.setPhoto(p);
            return entityToModel(y9PersonExtRepository.save(ext));
        }
        ext = new Y9PersonExt();
        ext.setName(person.getName());
        ext.setPersonId(person.getId());
        ext.setPhoto(p);
        return entityToModel(y9PersonExtRepository.save(ext));
    }

    @Override
    @Transactional
    public PersonExt savePersonSign(Person person, byte[] sign) {
        Y9PersonExt ext;
        Optional<Y9PersonExt> optionalY9PersonExt = y9PersonExtRepository.findByPersonId(person.getId());
        if (optionalY9PersonExt.isPresent()) {
            ext = optionalY9PersonExt.get();
            ext.setSign(sign);
            return entityToModel(y9PersonExtRepository.save(ext));
        }
        ext = new Y9PersonExt();
        ext.setName(person.getName());
        ext.setPersonId(person.getId());
        ext.setSign(sign);
        return entityToModel(y9PersonExtRepository.save(ext));
    }

    @Override
    @Transactional
    public PersonExt savePersonSign(Person person, String sign) {
        byte[] s = new byte[0];
        if (StringUtils.isNotBlank(sign)) {
            s = Base64.getDecoder().decode(sign);
        }
        Y9PersonExt ext;
        Optional<Y9PersonExt> optionalY9PersonExt = y9PersonExtRepository.findByPersonId(person.getId());
        if (optionalY9PersonExt.isPresent()) {
            ext = optionalY9PersonExt.get();
            ext.setSign(s);
            return entityToModel(y9PersonExtRepository.save(ext));
        }
        ext = new Y9PersonExt();
        ext.setName(person.getName());
        ext.setPersonId(person.getId());
        ext.setSign(s);
        return entityToModel(y9PersonExtRepository.save(ext));
    }

    private static PersonExt entityToModel(Y9PersonExt y9PersonExt) {
        return PlatformModelConvertUtil.convert(y9PersonExt, PersonExt.class);
    }
}
