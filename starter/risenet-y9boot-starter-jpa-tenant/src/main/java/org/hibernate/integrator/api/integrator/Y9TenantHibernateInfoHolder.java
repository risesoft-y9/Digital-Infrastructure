package org.hibernate.integrator.api.integrator;

import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.springframework.core.env.Environment;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Y9TenantHibernateInfoHolder {

    private static Metadata metadata;

    private static SessionFactoryImplementor sessionFactory;

    private static SessionFactoryServiceRegistry serviceRegistry;

    public static Metadata getMetadata() {
        return metadata;
    }

    public static SessionFactoryServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    public static SessionFactoryImplementor getSessionFactory() {
        return sessionFactory;
    }

    public static void schemaUpdate(Environment env) {
    	/*
        try {
            String systemName = env.getProperty("y9.systemName");
            String ddlAuto1 = env.getProperty("spring.jpa.hibernate.ddl-auto");
            String ddlAuto2 = env.getProperty("spring.jpa.properties.hibernate.hbm2ddl.auto");
            if (("update".equals(ddlAuto2) || "update".equals(ddlAuto1)) && null != metadata) {
                SessionFactoryImplementor sessionFactory = Y9TenantHibernateInfoHolder.getSessionFactory();
                StandardServiceRegistry standardServiceRegistry =
                    new StandardServiceRegistryBuilder().applySettings(sessionFactory.getProperties()).build();
                MetadataSources sources = new MetadataSources(standardServiceRegistry);
                new Reflections("net.risesoft.entity").getTypesAnnotatedWith(Entity.class)
                    .forEach(sources::addAnnotatedClass);
                Metadata metadata = sources.buildMetadata();

                String rootPath = env.getProperty("java.io.tmpdir");
                if (Y9Context.getServletContext() != null) {
                    rootPath = Y9Context.getWebRootRealPath();
                }

                EnumSet<TargetType> targetTypes = EnumSet.of(TargetType.DATABASE, TargetType.SCRIPT, TargetType.STDOUT);
                SchemaUpdate schemaUpdate = new SchemaUpdate();
                schemaUpdate.setOutputFile(rootPath + File.separator + systemName + "-update.sql");
                schemaUpdate.setOverrideOutputFileContent();
                schemaUpdate.execute(targetTypes, metadata);

                targetTypes = EnumSet.of(TargetType.SCRIPT, TargetType.STDOUT);
                SchemaExport schemaExport = new SchemaExport();
                schemaExport.setOverrideOutputFileContent();
                schemaExport.setOutputFile(rootPath + File.separator + systemName + "-all.sql");
                schemaExport.execute(targetTypes, Action.CREATE, Y9TenantHibernateInfoHolder.getMetadata(),
                    Y9TenantHibernateInfoHolder.getServiceRegistry());
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        */
    }

    public static void setMetadata(Metadata metadata) {
        Y9TenantHibernateInfoHolder.metadata = metadata;
    }

    public static void setServiceRegistry(SessionFactoryServiceRegistry serviceRegistry) {
        Y9TenantHibernateInfoHolder.serviceRegistry = serviceRegistry;
    }

    public static void setSessionFactory(SessionFactoryImplementor sessionFactory) {
        Y9TenantHibernateInfoHolder.sessionFactory = sessionFactory;
    }
}
