package y9.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import y9.entity.Y9LoginUser;

@Transactional(readOnly = true, transactionManager = "rsPublicTransactionManager")
@Repository
public interface Y9LoginUserRepository extends JpaRepository<Y9LoginUser, String> {

}
