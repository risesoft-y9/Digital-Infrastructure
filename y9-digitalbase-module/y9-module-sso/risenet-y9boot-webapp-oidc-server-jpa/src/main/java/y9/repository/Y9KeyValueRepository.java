package y9.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import y9.entity.Y9KeyValue;

@Transactional(transactionManager = "rsPublicTransactionManager")
@Repository
public interface Y9KeyValueRepository extends JpaRepository<Y9KeyValue, String> {

	@Modifying
	@Query("delete from Y9KeyValue t where t.expireTime < :time ")
	int deleteItem(@Param("time") Instant time);

	Optional<Y9KeyValue> getByKey(String key);
}
