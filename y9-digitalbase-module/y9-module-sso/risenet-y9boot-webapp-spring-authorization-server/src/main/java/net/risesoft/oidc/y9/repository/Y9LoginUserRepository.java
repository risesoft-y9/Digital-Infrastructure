package net.risesoft.oidc.y9.repository;

import net.risesoft.oidc.y9.entity.Y9LoginUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Y9LoginUserRepository extends JpaRepository<Y9LoginUser, String> {



}
