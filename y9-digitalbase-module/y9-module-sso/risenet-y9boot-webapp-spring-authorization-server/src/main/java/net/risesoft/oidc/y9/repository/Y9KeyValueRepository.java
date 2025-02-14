package net.risesoft.oidc.y9.repository;

import net.risesoft.oidc.y9.entity.Y9KeyValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Y9KeyValueRepository extends JpaRepository<Y9KeyValue, String> {


}
