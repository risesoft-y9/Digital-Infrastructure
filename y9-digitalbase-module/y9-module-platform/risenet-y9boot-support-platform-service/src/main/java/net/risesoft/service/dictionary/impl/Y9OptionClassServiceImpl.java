package net.risesoft.service.dictionary.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.dictionary.Y9OptionClass;
import net.risesoft.enums.AuditLogEnum;
import net.risesoft.exception.DictionaryErrorCodeEnum;
import net.risesoft.manager.dictionary.Y9OptionValueManager;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.repository.dictionary.Y9OptionClassRepository;
import net.risesoft.service.dictionary.Y9OptionClassService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9.util.Y9StringUtil;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service
@RequiredArgsConstructor
public class Y9OptionClassServiceImpl implements Y9OptionClassService {

    private final Y9OptionClassRepository y9OptionClassRepository;

    private final Y9OptionValueManager y9OptionValueManager;

    @Override
    @Transactional
    public void deleteByType(String type) {
        Y9OptionClass y9OptionClass = getById(type);
        y9OptionClassRepository.deleteById(type);
        y9OptionValueManager.deleteByType(type);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.DICTIONARY_TYPE_DELETE.getAction())
            .description(
                Y9StringUtil.format(AuditLogEnum.DICTIONARY_TYPE_DELETE.getDescription(), y9OptionClass.getName()))
            .objectId(type)
            .oldObject(y9OptionClass)
            .currentObject(null)
            .build();
        Y9Context.publishEvent(auditLogEvent);
    }

    private Y9OptionClass getById(String type) {
        return y9OptionClassRepository.findById(type)
            .orElseThrow(
                () -> Y9ExceptionUtil.notFoundException(DictionaryErrorCodeEnum.DICTIONARY_TYPE_NOT_FOUND, type));
    }

    @Override
    public Optional<Y9OptionClass> findByType(String type) {
        return y9OptionClassRepository.findById(type);
    }

    @Override
    public boolean hasData() {
        long count = y9OptionClassRepository.count();
        return count > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Y9OptionClass> list() {
        return y9OptionClassRepository.findAll();
    }

    @Override
    @Transactional
    public Y9OptionClass saveOptionClass(Y9OptionClass optionClass) {
        Optional<Y9OptionClass> y9OptionClassOptional = y9OptionClassRepository.findById(optionClass.getType());
        if (y9OptionClassOptional.isPresent()) {
            Y9OptionClass currentOptionClass = y9OptionClassOptional.get();
            Y9OptionClass originalOptionClass = Y9ModelConvertUtil.convert(currentOptionClass, Y9OptionClass.class);
            Y9BeanUtil.copyProperties(optionClass, currentOptionClass);

            Y9OptionClass savedOptionClass = y9OptionClassRepository.save(currentOptionClass);

            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(AuditLogEnum.DICTIONARY_TYPE_UPDATE.getAction())
                .description(Y9StringUtil.format(AuditLogEnum.DICTIONARY_TYPE_UPDATE.getDescription(),
                    savedOptionClass.getName()))
                .objectId(savedOptionClass.getType())
                .oldObject(originalOptionClass)
                .currentObject(savedOptionClass)
                .build();
            Y9Context.publishEvent(auditLogEvent);

            return savedOptionClass;
        }

        Y9OptionClass savedOptionClass = y9OptionClassRepository.save(optionClass);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.DICTIONARY_TYPE_CREATE.getAction())
            .description(
                Y9StringUtil.format(AuditLogEnum.DICTIONARY_TYPE_CREATE.getDescription(), savedOptionClass.getName()))
            .objectId(savedOptionClass.getType())
            .oldObject(null)
            .currentObject(savedOptionClass)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return savedOptionClass;
    }
}
