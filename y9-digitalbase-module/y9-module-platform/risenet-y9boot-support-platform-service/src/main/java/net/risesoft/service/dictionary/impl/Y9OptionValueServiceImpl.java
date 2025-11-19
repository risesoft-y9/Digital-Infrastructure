package net.risesoft.service.dictionary.impl;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.dictionary.Y9OptionValue;
import net.risesoft.enums.AuditLogEnum;
import net.risesoft.exception.DictionaryErrorCodeEnum;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.dictionary.Y9OptionValueManager;
import net.risesoft.model.platform.dictionary.OptionValue;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.repository.dictionary.Y9OptionValueRepository;
import net.risesoft.service.dictionary.Y9OptionValueService;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9StringUtil;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service
@RequiredArgsConstructor
public class Y9OptionValueServiceImpl implements Y9OptionValueService {

    private final Y9OptionValueRepository y9OptionValueRepository;
    private final Y9OptionValueManager y9OptionValueManager;

    private static List<OptionValue> entityToModel(List<Y9OptionValue> y9OptionValueList) {
        return PlatformModelConvertUtil.convert(y9OptionValueList, OptionValue.class);
    }

    private static OptionValue entityToModel(Y9OptionValue y9OptionValue) {
        return PlatformModelConvertUtil.convert(y9OptionValue, OptionValue.class);
    }

    private static Y9OptionValue modelToEntity(OptionValue optionValue) {
        return PlatformModelConvertUtil.convert(optionValue, Y9OptionValue.class);
    }

    @Override
    @Transactional
    public OptionValue create(String code, String name, String type) {
        Y9OptionValue y9OptionValue = y9OptionValueManager.create(code, name, type);
        return entityToModel(y9OptionValue);
    }

    @Override
    @Transactional
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
    public List<OptionValue> listByType(String type) {
        List<Y9OptionValue> y9OptionValueList = y9OptionValueRepository.findByType(type);
        return entityToModel(y9OptionValueList);
    }

    @Override
    @Transactional
    public OptionValue saveOptionValue(OptionValue optionValue) {
        if (StringUtils.isNotBlank(optionValue.getId())) {
            Optional<Y9OptionValue> y9OptionValueOptional = y9OptionValueRepository.findById(optionValue.getId());
            if (y9OptionValueOptional.isPresent()) {
                Y9OptionValue currentOptionValue = y9OptionValueOptional.get();
                Y9OptionValue originalOptionValue =
                    PlatformModelConvertUtil.convert(currentOptionValue, Y9OptionValue.class);

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

                return entityToModel(savedOptionValue);
            }
        } else {
            optionValue.setId(Y9IdGenerator.genId());
        }

        optionValue.setTabIndex(this.getNextTabIndex(optionValue));
        Y9OptionValue newOptionValue = modelToEntity(optionValue);
        Y9OptionValue savedOptionValue = y9OptionValueRepository.save(newOptionValue);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.DICTIONARY_VALUE_CREATE.getAction())
            .description(
                Y9StringUtil.format(AuditLogEnum.DICTIONARY_VALUE_CREATE.getDescription(), savedOptionValue.getName()))
            .objectId(savedOptionValue.getId())
            .oldObject(null)
            .currentObject(savedOptionValue)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return entityToModel(savedOptionValue);
    }

    private Integer getNextTabIndex(OptionValue optionValue) {
        Integer maxTabIndex = y9OptionValueManager.getMaxTabIndexByType(optionValue.getType());
        return maxTabIndex == null ? 0 : maxTabIndex + 1;
    }
}
