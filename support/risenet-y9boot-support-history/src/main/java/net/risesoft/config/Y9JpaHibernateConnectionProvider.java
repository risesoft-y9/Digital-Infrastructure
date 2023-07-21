package net.risesoft.config;

import java.sql.Connection;

import javax.persistence.EntityManager;

import org.hibernate.engine.spi.SessionImplementor;
import org.javers.repository.sql.ConnectionProvider;

public class Y9JpaHibernateConnectionProvider implements ConnectionProvider {

    private EntityManager entityManager;

    public Y9JpaHibernateConnectionProvider(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Connection getConnection() {

        SessionImplementor session = entityManager.unwrap(SessionImplementor.class);

        return session.connection();
    }

}
