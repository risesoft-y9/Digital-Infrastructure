package y9.service.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apereo.cas.services.Y9User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionOperations;

import y9.service.Y9UserService;

@Service
public class Y9UserServiceImpl implements Y9UserService {
    private static final String SELECT_QUERY = "SELECT r from Y9User r ";

    @Autowired
    @Qualifier("jdbcServiceRegistryTransactionTemplate")
    private TransactionOperations transactionTemplate;

    @PersistenceContext(unitName = "jpaServiceRegistryContext")
    private EntityManager entityManager;

    @Override
    public Y9User save(Y9User y9User) {
        return transactionTemplate.execute(status -> {
            return entityManager.merge(y9User);
        });
    }

    @Override
    public Y9User findByPersonIdAndTenantId(String id, String tenantId) {
        return transactionTemplate.execute(status -> {
            String sql = SELECT_QUERY.concat("WHERE r.personId = :id and r.tenantId = :tenantId");
            TypedQuery<Y9User> query =
                entityManager.createQuery(sql, Y9User.class).setParameter("id", id).setParameter("tenantId", tenantId);
            return query.getSingleResult();
        });
    }

    @Override
    public List<Y9User> findByLoginNameAndOriginal(String loginName, Boolean original) {
        return transactionTemplate.execute(status -> {
            String sql = SELECT_QUERY.concat("WHERE r.loginName = :loginName AND r.original = :original");
            TypedQuery<Y9User> query = entityManager.createQuery(sql, Y9User.class).setParameter("loginName", loginName)
                .setParameter("original", original);
            return query.getResultList();
        });
    }

    @Override
    public List<Y9User> findByLoginNameContainingAndOriginalOrderByTenantShortName(String loginName, Boolean original) {
        return transactionTemplate.execute(status -> {
            String sql = SELECT_QUERY
                .concat("WHERE r.loginName LIKE :loginName AND r.original = :original ORDER BY r.tenantShortName");
            TypedQuery<Y9User> query = entityManager.createQuery(sql, Y9User.class)
                .setParameter("loginName", '%' + loginName + '%').setParameter("original", original);
            return query.getResultList();
        });
    }

    @Override
    public List<Y9User> findByMobileAndOriginal(String mobile, Boolean original) {
        return transactionTemplate.execute(status -> {
            String sql = SELECT_QUERY.concat("WHERE r.mobile = :mobile AND r.original = :original");
            TypedQuery<Y9User> query = entityManager.createQuery(sql, Y9User.class).setParameter("mobile", mobile)
                .setParameter("original", original);
            return query.getResultList();
        });
    }

    @Override
    public List<Y9User> findByOriginalAndLoginNameStartingWith(Boolean original, String loginName) {
        return transactionTemplate.execute(status -> {
            String sql = SELECT_QUERY.concat("WHERE r.original = :original AND r.loginName LIKE :loginName");
            TypedQuery<Y9User> query = entityManager.createQuery(sql, Y9User.class).setParameter("original", original)
                .setParameter("loginName", loginName + '%');
            return query.getResultList();
        });
    }

    @Override
    public List<Y9User> findByTenantIdAndLoginNameAndOriginal(String tenantId, String loginName, Boolean original) {
        return transactionTemplate.execute(status -> {
            String sql = SELECT_QUERY
                .concat("WHERE r.tenantId = :tenantId AND r.loginName = :loginName AND r.original = :original");
            TypedQuery<Y9User> query = entityManager.createQuery(sql, Y9User.class).setParameter("tenantId", tenantId)
                .setParameter("loginName", loginName).setParameter("original", original);
            return query.getResultList();
        });
    }

    @Override
    public List<Y9User> findByTenantShortNameAndLoginName(String tenantShortName, String loginName) {
        return transactionTemplate.execute(status -> {
            String sql = SELECT_QUERY.concat("WHERE r.tenantShortName=:tenantShortName AND r.loginName = :loginName");
            TypedQuery<Y9User> query = entityManager.createQuery(sql, Y9User.class)
                .setParameter("tenantShortName", tenantShortName).setParameter("loginName", loginName);
            return query.getResultList();
        });
    }

    @Override
    public List<Y9User> findByTenantShortNameAndLoginNameAndOriginal(String tenantShortName, String loginName,
        Boolean original) {
        return transactionTemplate.execute(status -> {
            String sql = SELECT_QUERY.concat(
                "WHERE r.tenantShortName = :tenantShortName AND r.loginName = :loginName AND r.original = :original");
            TypedQuery<Y9User> query =
                entityManager.createQuery(sql, Y9User.class).setParameter("tenantShortName", tenantShortName)
                    .setParameter("loginName", loginName).setParameter("original", original);
            return query.getResultList();
        });
    }

    @Override
    public List<Y9User> findByTenantShortNameAndLoginNameAndParentId(String tenantShortName, String loginName,
        String parentId) {
        return transactionTemplate.execute(status -> {
            String sql = SELECT_QUERY.concat(
                "WHERE r.tenantShortName = :tenantShortName AND r.loginName = :loginName AND r.parentId = :parentId");
            TypedQuery<Y9User> query =
                entityManager.createQuery(sql, Y9User.class).setParameter("tenantShortName", tenantShortName)
                    .setParameter("loginName", loginName).setParameter("parentId", parentId);
            return query.getResultList();
        });
    }

