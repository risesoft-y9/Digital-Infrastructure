package net.risesoft.oidc.y9.repository;

import net.risesoft.oidc.y9.entity.Y9Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Y9ThemeRepository extends JpaRepository<Y9Theme, String> {

	@Override
	Optional<Y9Theme> findById(String s);

}
