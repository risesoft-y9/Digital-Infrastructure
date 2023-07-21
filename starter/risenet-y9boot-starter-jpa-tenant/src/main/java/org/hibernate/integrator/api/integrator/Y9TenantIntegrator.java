package org.hibernate.integrator.api.integrator;

import java.util.Collection;

import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

public class Y9TenantIntegrator implements org.hibernate.integrator.spi.Integrator {

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {}

    @Override
    public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        Collection<PersistentClass> classes = metadata.getEntityBindings();
        if (classes.size() > 0) {
            String name = (String)sessionFactory.getProperties().get("hibernate.persistenceUnitName");
            if ("y9Tenant".equals(name)) {
                Y9TenantHibernateInfoHolder.setMetadata(metadata);
                Y9TenantHibernateInfoHolder.setSessionFactory(sessionFactory);
                Y9TenantHibernateInfoHolder.setServiceRegistry(serviceRegistry);
            }
        }
    }
}