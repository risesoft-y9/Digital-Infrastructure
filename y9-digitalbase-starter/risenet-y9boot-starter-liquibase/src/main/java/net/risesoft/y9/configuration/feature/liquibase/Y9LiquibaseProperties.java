package net.risesoft.y9.configuration.feature.liquibase;

import java.io.File;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * liquibase
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "y9.feature.liquibase", ignoreInvalidFields = true, ignoreUnknownFields = true)
public class Y9LiquibaseProperties {

    /**
     * Change log configuration path for public.
     */
    private String publicChangeLog = "classpath:/liquibase/public/main.xml";

    /**
     * Change log configuration path for tenant.
     */
    private String tenantChangeLog = "classpath:/liquibase/tenant/main.xml";

    /**
     * Whether to clear all checksums in the current changelog, so they will be recalculated upon the next update.
     */
    private boolean clearChecksums;

    /**
     * Comma-separated list of runtime contexts to use.
     */
    private String contexts;

    /**
     * Default database schema.
     */
    private String defaultSchema;

    /**
     * Schema to use for Liquibase objects.
     */
    private String liquibaseSchema;

    /**
     * Tablespace to use for Liquibase objects.
     */
    private String liquibaseTablespace;

    /**
     * Name of table to use for tracking change history.
     */
    private String databaseChangeLogTable = "DATABASECHANGELOG";

    /**
     * Name of table to use for tracking concurrent Liquibase usage.
     */
    private String databaseChangeLogLockTable = "DATABASECHANGELOGLOCK";

    /**
     * Whether to first drop the database schema.
     */
    private boolean dropFirst;

    /**
     * Whether to enable tenant Liquibase support.
     */
    private boolean tenantEnabled = false;

    /**
     * Whether to enable public Liquibase support.
     */
    private boolean publicEnabled = false;

    /**
     * Login user of the database to migrate.
     */
    private String user;

    /**
     * Login password of the database to migrate.
     */
    private String password;

    /**
     * Fully qualified name of the JDBC driver. Auto-detected based on the URL by default.
     */
    private String driverClassName;

    /**
     * JDBC URL of the database to migrate. If not set, the primary configured data source is used.
     */
    private String url;

    /**
     * Comma-separated list of runtime labels to use.
     */
    private String labels;

    /**
     * Change log parameters.
     */
    private Map<String, String> parameters;

    /**
     * File to which rollback SQL is written when an update is performed.
     */
    private File rollbackFile;

    /**
     * Whether rollback should be tested before update is performed.
     */
    private boolean testRollbackOnUpdate;

    /**
     * Tag name to use when applying database changes. Can also be used with "rollbackFile" to generate a rollback
     * script for all existing changes associated with that tag.
     */
    private String tag;
}
