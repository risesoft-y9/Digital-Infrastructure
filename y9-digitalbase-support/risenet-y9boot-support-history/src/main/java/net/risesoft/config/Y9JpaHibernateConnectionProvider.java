package net.risesoft.config;

import java.sql.Connection;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.hibernate.engine.spi.SessionImplementor;
import org.javers.repository.sql.ConnectionProvider;

public class Y9JpaHibernateConnectionProvider implements ConnectionProvider {

    @PersistenceContext(unitName = "y9Public")
    private EntityManager entityManager;

    @Override
    public Connection getConnection() {

        SessionImplementor session = entityManager.unwrap(SessionImplementor.class);

        return null;
    }

}
