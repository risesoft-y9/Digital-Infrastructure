package net.risesoft.config;

import java.sql.Connection;
import java.sql.SQLException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.javers.repository.sql.ConnectionProvider;

public class Y9JpaHibernateConnectionProvider implements ConnectionProvider {

    @PersistenceContext(unitName = "y9Public")
    private EntityManager entityManager;

    @Override
    public Connection getConnection() {

        Session session = entityManager.unwrap(Session.class);
        GetConnectionWork connectionWork = new GetConnectionWork();
        session.doWork(connectionWork);

        // TODO that's a dirty hack forced by upgrading to Hibernate 6
        // this method should accept a connection consumer
        return connectionWork.theConnection;
    }

    private class GetConnectionWork implements Work {
        private Connection theConnection;

        @Override
        public void execute(Connection connection) throws SQLException {
            theConnection = connection;
        }

    }
}
