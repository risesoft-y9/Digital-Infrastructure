package y9.service.impl;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import y9.entity.Y9KeyValue;
import y9.repository.Y9KeyValueRepository;
import y9.service.Y9KeyValueService;

@Service("y9KeyValueService")
@Slf4j
@RequiredArgsConstructor
public class Y9KeyValueServiceImpl implements Y9KeyValueService {

    private final Y9KeyValueRepository y9KeyValueRepository;

    @Override
    public void cleanUpExpiredKeyValue() {
        int deletedCount = y9KeyValueRepository.deleteItem(Instant.now());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Cleaned up " + deletedCount + " expired Y9KeyValue");
        }
    }

    @Override
    public String get(String key) {
        Optional<Y9KeyValue> keyValue = y9KeyValueRepository.getByKey(key);
        return keyValue.isPresent() ? keyValue.get().getValue() : "";
    }

    @Override
    public void put(String key, String value, long minutes) {
        Y9KeyValue y9KeyValue = new Y9KeyValue();
        y9KeyValue.setKey(key);
        y9KeyValue.setValue(value);
        y9KeyValue.setExpireTime(Instant.ofEpochSecond(minutes * 60));
        y9KeyValueRepository.save(y9KeyValue);
    }
}
