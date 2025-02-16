package net.risesoft.oidc.y9.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.oidc.y9.entity.Y9KeyValue;
import net.risesoft.oidc.y9.repository.Y9KeyValueRepository;
import net.risesoft.oidc.y9.service.Y9KeyValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionOperations;

import java.util.Date;
import java.util.Optional;

@Service("y9KeyValueService")
@Slf4j
@RequiredArgsConstructor
public class Y9KeyValueServiceImpl implements Y9KeyValueService {

    @Autowired
    private final Y9KeyValueRepository y9KeyValueRepository;

    @Override
    public void cleanUpExpiredKeyValue() {
        int deletedCount = y9KeyValueRepository.deleteItem(new Date());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Cleaned up " + deletedCount + " expired Y9KeyValue");
        }
    }

    @Override
    public String get(String key) {
        Optional<Y9KeyValue> result = y9KeyValueRepository.getByKey(key);
        if(result.isPresent()) {
            if(result.get().getExpireTime().after(new Date())){
                return result.get().getValue();
            }
        }

        // 查不到（可能被清除了）或超过过期时间都返回 null
        return null;
    }

    @Override
    public void put(String key, String value, long minutes) {
        Y9KeyValue y9KeyValue = new Y9KeyValue();
        y9KeyValue.setKey(key);
        y9KeyValue.setValue(value);
        y9KeyValue.setExpireTime(new Date(System.currentTimeMillis() + minutes * 1000 * 60));
        y9KeyValueRepository.save(y9KeyValue);
    }
}
