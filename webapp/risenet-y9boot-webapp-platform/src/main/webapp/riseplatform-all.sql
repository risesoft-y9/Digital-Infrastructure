create table Y9_ORG_AUTHORIZATION
(
    ID             varchar(38)       not null comment '主键',
    CREATE_TIME    datetime(6) comment '创建时间',
    UPDATE_TIME    datetime(6) comment '更新时间',
    AUTHORITY      integer           not null comment '权限类型',
    AUTHORIZER     varchar(30) comment '授权人',
    PRINCIPAL_ID   varchar(38)       not null comment '授权主体的唯一标识',
    PRINCIPAL_NAME varchar(255) comment '授权主体的名称。冗余字段，纯显示用',
    PRINCIPAL_TYPE integer default 0 not null comment '授权主体类型:0=角色；1=岗位；2=人员,3=组，4=部门，5=组织',
    RESOURCE_ID    varchar(38)       not null comment '资源唯一标识符',
    RESOURCE_NAME  varchar(255) comment '资源名称。冗余字段，纯显示用',
    RESOURCE_TYPE  integer comment '资源类型。冗余字段，纯显示用',
    TENANT_ID      varchar(38) comment '租户id',
    primary key (ID)
) comment='权限配置表' engine=InnoDB;
create table Y9_ORG_DEPARTMENT
(
    ID              varchar(38)       not null comment 'UUID字段',
    CREATE_TIME     datetime(6) comment '创建时间',
    UPDATE_TIME     datetime(6) comment '更新时间',
    CUSTOM_ID       varchar(255) comment '自定义id',
    DESCRIPTION     varchar(255) comment '描述',
    DISABLED        integer default 0 not null comment '是否可用',
    DN              varchar(2000) comment '由name组成的父子关系列表(倒序)，之间用逗号分隔',
    GUID_PATH       varchar(400) comment '由ID组成的父子关系列表(正序)，之间用逗号分隔',
    NAME            varchar(255)      not null comment '名称',
    ORG_TYPE        varchar(255)      not null comment '组织类型',
    PARENT_ID       varchar(38) comment '父节点id',
    PROPERTIES      varchar(500) comment '扩展属性',
    TAB_INDEX       integer           not null comment '排序号',
    TENANT_ID       varchar(38) comment '租户id',
    VERSION         integer comment '版本号,乐观锁',
    ALIAS_NAME      varchar(255) comment '部门简称',
    BUREAU          integer default 0 not null comment '是否委办局',
    DEPT_ADDRESS    varchar(255) comment '部门地址',
    DEPT_FAX        varchar(255) comment '传真号码',
    DEPT_GIVEN_NAME varchar(255) comment '特定名称',
    DEPT_OFFICE     varchar(255) comment '办公室',
    DEPT_PHONE      varchar(255) comment '电话号码',
    DEPT_TYPE       varchar(255) comment '部门类型',
    DEPT_TYPE_NAME  varchar(255) comment '部门类型名称',
    DIVISION_SCODE  varchar(255) comment '区域代码',
    EN_NAME         varchar(255) comment '英文名称',
    ESTABLISH_DATE  date comment '成立时间',
    GRADE_CODE      varchar(255) comment '等级编码',
    GRADE_CODE_NAME varchar(255) comment '等级名称',
    ZIP_CODE        varchar(255) comment '邮政编码',
    primary key (ID)
) comment='部门实体表' engine=InnoDB;
create table Y9_ORG_DEPARTMENT_PROP
(
    ID          varchar(38) not null comment '唯一标示',
    CREATE_TIME datetime(6) comment '创建时间',
    UPDATE_TIME datetime(6) comment '更新时间',
    CATEGORY    integer     not null comment '类别',
    DEPT_ID     varchar(50) not null comment '部门唯一标示',
    ORG_BASE_ID varchar(50) not null comment '组织唯一标示',
    TAB_INDEX   integer     not null comment '排序号',
    primary key (ID)
) comment='部门信息配置表' engine=InnoDB;
create table Y9_ORG_GROUP
(
    ID          varchar(38)                  not null comment 'UUID字段',
    CREATE_TIME datetime(6) comment '创建时间',
    UPDATE_TIME datetime(6) comment '更新时间',
    CUSTOM_ID   varchar(255) comment '自定义id',
    DESCRIPTION varchar(255) comment '描述',
    DISABLED    integer     default 0        not null comment '是否可用',
    DN          varchar(2000) comment '由name组成的父子关系列表(倒序)，之间用逗号分隔',
    GUID_PATH   varchar(400) comment '由ID组成的父子关系列表(正序)，之间用逗号分隔',
    NAME        varchar(255)                 not null comment '名称',
    ORG_TYPE    varchar(255)                 not null comment '组织类型',
    PARENT_ID   varchar(38) comment '父节点id',
    PROPERTIES  varchar(500) comment '扩展属性',
    TAB_INDEX   integer                      not null comment '排序号',
    TENANT_ID   varchar(38) comment '租户id',
    VERSION     integer comment '版本号,乐观锁',
    TYPE        varchar(10) default 'person' not null comment '类型：position、person',
    primary key (ID)
) comment='用户组表' engine=InnoDB;
create table Y9_ORG_JOB
(
    ID          varchar(38)  not null comment '主键id',
    CREATE_TIME datetime(6) comment '创建时间',
    UPDATE_TIME datetime(6) comment '更新时间',
    CODE        varchar(255) not null comment '数据代码',
    NAME        varchar(255) not null comment '名称',
    TAB_INDEX   integer      not null comment '排序号',
    primary key (ID)
) comment='职位表' engine=InnoDB;
create table Y9_ORG_MANAGER
(
    ID              varchar(38)       not null comment 'UUID字段',
    CREATE_TIME     datetime(6) comment '创建时间',
    UPDATE_TIME     datetime(6) comment '更新时间',
    CUSTOM_ID       varchar(255) comment '自定义id',
    DESCRIPTION     varchar(255) comment '描述',
    DISABLED        integer default 0 not null comment '是否可用',
    DN              varchar(2000) comment '由name组成的父子关系列表(倒序)，之间用逗号分隔',
    GUID_PATH       varchar(400) comment '由ID组成的父子关系列表(正序)，之间用逗号分隔',
    NAME            varchar(255)      not null comment '名称',
    ORG_TYPE        varchar(255)      not null comment '组织类型',
    PARENT_ID       varchar(38) comment '父节点id',
    PROPERTIES      varchar(500) comment '扩展属性',
    TAB_INDEX       integer           not null comment '排序号',
    TENANT_ID       varchar(38) comment '租户id',
    VERSION         integer comment '版本号,乐观锁',
    AVATOR          varchar(100) comment '头像',
    CHECK_CYCLE     integer comment '审查周期',
    CHECK_TIME      varchar(50) comment '审查时间',
    EMAIL           varchar(255) comment '电子邮箱',
    GLOBAL_MANAGER  integer default 0 not null comment '是否全局管理员',
    LOGIN_NAME      varchar(255)      not null comment '登录名',
    MANAGER_LEVEL   integer default 0 not null comment '管理员类型',
    MOBILE          varchar(255) comment '手机号码',
    MODIFY_PWD_TIME varchar(50) comment '修改密码时间',
    ORDERED_PATH    varchar(500) comment '排序序列号',
    PASSWORD        varchar(255) comment '登录密码',
    PWD_CYCLE       integer comment '修改密码周期（天）',
    SEX             integer default 1 not null comment '性别',
    USER_HOST_IP    varchar(150) comment '允许访问的客户端IP',
    primary key (ID)
) comment='三员表' engine=InnoDB;
create table Y9_ORG_OPTIONCLASS
(
    TYPE        varchar(255) not null comment '主键，类型名称',
    CREATE_TIME datetime(6) comment '创建时间',
    UPDATE_TIME datetime(6) comment '更新时间',
    NAME        varchar(255) not null comment '中文名称',
    primary key (TYPE)
) comment='字典类型表' engine=InnoDB;
create table Y9_ORG_OPTIONVALUE
(
    ID          varchar(38)  not null comment '主键id',
    CREATE_TIME datetime(6) comment '创建时间',
    UPDATE_TIME datetime(6) comment '更新时间',
    CODE        varchar(255) not null comment '数据代码',
    NAME        varchar(255) not null comment '主键名称',
    TAB_INDEX   integer      not null comment '排序号',
    TYPE        varchar(255) not null comment '字典类型',
    primary key (ID)
) comment='字典数据表' engine=InnoDB;
create table Y9_ORG_ORGANIZATION
(
    ID                varchar(38)       not null comment 'UUID字段',
    CREATE_TIME       datetime(6) comment '创建时间',
    UPDATE_TIME       datetime(6) comment '更新时间',
    CUSTOM_ID         varchar(255) comment '自定义id',
    DESCRIPTION       varchar(255) comment '描述',
    DISABLED          integer default 0 not null comment '是否可用',
    DN                varchar(2000) comment '由name组成的父子关系列表(倒序)，之间用逗号分隔',
    GUID_PATH         varchar(400) comment '由ID组成的父子关系列表(正序)，之间用逗号分隔',
    NAME              varchar(255)      not null comment '名称',
    ORG_TYPE          varchar(255)      not null comment '组织类型',
    PARENT_ID         varchar(38) comment '父节点id',
    PROPERTIES        varchar(500) comment '扩展属性',
    TAB_INDEX         integer           not null comment '排序号',
    TENANT_ID         varchar(38) comment '租户id',
    VERSION           integer comment '版本号,乐观锁',
    EN_NAME           varchar(255) comment '英文名称',
    ORGANIZATION_CODE varchar(255) comment '组织机构代码',
    ORGANIZATION_TYPE varchar(255) comment '组织机构类型',
    VIRTUALIZED       integer default 0 not null comment '类型:0=实体组织，1=虚拟组织',
    primary key (ID)
) comment='组织机构实体表' engine=InnoDB;
create table Y9_ORG_ORGBASES_ROLES
(
    ID            integer           not null auto_increment comment '主键',
    CREATE_TIME   datetime(6) comment '创建时间',
    UPDATE_TIME   datetime(6) comment '更新时间',
    NEGATIVE      integer default 0 not null comment '是否为负角色关联',
    ORG_ID        varchar(38)       not null comment '人员或部门组织机构等唯一标识',
    ORG_ORDER     integer           not null comment '关联排序号',
    ORG_TYPE      varchar(255) comment '组织类型',
    ORG_PARENT_ID varchar(255) comment '父节点唯一标识',
    ROLE_ID       varchar(38)       not null comment '角色id',
    primary key (ID)
) comment='组织节点与角色关联表' engine=InnoDB;
create table Y9_ORG_PERMISSION_DATAFIELD
(
    ID              varchar(38) not null comment '主键',
    CREATE_TIME     datetime(6) comment '创建时间',
    UPDATE_TIME     datetime(6) comment '更新时间',
    APP_ID          varchar(38) comment '应用id',
    DESCRIPTION     varchar(255) comment '描述',
    HIDDEN_FIELDS   varchar(1000) comment '不可见字段列表（之间用逗号隔开）',
    NAME            varchar(255) comment '名称',
    READONLY_FIELDS varchar(1000) comment '只读字段列表（之间用逗号隔开）',
    TAB_INDEX       integer     not null comment '排序号',
    primary key (ID)
) comment='数据列权限表' engine=InnoDB;
create table Y9_ORG_PERMISSION_DATAROW
(
    ID           varchar(38) not null comment '主键',
    CREATE_TIME  datetime(6) comment '创建时间',
    UPDATE_TIME  datetime(6) comment '更新时间',
    APP_ID       varchar(38) comment '应用id',
    DESCRIPTION  varchar(255) comment '描述',
    LIMIT_CLAUSE varchar(2000) comment '数据范围',
    NAME         varchar(255) comment '名称',
    TAB_INDEX    integer     not null comment '排序号',
    primary key (ID)
) comment='数据行权限表' engine=InnoDB;
create table Y9_ORG_PERSON
(
    ID             varchar(38)                       not null comment 'UUID字段',
    CREATE_TIME    datetime(6) comment '创建时间',
    UPDATE_TIME    datetime(6) comment '更新时间',
    CUSTOM_ID      varchar(255) comment '自定义id',
    DESCRIPTION    varchar(255) comment '描述',
    DISABLED       integer      default 0            not null comment '是否可用',
    DN             varchar(2000) comment '由name组成的父子关系列表(倒序)，之间用逗号分隔',
    GUID_PATH      varchar(400) comment '由ID组成的父子关系列表(正序)，之间用逗号分隔',
    NAME           varchar(255)                      not null comment '名称',
    ORG_TYPE       varchar(255)                      not null comment '组织类型',
    PARENT_ID      varchar(38) comment '父节点id',
    PROPERTIES     varchar(500) comment '扩展属性',
    TAB_INDEX      integer                           not null comment '排序号',
    TENANT_ID      varchar(38) comment '租户id',
    VERSION        integer comment '版本号,乐观锁',
    AVATOR         varchar(100) comment '头像',
    CAID           varchar(255) comment 'CA认证码',
    EMAIL          varchar(255) comment '电子邮箱',
    LOGIN_NAME     varchar(255)                      not null comment '登录名',
    MOBILE         varchar(255)                      not null comment '手机号码',
    OFFICE_ADDRESS varchar(255) comment '办公地址',
    OFFICE_FAX     varchar(255) comment '办公传真',
    OFFICE_PHONE   varchar(255) comment '办公电话',
    OFFICIAL       integer comment '是否在编',
    OFFICIAL_TYPE  varchar(255) comment '编制类型',
    ORDERED_PATH   varchar(500) comment '排序序列号',
    ORIGINAL       integer      default 1            not null comment '0:添加的人员，1：新增的人员',
    ORIGINAL_ID    varchar(255) comment '原始人员id',
    PASSWORD       varchar(255) comment '登录密码',
    PERSON_TYPE    varchar(255) default 'deptPerson' not null comment '人员类型',
    SEX            integer      default 1            not null comment '性别',
    WEIXIN_ID      varchar(255) comment '人员绑定微信的唯一标识',
    primary key (ID)
) comment='人员表' engine=InnoDB;
create table Y9_ORG_PERSON_CUSTOMGROUP
(
    ID          varchar(38) not null comment '主键',
    CREATE_TIME datetime(6) comment '创建时间',
    UPDATE_TIME datetime(6) comment '更新时间',
    CUSTOM_ID   varchar(255) comment '自定义id',
    GROUP_NAME  varchar(50) not null comment '群组名称',
    USER_ID     varchar(38) comment '用户id',
    USER_NAME   varchar(255) comment '用户名称',
    SHARE_ID    varchar(38) comment '分享人Id',
    SHARE_NAME  varchar(255) comment '分享人',
    TAB_INDEX   integer     not null comment '排序字段',
    TENANT_ID   varchar(38) comment '租户id',
    primary key (ID)
) comment='自定义群组' engine=InnoDB;
create table Y9_ORG_PERSON_CUSTOMGROUP_MEMBER
(
    ID          varchar(38)  not null comment '主键',
    CREATE_TIME datetime(6) comment '创建时间',
    UPDATE_TIME datetime(6) comment '更新时间',
    GROUP_ID    varchar(38) comment '所在群组id',
    MEMBER_ID   varchar(38)  not null comment '成员id',
    MEMBER_NAME varchar(255) not null comment '成员名称',
    MEMBER_TYPE varchar(255) comment '成员类型',
    PARENT_ID   varchar(38) comment '所在组织架构父节点id',
    SEX         integer comment '性别',
    TAB_INDEX   integer      not null comment '排序',
    primary key (ID)
) comment='自定义群组成员表' engine=InnoDB;
create table Y9_ORG_PERSON_EXT
(
    PERSON_ID        varchar(38)       not null comment '人员ID',
    CREATE_TIME      datetime(6) comment '创建时间',
    UPDATE_TIME      datetime(6) comment '更新时间',
    BIRTHDAY         date comment '出生年月',
    CITY             varchar(255) comment '居住城市',
    COUNTRY          varchar(255) comment '居住国家',
    EDUCATION        varchar(255) comment '学历',
    HOME_ADDRESS     varchar(255) comment '家庭地址',
    HOME_PHONE       varchar(255) comment '家庭电话',
    ID_NUM           varchar(255) comment '证件号码',
    ID_TYPE          varchar(255) comment '证件类型',
    MARITAL_STATUS   integer default 0 not null comment '婚姻状况',
    NAME             varchar(255)      not null comment '登录名，拥于查找',
    PHOTO            longblob comment '照片',
    POLITICAL_STATUS varchar(255) comment '政治面貌',
    PROFESSIONAL     varchar(255) comment '专业',
    PROVINCE         varchar(255) comment '人员籍贯',
    SIGN             longblob comment '签名',
    WORK_TIME        date comment '入职时间',
    primary key (PERSON_ID)
) comment='人员信息扩展表' engine=InnoDB;
create table Y9_ORG_PERSONS_GROUPS
(
    ID           integer     not null auto_increment comment '主键',
    CREATE_TIME  datetime(6) comment '创建时间',
    UPDATE_TIME  datetime(6) comment '更新时间',
    GROUP_ID     varchar(38) not null comment '用户组id',
    GROUP_ORDER  integer     not null comment '用户组排序号',
    PERSON_ID    varchar(38) not null comment '人员ID',
    PERSON_ORDER integer     not null comment '人员排序号',
    primary key (ID)
) comment='人员用户组关联表' engine=InnoDB;
create table Y9_ORG_PERSONS_POSITIONS
(
    ID             integer           not null auto_increment comment '主键',
    CREATE_TIME    datetime(6) comment '创建时间',
    UPDATE_TIME    datetime(6) comment '更新时间',
    PERSON_ID      varchar(38)       not null comment '人员ID',
    PERSON_ORDER   integer default 0 not null comment '人员排序号',
    POSITION_ID    varchar(38)       not null comment '岗位ID',
    POSITION_ORDER integer default 0 not null comment '岗位排序号',
    primary key (ID)
) comment='人员岗位关联表' engine=InnoDB;
create table Y9_ORG_PERSONS_RESOURCES
(
    ID                   integer     not null auto_increment comment '主键id',
    CREATE_TIME          datetime(6) comment '创建时间',
    UPDATE_TIME          datetime(6) comment '更新时间',
    APP_ID               varchar(38) not null comment '应用id',
    APP_NAME             varchar(255) comment '应用名称',
    AUTHORITY            integer     not null comment '权限类型',
    AUTHORIZATION_ID     varchar(38) comment '权限配置id',
    INHERIT              integer     not null comment '资源是否为继承上级节点的权限。冗余字段，纯显示用',
    PARENT_RESOURCE_ID   varchar(38) comment '父资源id',
    RESOURCE_CUSTOM_ID   varchar(38) comment '资源自定义id',
    RESOURCE_DESCRIPTION varchar(255) comment '描述',
    RESOURCE_ID          varchar(38) not null comment '资源id',
    RESOURCE_NAME        varchar(255) comment '资源名称',
    RESOURCE_TAB_INDEX   integer     not null comment '排序',
    RESOURCE_TYPE        integer     not null comment '资源类型：0=应用，1=菜单，2=操作',
    RESOURCE_URL         varchar(255) comment '资源URL',
    SYSTEM_CN_NAME       varchar(255) comment '系统中文名称',
    SYSTEM_ID            varchar(38) not null comment '系统id',
    SYSTEM_NAME          varchar(255) comment '系统英文名称',
    TENANT_ID            varchar(38) comment '租户id',
    PERSON_ID            varchar(38) not null comment '身份(人员)唯一标识',
    primary key (ID)
) comment='人员与（资源、权限）关系表' engine=InnoDB;
create table Y9_ORG_PERSONS_ROLES
(
    ID             integer     not null auto_increment comment '主键id',
    CREATE_TIME    datetime(6) comment '创建时间',
    UPDATE_TIME    datetime(6) comment '更新时间',
    APP_ID         varchar(38) comment '应用id',
    APP_NAME       varchar(255) comment '应用名称',
    DEPARTMENT_ID  varchar(38) not null comment '部门id',
    DESCRIPTION    varchar(255) comment '描述',
    ROLE_CUSTOM_ID varchar(38) comment '角色自定义id',
    ROLE_ID        varchar(38) not null comment '角色id',
    ROLE_NAME      varchar(255) comment '角色名称',
    SYSTEM_CN_NAME varchar(255) comment '系统中文名称',
    SYSTEM_ID      varchar(38) comment '系统id',
    SYSTEM_NAME    varchar(255) comment '系统英文名称',
    TENANT_ID      varchar(38) comment '租户id',
    PERSON_ID      varchar(38) not null comment '身份(人员)唯一标识',
    primary key (ID)
) comment='人员与角色关系表' engine=InnoDB;
create table Y9_ORG_POSITION
(
    ID              varchar(38)       not null comment 'UUID字段',
    CREATE_TIME     datetime(6) comment '创建时间',
    UPDATE_TIME     datetime(6) comment '更新时间',
    CUSTOM_ID       varchar(255) comment '自定义id',
    DESCRIPTION     varchar(255) comment '描述',
    DISABLED        integer default 0 not null comment '是否可用',
    DN              varchar(2000) comment '由name组成的父子关系列表(倒序)，之间用逗号分隔',
    GUID_PATH       varchar(400) comment '由ID组成的父子关系列表(正序)，之间用逗号分隔',
    NAME            varchar(255)      not null comment '名称',
    ORG_TYPE        varchar(255)      not null comment '组织类型',
    PARENT_ID       varchar(38) comment '父节点id',
    PROPERTIES      varchar(500) comment '扩展属性',
    TAB_INDEX       integer           not null comment '排序号',
    TENANT_ID       varchar(38) comment '租户id',
    VERSION         integer comment '版本号,乐观锁',
    CAPACITY        integer default 1 not null comment '岗位容量，默认容量为1，即一人一岗',
    DUTY            varchar(255) comment '职务',
    DUTY_LEVEL      integer default 0 not null comment '职务级别',
    DUTY_LEVEL_NAME varchar(255) comment '职级名称',
    DUTY_TYPE       varchar(255) comment '职务类型',
    EXCLUSIVE_IDS   varchar(500) comment '互斥的岗位Id列表，之间用逗号分割',
    HEAD_COUNT      integer default 0 not null comment '岗位当前人数，小于或等于岗位容量',
    JOB_ID          varchar(38)       not null comment '职位id',
    ORDERED_PATH    varchar(500) comment '排序序列号',
    primary key (ID)
) comment='岗位表' engine=InnoDB;
create table Y9_ORG_POSITIONS_GROUPS
(
    ID             integer     not null auto_increment comment '主键',
    CREATE_TIME    datetime(6) comment '创建时间',
    UPDATE_TIME    datetime(6) comment '更新时间',
    GROUP_ID       varchar(38) not null comment '用户组id',
    GROUP_ORDER    integer     not null comment '用户组排序号',
    POSITION_ID    varchar(38) not null comment '岗位ID',
    POSITION_ORDER integer     not null comment '人员排序号',
    primary key (ID)
) comment='岗位与岗位组关联表' engine=InnoDB;
create table Y9_ORG_POSITIONS_RESOURCES
(
    ID                   integer     not null auto_increment comment '主键id',
    CREATE_TIME          datetime(6) comment '创建时间',
    UPDATE_TIME          datetime(6) comment '更新时间',
    APP_ID               varchar(38) not null comment '应用id',
    APP_NAME             varchar(255) comment '应用名称',
    AUTHORITY            integer     not null comment '权限类型',
    AUTHORIZATION_ID     varchar(38) comment '权限配置id',
    INHERIT              integer     not null comment '资源是否为继承上级节点的权限。冗余字段，纯显示用',
    PARENT_RESOURCE_ID   varchar(38) comment '父资源id',
    RESOURCE_CUSTOM_ID   varchar(38) comment '资源自定义id',
    RESOURCE_DESCRIPTION varchar(255) comment '描述',
    RESOURCE_ID          varchar(38) not null comment '资源id',
    RESOURCE_NAME        varchar(255) comment '资源名称',
    RESOURCE_TAB_INDEX   integer     not null comment '排序',
    RESOURCE_TYPE        integer     not null comment '资源类型：0=应用，1=菜单，2=操作',
    RESOURCE_URL         varchar(255) comment '资源URL',
    SYSTEM_CN_NAME       varchar(255) comment '系统中文名称',
    SYSTEM_ID            varchar(38) not null comment '系统id',
    SYSTEM_NAME          varchar(255) comment '系统英文名称',
    TENANT_ID            varchar(38) comment '租户id',
    POSITION_ID          varchar(38) not null comment '身份(岗位)唯一标识',
    primary key (ID)
) comment='岗位与（资源、权限）关系表' engine=InnoDB;
create table Y9_ORG_POSITIONS_ROLES
(
    ID             integer     not null auto_increment comment '主键id',
    CREATE_TIME    datetime(6) comment '创建时间',
    UPDATE_TIME    datetime(6) comment '更新时间',
    APP_ID         varchar(38) comment '应用id',
    APP_NAME       varchar(255) comment '应用名称',
    DEPARTMENT_ID  varchar(38) not null comment '部门id',
    DESCRIPTION    varchar(255) comment '描述',
    ROLE_CUSTOM_ID varchar(38) comment '角色自定义id',
    ROLE_ID        varchar(38) not null comment '角色id',
    ROLE_NAME      varchar(255) comment '角色名称',
    SYSTEM_CN_NAME varchar(255) comment '系统中文名称',
    SYSTEM_ID      varchar(38) comment '系统id',
    SYSTEM_NAME    varchar(255) comment '系统英文名称',
    TENANT_ID      varchar(38) comment '租户id',
    POSITION_ID    varchar(38) not null comment '身份(岗位)唯一标识',
    primary key (ID)
) comment='岗位与角色关系表' engine=InnoDB;
create table Y9_ORGBASE_DELETED
(
    ID           varchar(38)  not null comment '主键',
    CREATE_TIME  datetime(6) comment '创建时间',
    UPDATE_TIME  datetime(6) comment '更新时间',
    DN           varchar(2000) comment '名称组成的父子关系，之间以逗号分割',
    JSON_CONTENT longtext comment '内容',
    OPERATOR     varchar(30) comment '操作者',
    ORG_ID       varchar(38) comment '组织id',
    ORG_TYPE     varchar(255) not null comment '组织类型',
    PARENT_ID    varchar(38) comment '父节点的Id',
    primary key (ID)
) comment='删除的组织表' engine=InnoDB;
create table Y9_ORGBASE_MOVED
(
    ID             varchar(38)       not null comment '主键',
    CREATE_TIME    datetime(6) comment '创建时间',
    UPDATE_TIME    datetime(6) comment '更新时间',
    FINISHED       integer default 0 not null comment '工作交接是否完成',
    OPERATOR       varchar(30) comment '操作者',
    ORG_ID         varchar(38) comment '组织id',
    ORG_TYPE       varchar(255)      not null comment '组织类型',
    PARENT_ID_FROM varchar(38) comment '原来的父节点Id',
    PARENT_ID_TO   varchar(38) comment '目标父节点Id',
    primary key (ID)
) comment='移动的组织表' engine=InnoDB;
create index IDX53q7nmihwr3yfeu8ribdd6sb on Y9_ORG_AUTHORIZATION (TENANT_ID, PRINCIPAL_ID asc);
create index IDXrgw1eknlb0a7kg5xonypd52ag on Y9_ORG_AUTHORIZATION (TENANT_ID, RESOURCE_ID asc);
alter table Y9_ORG_AUTHORIZATION
    add constraint UKpv5gy69bjyoflhhexyix9d8gt unique (PRINCIPAL_ID, RESOURCE_ID, AUTHORITY);
