package net.risesoft.service.org.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9PersonExt;
import net.risesoft.exception.PersonErrorCodeEnum;
import net.risesoft.manager.org.Y9PersonExtManager;
import net.risesoft.manager.org.Y9PersonManager;
import net.risesoft.repository.Y9PersonExtRepository;
import net.risesoft.service.org.Y9PersonExtService;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;

import jodd.util.Base64;

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
    public Y9PersonExt findByPersonId(String personId) {
        return y9PersonExtRepository.findByPersonId(personId);
    }

    @Override
    public Y9PersonExt getById(String id) {
        return y9PersonExtRepository.findById(id).orElseThrow(() -> Y9ExceptionUtil.notFoundException(PersonErrorCodeEnum.PERSON_EXT_NOT_FOUND, id));
    }

    @Override
    public String getEncodePhotoByPersonId(String personId) {
        Y9PersonExt ext = y9PersonExtRepository.findByPersonId(personId);
        if (null != ext) {
            return Base64.encodeToString(ext.getPhoto());
        }
        return "";
    }

    @Override
    public byte[] getPhotoByPersonId(String personId) {
        Y9PersonExt ext = y9PersonExtRepository.findByPersonId(personId);
        if (null != ext) {
            return ext.getPhoto();
        }
        return new byte[0];
    }

    @Override
    @Transactional(readOnly = false)
    public Y9PersonExt saveOrUpdate(Y9PersonExt y9PersonExt, Y9Person person) {
        return y9PersonExtManager.saveOrUpdate(y9PersonExt, person);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9PersonExt savePersonPhoto(Y9Person person, byte[] photo) {
        Y9PersonExt ext = y9PersonExtRepository.findByPersonId(person.getId());
        if (null != ext) {
            ext.setPhoto(photo);
            return y9PersonExtRepository.save(ext);
        }
        ext = new Y9PersonExt();
        ext.setName(person.getName());
        ext.setPersonId(person.getId());
        ext.setPhoto(photo);
        ext.setMaritalStatus(ext.getMaritalStatus() == null ? 0 : ext.getMaritalStatus());
        return y9PersonExtRepository.save(ext);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9PersonExt savePersonPhoto(Y9Person person, String photo) {
        byte[] p = new byte[0];
        if (StringUtils.isNotBlank(photo)) {
            p = Base64.decode(photo);
        }
        Y9PersonExt ext = y9PersonExtRepository.findByPersonId(person.getId());
        if (null != ext) {
            ext.setPhoto(p);
            return y9PersonExtRepository.save(ext);
        }
        ext = new Y9PersonExt();
        ext.setName(person.getName());
        ext.setPersonId(person.getId());
        ext.setPhoto(p);
        ext.setMaritalStatus(ext.getMaritalStatus() == null ? 0 : ext.getMaritalStatus());
        return y9PersonExtRepository.save(ext);
    }

    @Override
    public Y9PersonExt savePersonPhoto(String personId, String photo) {
        Y9Person y9Person = y9PersonManager.getById(personId);
        return this.savePersonPhoto(y9Person, photo);
    }
}
