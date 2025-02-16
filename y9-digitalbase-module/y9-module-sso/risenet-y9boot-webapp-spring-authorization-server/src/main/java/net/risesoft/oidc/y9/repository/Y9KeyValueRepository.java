package net.risesoft.oidc.y9.repository;

import net.risesoft.oidc.y9.entity.Y9KeyValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface Y9KeyValueRepository extends JpaRepository<Y9KeyValue, String> {

    @Modifying
    @Query("delete from Y9KeyValue t where t.expireTime < :date ")
    int deleteItem(@Param("date") Date date);

    Optional<Y9KeyValue> getByKey(String key);
}
