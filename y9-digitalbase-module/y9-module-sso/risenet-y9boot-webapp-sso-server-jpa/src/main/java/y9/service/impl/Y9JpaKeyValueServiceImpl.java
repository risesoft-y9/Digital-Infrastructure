package y9.service.impl;

import java.time.Instant;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apereo.cas.services.Y9KeyValue;
import org.springframework.transaction.support.TransactionOperations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import y9.service.Y9KeyValueService;

@RequiredArgsConstructor
@Slf4j
public class Y9JpaKeyValueServiceImpl implements Y9KeyValueService {

	private final TransactionOperations transactionTemplate;

	@PersistenceContext(unitName = "serviceEntityManagerFactory")
	private EntityManager entityManager;

	@Override
	public void cleanUpExpiredKeyValue() {
		int deletedCount = transactionTemplate.execute(status -> {
			Query query = entityManager.createQuery("DELETE FROM Y9KeyValue r WHERE r.expireTime < :now");
			query.setParameter("now", Instant.now());
			return query.executeUpdate();
		});

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Cleaned up " + deletedCount + " expired Y9KeyValue");
		}
	}

	@Override
	public String get(String key) {
		return transactionTemplate.execute(status -> {
			Y9KeyValue result = entityManager.find(Y9KeyValue.class, key);
			if (result != null && result.getExpireTime().isAfter(Instant.now())) {
				return result.getValue();
			}
			// 查不到（可能被清除了）或超过过期时间都返回 null
			return null;
		});
	}

	@Override
	public void put(String key, String value, long minutes) {
		Y9KeyValue y9KeyValue = new Y9KeyValue();
		y9KeyValue.setKey(key);
		y9KeyValue.setValue(value);
		y9KeyValue.setExpireTime(Instant.ofEpochSecond(minutes * 60));
		transactionTemplate.executeWithoutResult(status -> entityManager.persist(y9KeyValue));
	}
}
