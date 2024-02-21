package net.risesoft.manager.dictionary.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9OptionValue;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.dictionary.Y9OptionValueManager;
import net.risesoft.repository.Y9OptionValueRepository;

/**
 * 字典数据 Manager 实现类
 *
 * @author shidaobang
 * @date 2023/06/13
 * @since 9.6.2
 */
@Service
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@RequiredArgsConstructor
public class Y9OptionValueManagerImpl implements Y9OptionValueManager {

    private final Y9OptionValueRepository orgOptionValueRepository;

    @Override
    @Transactional(readOnly = false)
    public Y9OptionValue create(String code, String name, String type) {
        Optional<Y9OptionValue> optionalY9OptionValue = orgOptionValueRepository.findByTypeAndName(type, name);
        if (optionalY9OptionValue.isEmpty()) {
            Y9OptionValue optionValue = new Y9OptionValue();
            Integer maxTabIndex = getMaxTabIndexByType(optionValue.getType());
            optionValue.setTabIndex(maxTabIndex == null ? 0 : maxTabIndex + 1);
            optionValue.setId(Y9IdGenerator.genId());
            optionValue.setType(type);
            optionValue.setName(name);
            optionValue.setCode(code);
            return orgOptionValueRepository.save(optionValue);
        }
        return optionalY9OptionValue.get();
    }

    @Override
    public Integer getMaxTabIndexByType(String type) {
        return orgOptionValueRepository.findTopByType(type).map(Y9OptionValue::getTabIndex).orElse(0);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteByType(String type) {
        orgOptionValueRepository.deleteByType(type);
    }

}
