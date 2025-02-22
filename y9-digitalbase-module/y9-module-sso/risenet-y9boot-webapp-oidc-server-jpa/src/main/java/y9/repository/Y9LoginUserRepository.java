package y9.repository;

import y9.entity.Y9LoginUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Repository
public interface Y9LoginUserRepository extends JpaRepository<Y9LoginUser, String> {

}
