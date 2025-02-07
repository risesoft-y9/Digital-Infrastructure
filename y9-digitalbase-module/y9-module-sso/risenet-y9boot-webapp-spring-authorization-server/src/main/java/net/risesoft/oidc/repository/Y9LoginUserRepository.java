package net.risesoft.oidc.repository;

import net.risesoft.oidc.entity.Y9LoginUser;
import net.risesoft.oidc.entity.Y9User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface Y9LoginUserRepository extends JpaRepository<Y9LoginUser, String> {



}
