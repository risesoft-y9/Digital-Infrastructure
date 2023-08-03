package net.risesoft.manager.dictionary.impl;

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
        Y9OptionValue optionValue = orgOptionValueRepository.findByTypeAndName(type, name);
        if (optionValue == null) {
            optionValue = new Y9OptionValue();
            Integer maxTabIndex = getMaxTabIndexByType(optionValue.getType());
            optionValue.setTabIndex(maxTabIndex == null ? 0 : maxTabIndex + 1);
            optionValue.setId(Y9IdGenerator.genId());
            optionValue.setType(type);
            optionValue.setName(name);
            optionValue.setCode(code);
            return orgOptionValueRepository.save(optionValue);
        }
        return optionValue;
    }

    @Override
    public Integer getMaxTabIndexByType(String type) {
        Y9OptionValue ov = orgOptionValueRepository.findTopByType(type);
        if (ov != null) {
            return ov.getTabIndex();
        }
        return 0;
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteByType(String type) {
        orgOptionValueRepository.deleteByType(type);
    }

}
