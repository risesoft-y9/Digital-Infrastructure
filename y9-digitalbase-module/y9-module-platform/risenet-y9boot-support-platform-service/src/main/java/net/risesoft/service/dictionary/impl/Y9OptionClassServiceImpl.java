package net.risesoft.service.dictionary.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9OptionClass;
import net.risesoft.manager.dictionary.Y9OptionValueManager;
import net.risesoft.repository.Y9OptionClassRepository;
import net.risesoft.service.dictionary.Y9OptionClassService;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service
@RequiredArgsConstructor
public class Y9OptionClassServiceImpl implements Y9OptionClassService {

    private final Y9OptionClassRepository y9OptionClassRepository;

    private final Y9OptionValueManager y9OptionValueManager;

    @Override
    @Transactional(readOnly = false)
    public void deleteByType(String type) {
        y9OptionValueManager.deleteByType(type);
        y9OptionClassRepository.deleteById(type);
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
    @Transactional(readOnly = false)
    public Y9OptionClass saveOptionClass(Y9OptionClass optionClass) {
        return y9OptionClassRepository.save(optionClass);
    }
}