    @Override
    public List<Y9User> findByTenantShortNameAndLoginNameAndPassword(String tenantShortName, String loginName,
        String password) {
        return transactionTemplate.execute(status -> {
            String sql = SELECT_QUERY.concat(
                "WHERE r.tenantShortName = :tenantShortName AND r.loginName = :loginName AND r.password = :password");
            TypedQuery<Y9User> query =
                entityManager.createQuery(sql, Y9User.class).setParameter("tenantShortName", tenantShortName)
                    .setParameter("loginName", loginName).setParameter("password", password);
            return query.getResultList();
        });
    }

    @Override
    public List<Y9User> findByTenantShortNameAndLoginNameAndPasswordAndOriginal(String tenantShortName,
        String loginName, String password, Boolean original) {
        return transactionTemplate.execute(status -> {
            String sql = SELECT_QUERY.concat(
                "WHERE r.tenantShortName = :tenantShortName AND r.loginName = :loginName AND r.password = :password AND r.original = :original");
            TypedQuery<Y9User> query = entityManager.createQuery(sql, Y9User.class)
                .setParameter("tenantShortName", tenantShortName).setParameter("loginName", loginName)
                .setParameter("password", password).setParameter("original", original);
            return query.getResultList();
        });
    }

    @Override
    public List<Y9User> findByTenantShortNameAndMobile(String tenantShortName, String mobile) {
        return transactionTemplate.execute(status -> {
            String sql = SELECT_QUERY.concat("WHERE r.tenantShortName = :tenantShortName AND r.mobile = :mobile");
            TypedQuery<Y9User> query = entityManager.createQuery(sql, Y9User.class)
                .setParameter("tenantShortName", tenantShortName).setParameter("mobile", mobile);
            return query.getResultList();
        });
    }

    @Override
    public List<Y9User> findByTenantShortNameAndMobileAndOriginal(String tenantShortName, String mobile,
        Boolean original) {
        return transactionTemplate.execute(status -> {
            String sql = SELECT_QUERY
                .concat("WHERE r.tenantShortName = :tenantShortName AND r.mobile = :mobile AND r.original = :original");
            TypedQuery<Y9User> query =
                entityManager.createQuery(sql, Y9User.class).setParameter("tenantShortName", tenantShortName)
                    .setParameter("mobile", mobile).setParameter("original", original);
            return query.getResultList();
        });
    }

    @Override
    public List<Y9User> findByTenantShortNameAndMobileAndParentId(String tenantShortName, String mobile,
        String parentId) {
        return transactionTemplate.execute(status -> {
            String sql = SELECT_QUERY
                .concat("WHERE r.tenantShortName=:tenantShortName AND r.mobile=:mobile AND r.parentId=:parentId");
            TypedQuery<Y9User> query =
                entityManager.createQuery(sql, Y9User.class).setParameter("tenantShortName", tenantShortName)
                    .setParameter("mobile", mobile).setParameter("parentId", parentId);
            return query.getResultList();
        });
    }

    @Override
    public List<Y9User> findByTenantShortNameAndMobileAndPassword(String tenantShortName, String mobile,
        String password) {
        return transactionTemplate.execute(status -> {
            String sql = SELECT_QUERY
                .concat("WHERE r.tenantShortName = :tenantShortName AND r.mobile = :mobile AND r.password = :password");
            TypedQuery<Y9User> query =
                entityManager.createQuery(sql, Y9User.class).setParameter("tenantShortName", tenantShortName)
                    .setParameter("mobile", mobile).setParameter("password", password);
            return query.getResultList();
        });
    }

    @Override
    public List<Y9User> findByTenantShortNameNotInAndLoginNameAndOriginal(List<String> tenantlist, String loginName,
        Boolean original) {
        return transactionTemplate.execute(status -> {
            String sql = SELECT_QUERY.concat(
                "WHERE r.tenantShortName not in :tenantlist AND r.loginName = :loginName AND r.original = :original");
            TypedQuery<Y9User> query =
                entityManager.createQuery(sql, Y9User.class).setParameter("tenantlist", tenantlist)
                    .setParameter("loginName", loginName).setParameter("original", original);
            return query.getResultList();
        });
    }

    @Override
    public List<Y9User> findByTenantNameAndLoginNameAndOriginal(String tenantName, String loginName, Boolean original) {
        return transactionTemplate.execute(status -> {
            String sql = SELECT_QUERY
                .concat("WHERE r.tenantName = :tenantName AND r.loginName = :loginName AND r.original = :original");
            TypedQuery<Y9User> query =
                entityManager.createQuery(sql, Y9User.class).setParameter("tenantName", tenantName)
                    .setParameter("loginName", loginName).setParameter("original", original);
            return query.getResultList();
        });
    }

}
