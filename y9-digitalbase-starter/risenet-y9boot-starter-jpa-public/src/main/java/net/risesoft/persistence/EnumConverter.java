package net.risesoft.persistence;

import net.risesoft.enums.FileStoreTypeEnum;
import net.risesoft.enums.platform.DataSourceTypeEnum;
import net.risesoft.enums.platform.RoleTypeEnum;
import net.risesoft.enums.platform.SqlFileTypeEnum;
import net.risesoft.enums.platform.org.DepartmentPropCategoryEnum;
import net.risesoft.enums.platform.org.GroupTypeEnum;
import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.enums.platform.org.MaritalStatusEnum;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.enums.platform.org.PersonTypeEnum;
import net.risesoft.enums.platform.org.SexEnum;
import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.enums.platform.permission.AuthorizationPrincipalTypeEnum;
import net.risesoft.enums.platform.resource.AppOpenTypeEnum;
import net.risesoft.enums.platform.resource.AppTypeEnum;
import net.risesoft.enums.platform.resource.DataCatalogTypeEnum;
import net.risesoft.enums.platform.resource.OperationDisplayTypeEnum;
import net.risesoft.enums.platform.resource.ResourceTypeEnum;

/**
 * @author shidaobang
 * @date 2023/11/15
 * @since 9.6.3
 */
public class EnumConverter {
    public static class DataCatalogTypeEnumConverter extends AbstractEnumConverter<DataCatalogTypeEnum, String> {
        public DataCatalogTypeEnumConverter() {
            super(DataCatalogTypeEnum.class);
        }
    }

    public static class ResourceTypeEnumConverter extends AbstractEnumConverter<ResourceTypeEnum, Integer> {
        public ResourceTypeEnumConverter() {
            super(ResourceTypeEnum.class);
        }
    }

    public static class RoleTypeEnumConverter extends AbstractEnumConverter<RoleTypeEnum, String> {
        public RoleTypeEnumConverter() {
            super(RoleTypeEnum.class);
        }
    }

    public static class SexEnumConverter extends AbstractEnumConverter<SexEnum, Integer> {
        public SexEnumConverter() {
            super(SexEnum.class);
        }
    }

    public static class SqlFileTypeEnumConverter extends AbstractEnumConverter<SqlFileTypeEnum, String> {
        public SqlFileTypeEnumConverter() {
            super(SqlFileTypeEnum.class);
        }
    }

    public static class PersonTypeEnumConverter extends AbstractEnumConverter<PersonTypeEnum, String> {
        public PersonTypeEnumConverter() {
            super(PersonTypeEnum.class);
        }
    }

    public static class OrgTypeEnumConverter extends AbstractEnumConverter<OrgTypeEnum, String> {
        public OrgTypeEnumConverter() {
            super(OrgTypeEnum.class);
        }
    }

    public static class ManagerLevelEnumConverter extends AbstractEnumConverter<ManagerLevelEnum, Integer> {
        public ManagerLevelEnumConverter() {
            super(ManagerLevelEnum.class);
        }
    }

    public static class MaritalStatusEnumConverter extends AbstractEnumConverter<MaritalStatusEnum, Integer> {
        public MaritalStatusEnumConverter() {
            super(MaritalStatusEnum.class);
        }
    }

    public static class GroupTypeEnumConverter extends AbstractEnumConverter<GroupTypeEnum, String> {
        public GroupTypeEnumConverter() {
            super(GroupTypeEnum.class);
        }
    }

    public static class FileStoreTypeEnumConverter extends AbstractEnumConverter<FileStoreTypeEnum, Integer> {
        public FileStoreTypeEnumConverter() {
            super(FileStoreTypeEnum.class);
        }
    }

    public static class DataSourceTypeEnumConverter extends AbstractEnumConverter<DataSourceTypeEnum, Integer> {
        public DataSourceTypeEnumConverter() {
            super(DataSourceTypeEnum.class);
        }
    }

    public static class AuthorityEnumConverter extends AbstractEnumConverter<AuthorityEnum, Integer> {
        public AuthorityEnumConverter() {
            super(AuthorityEnum.class);
        }
    }

    public static class AppTypeEnumConverter extends AbstractEnumConverter<AppTypeEnum, Integer> {
        public AppTypeEnumConverter() {
            super(AppTypeEnum.class);
        }
    }

    public static class AppOpenTypeEnumConverter extends AbstractEnumConverter<AppOpenTypeEnum, Integer> {
        public AppOpenTypeEnumConverter() {
            super(AppOpenTypeEnum.class);
        }
    }

    public static class OperationDisplayTypeEnumConverter
        extends AbstractEnumConverter<OperationDisplayTypeEnum, Integer> {
        public OperationDisplayTypeEnumConverter() {
            super(OperationDisplayTypeEnum.class);
        }
    }

    public static class AuthorizationPrincipalTypeEnumConverter
        extends AbstractEnumConverter<AuthorizationPrincipalTypeEnum, Integer> {
        public AuthorizationPrincipalTypeEnumConverter() {
            super(AuthorizationPrincipalTypeEnum.class);
        }
    }

    public static class DepartmentPropCategoryEnumConverter
        extends AbstractEnumConverter<DepartmentPropCategoryEnum, Integer> {
        public DepartmentPropCategoryEnumConverter() {
            super(DepartmentPropCategoryEnum.class);
        }
    }
}
