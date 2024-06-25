package liquibase.database.core;

import liquibase.Scope;
import liquibase.database.DatabaseConnection;
import liquibase.exception.DatabaseException;
import liquibase.logging.Logger;

public class KingbaseDatabase extends PostgresDatabase {
    public static final String PRODUCT_NAME = "KingbaseES";
    private static final Logger LOG = Scope.getCurrentScope().getLog(KingbaseDatabase.class);

    @Override
    public String getShortName() {
        return "kingbase";
    }

    @Override
    protected String getDefaultDatabaseProductName() {
        return PRODUCT_NAME;
    }

    @Override
    public Integer getDefaultPort() {
        return 54321;
    }

    @Override
    public String getDefaultDriver(String url) {
        if (url.startsWith("jdbc:kingbase8")) {
            return "com.kingbase8.Driver";
        }
        return null;
    }

    @Override
    public boolean isCorrectDatabaseImplementation(DatabaseConnection conn) throws DatabaseException {
        if (!PRODUCT_NAME.equalsIgnoreCase(conn.getDatabaseProductName())) {
            return false;
        }

        int majorVersion = conn.getDatabaseMajorVersion();
        int minorVersion = conn.getDatabaseMinorVersion();

        if ((majorVersion < MINIMUM_DBMS_MAJOR_VERSION)
            || ((majorVersion == MINIMUM_DBMS_MAJOR_VERSION) && (minorVersion < MINIMUM_DBMS_MINOR_VERSION))) {
            LOG.warning(String.format(
                "Your PostgreSQL software version (%d.%d) seems to indicate that your software is "
                    + "older than %d.%d. This means that you might encounter strange behaviour and "
                    + "incorrect error messages.",
                majorVersion, minorVersion, MINIMUM_DBMS_MAJOR_VERSION, MINIMUM_DBMS_MINOR_VERSION));
            return true;
        }

        return true;
    }
}
