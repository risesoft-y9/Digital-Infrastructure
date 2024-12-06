package net.risesoft.config;

import java.sql.Connection;

import jakarta.persistence.EntityManager;

import org.hibernate.engine.spi.SessionImplementor;
import org.javers.repository.sql.ConnectionProvider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Y9JpaHibernateConnectionProvider implements ConnectionProvider {

    private final EntityManager entityManager;

    @Override
    public Connection getConnection() {

        SessionImplementor session = entityManager.unwrap(SessionImplementor.class);

        return session.connection();
    }

}
