package y9.dbcomment;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.jdbc.core.JdbcTemplate;

import lombok.RequiredArgsConstructor;

import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.db.DbType;
import net.risesoft.y9.db.DbUtil;
import net.risesoft.y9.tenant.datasource.Y9TenantDataSource;

/**
 * 刷新数据库注释
 *
 * @author dingzhaojun
 *
 */
@Endpoint(id = "databaseComment")
@RequiredArgsConstructor
public class Y9DatabaseCommentEndpoint {

    private final DataSource y9PublicDs;
    private final Y9TenantDataSource y9TenantDataSource;

    @ReadOperation
    public String databaseComment() {
        if (this.y9PublicDs != null) {
            String scanPackagePublic =
                Y9Context.getProperty("y9.feature.jpa.packagesToScanEntityPublic", "net.risesoft.y9public.entity");
            JdbcTemplate jdbcTemplate4Public = new JdbcTemplate(this.y9PublicDs);
            DbType dbType = DbUtil.getDbType(this.y9PublicDs);
            if (DbType.mysql.equals(dbType)) {
                Y9CommentUtil.scanner4Mysql(jdbcTemplate4Public, scanPackagePublic.split(","));
            } else if (DbType.oracle.equals(dbType)) {
                Y9CommentUtil.scanner4Oracle(jdbcTemplate4Public, scanPackagePublic.split(","));
            }
        }

        if (this.y9TenantDataSource != null) {
            String scanPackageTenant =
                Y9Context.getProperty("y9.feature.jpa.packagesToScanEntityTenant", "net.risesoft.entity");
            JdbcTemplate jdbcTemplate4Tenant = new JdbcTemplate(this.y9TenantDataSource);

            JdbcTemplate jdbcTemplate4Public = new JdbcTemplate(this.y9PublicDs);
            List<String> tenants = jdbcTemplate4Public.queryForList("select id from Y9_COMMON_TENANT", String.class);
            for (String tenantId : tenants) {
                Y9LoginUserHolder.setTenantId(tenantId);
                DataSource ds = this.y9TenantDataSource.determineTargetDataSource();
                DbType dbType = DbUtil.getDbType(ds);
                if (DbType.mysql.equals(dbType)) {
                    Y9CommentUtil.scanner4Mysql(jdbcTemplate4Tenant, scanPackageTenant.split(","));
                } else if (DbType.oracle.equals(dbType)) {
                    Y9CommentUtil.scanner4Oracle(jdbcTemplate4Tenant, scanPackageTenant.split(","));
                }
            }
        }
        return "finished.";
    }

}
