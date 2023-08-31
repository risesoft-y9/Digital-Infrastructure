package net.risesoft.service.dictionary.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9OptionValue;
import net.risesoft.manager.dictionary.Y9OptionValueManager;
import net.risesoft.repository.Y9OptionValueRepository;
import net.risesoft.service.dictionary.Y9OptionValueService;

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

    private final Y9OptionValueRepository orgOptionValueRepository;
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
            orgOptionValueRepository.deleteById(id);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteByType(String type) {
        y9OptionValueManager.deleteByType(type);
    }

    @Override
    public List<Y9OptionValue> listByType(String type) {
        return orgOptionValueRepository.findByType(type);
    }

    @Override
    public Page<Y9OptionValue> pageByType(int page, int rows, String type) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, Sort.by(Sort.Direction.ASC, "tabIndex"));
        return orgOptionValueRepository.findPageByType(type, pageable);
    }

    @Override
    public Page<Y9OptionValue> pageByTypeAndNameLike(int page, int rows, String type, String name) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, Sort.by(Sort.Direction.ASC, "tabIndex"));
        return orgOptionValueRepository.findPageByTypeAndNameContaining(type, name, pageable);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9OptionValue saveOptionValue(Y9OptionValue optionValue) {
        Integer maxTabIndex = y9OptionValueManager.getMaxTabIndexByType(optionValue.getType());
        optionValue.setTabIndex(maxTabIndex == null ? 0 : maxTabIndex + 1);
        return orgOptionValueRepository.save(optionValue);
    }
}