alter table Y9_ORG_DEPARTMENT
    add constraint UK_3rame1rxiq6j18b20rqfj8eyn unique (GUID_PATH);
alter table Y9_ORG_GROUP
    add constraint UK_j3g3vc1ffuwb0l25xiwc0gue3 unique (GUID_PATH);
alter table Y9_ORG_MANAGER
    add constraint UK_cmcf8n9689uc3cxmj0ji2xdqf unique (GUID_PATH);
alter table Y9_ORG_ORGANIZATION
    add constraint UK_jlnficawh29fkypg7dj2cf4bg unique (GUID_PATH);
create index IDX1q0y1dxaypjqxoal65vshj214 on Y9_ORG_ORGBASES_ROLES (ROLE_ID, ORG_ID);
alter table Y9_ORG_PERSON
    add constraint UK_8k5uhooq866n687vvcv37ufrh unique (GUID_PATH);
alter table Y9_ORG_PERSONS_GROUPS
    add constraint UKbk6jbj6n5gui7fd6e2ljak95k unique (GROUP_ID, PERSON_ID);
alter table Y9_ORG_PERSONS_POSITIONS
    add constraint UKrty6axgf79bgsw4vxtki54i3c unique (POSITION_ID, PERSON_ID);
alter table Y9_ORG_PERSONS_RESOURCES
    add constraint UKn2p2pe8jbhlmtlxv8ixn8cw7d unique (PERSON_ID, RESOURCE_ID, AUTHORIZATION_ID, AUTHORITY);
alter table Y9_ORG_PERSONS_ROLES
    add constraint UKa1ns3b527lyr0sn45sk036ts9 unique (PERSON_ID, ROLE_ID);
alter table Y9_ORG_POSITION
    add constraint UK_dkllqwv7eq7trb5394pg0tm6g unique (GUID_PATH);
alter table Y9_ORG_POSITIONS_GROUPS
    add constraint UKtjxh3p9qk2s8we8mtrg2mxc9y unique (GROUP_ID, POSITION_ID);
alter table Y9_ORG_POSITIONS_RESOURCES
    add constraint UK5cps1b1fq199989ni2hcrlwmd unique (POSITION_ID, RESOURCE_ID, AUTHORIZATION_ID, AUTHORITY);
alter table Y9_ORG_POSITIONS_ROLES
    add constraint UK8w7r8ookrjq6rcgiy98k56aba unique (POSITION_ID, ROLE_ID);
