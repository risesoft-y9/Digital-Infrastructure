package net.risesoft.service.dictionary.impl;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9OptionValue;
import net.risesoft.enums.AuditLogEnum;
import net.risesoft.exception.DictionaryErrorCodeEnum;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.dictionary.Y9OptionValueManager;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.repository.Y9OptionValueRepository;
import net.risesoft.service.dictionary.Y9OptionValueService;
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
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service
@RequiredArgsConstructor
public class Y9OptionValueServiceImpl implements Y9OptionValueService {

    private final Y9OptionValueRepository y9OptionValueRepository;
    private final Y9OptionValueManager y9OptionValueManager;

    @Override
    @Transactional(readOnly = false)
    public Y9OptionValue create(String code, String name, String type) {
        return y9OptionValueManager.create(code, name, type);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String[] ids) {
        for (String id : ids) {
            Y9OptionValue y9OptionValue = getById(id);
            y9OptionValueRepository.deleteById(id);

            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(AuditLogEnum.DICTIONARY_VALUE_DELETE.getAction())
                .description(
                    Y9StringUtil.format(AuditLogEnum.DICTIONARY_VALUE_DELETE.getDescription(), y9OptionValue.getName()))
                .objectId(id)
                .oldObject(y9OptionValue)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        }
    }

    private Y9OptionValue getById(String id) {
        return y9OptionValueRepository.findById(id)
            .orElseThrow(
                () -> Y9ExceptionUtil.notFoundException(DictionaryErrorCodeEnum.DICTIONARY_VALUE_NOT_FOUND, id));
    }

    @Override
    public List<Y9OptionValue> listByType(String type) {
        return y9OptionValueRepository.findByType(type);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9OptionValue saveOptionValue(Y9OptionValue optionValue) {
        if (StringUtils.isNotBlank(optionValue.getId())) {
            Optional<Y9OptionValue> y9OptionValueOptional = y9OptionValueRepository.findById(optionValue.getId());
            if (y9OptionValueOptional.isPresent()) {
                Y9OptionValue currentOptionValue = y9OptionValueOptional.get();
                Y9OptionValue originalOptionValue = Y9ModelConvertUtil.convert(currentOptionValue, Y9OptionValue.class);

                Y9BeanUtil.copyProperties(optionValue, currentOptionValue);
                Y9OptionValue savedOptionValue = y9OptionValueRepository.save(currentOptionValue);

                AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                    .action(AuditLogEnum.DICTIONARY_VALUE_UPDATE.getAction())
                    .description(Y9StringUtil.format(AuditLogEnum.DICTIONARY_VALUE_UPDATE.getDescription(),
                        savedOptionValue.getName()))
                    .objectId(savedOptionValue.getId())
                    .oldObject(originalOptionValue)
                    .currentObject(savedOptionValue)
                    .build();
                Y9Context.publishEvent(auditLogEvent);

                return savedOptionValue;
            }
        } else {
            optionValue.setId(Y9IdGenerator.genId());
        }

        optionValue.setTabIndex(this.getNextTabIndex(optionValue));
        Y9OptionValue savedOptionValue = y9OptionValueRepository.save(optionValue);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.DICTIONARY_VALUE_CREATE.getAction())
            .description(
                Y9StringUtil.format(AuditLogEnum.DICTIONARY_VALUE_CREATE.getDescription(), savedOptionValue.getName()))
            .objectId(savedOptionValue.getId())
            .oldObject(null)
            .currentObject(savedOptionValue)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return savedOptionValue;
    }

    private Integer getNextTabIndex(Y9OptionValue optionValue) {
        Integer maxTabIndex = y9OptionValueManager.getMaxTabIndexByType(optionValue.getType());
        return maxTabIndex == null ? 0 : maxTabIndex + 1;
    }
}
