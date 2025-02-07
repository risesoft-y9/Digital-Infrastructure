package net.risesoft.oidc.repository;

import net.risesoft.oidc.entity.Y9User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface Y9UserRepository extends JpaRepository<Y9User, String> {

	@Query("SELECT u FROM Y9User u WHERE u.loginName = :username")
	Y9User getUserByUsername(@Param("username") String username);


    Y9User findByPersonIdAndTenantId(String personId, String tenantId);
}
