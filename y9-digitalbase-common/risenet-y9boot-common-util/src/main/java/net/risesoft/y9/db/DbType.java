package net.risesoft.y9.db;

/**
 * 拷贝阿里druid代码
 * 
 * @author DZJ
 * @date 2022/02/26
 */
public enum DbType {
    /** 其他/未知数据库类型 */
    other(1 << 0),
    /** jTDS 驱动（SQL Server、Sybase） */
    jtds(1 << 1),
    /** HSQLDB 数据库 */
    hsql(1 << 2),
    /** IBM DB2 数据库 */
    db2(1 << 3),
    /** PostgreSQL 数据库 */
    postgresql(1 << 4),

    /** Microsoft SQL Server 数据库 */
    sqlserver(1 << 5),
    /** Oracle 数据库 */
    oracle(1 << 6),
    /** MySQL 数据库 */
    mysql(1 << 7),
    /** MariaDB 数据库 */
    mariadb(1 << 8),
    /** Apache Derby 数据库 */
    derby(1 << 9),

    /** Apache Hive 数据库 */
    hive(1 << 10),
    /** H2 数据库 */
    h2(1 << 11),
    /** 达梦数据库 */
    dm(1 << 12), // dm.jdbc.driver.DmDriver
    /** 人大金仓数据库 */
    kingbase(1 << 13),
    /** 南大通用 GBase 数据库 */
    gbase(1 << 14),

    /** OceanBase 数据库 */
    oceanbase(1 << 15),
    /** IBM Informix 数据库 */
    informix(1 << 16),
    /** 阿里云 ODPS（MaxCompute） */
    odps(1 << 17),
    /** Teradata 数据库 */
    teradata(1 << 18),
    /** Apache Phoenix 数据库 */
    phoenix(1 << 19),

    /** EnterpriseDB（PostgreSQL 衍生版）数据库 */
    edb(1 << 20),
    /** Apache Kylin 数据库 */
    kylin(1 << 21), // org.apache.kylin.jdbc.Driver
    /** SQLite 数据库 */
    sqlite(1 << 22),
    /** 阿里云 AnalyticDB（ADS） */
    ads(1 << 23),
    /** Presto 数据库 */
    presto(1 << 24),

    /** Elasticsearch */
    elastic_search(1 << 25), // com.alibaba.xdriver.elastic.jdbc.ElasticDriver
    /** Apache HBase */
    hbase(1 << 26),
    /** 阿里云 DRDS 分布式关系型数据库 */
    drds(1 << 27),

    /** ClickHouse 数据库 */
    clickhouse(1 << 28),
    /** 阿里 Blink */
    blink(1 << 29),
    /** 蚂蚁 AntSpark */
    antspark(1 << 30),
    /** OceanBase Oracle 兼容模式 */
    oceanbase_oracle(1 << 31),
    /** 阿里云 PolarDB */
    polardb(1 << 32),

    /** 阿里云 Oracle 数据库 */
    ali_oracle(1 << 33),
    /** 模拟数据库，仅用于测试 */
    mock(1 << 34),
    /** Sybase 数据库 */
    sybase(1 << 35),
    /** 瀚高数据库 */
    highgo(1 << 36),
    /**
     * 非常成熟的开源mpp数据库
     */
    greenplum(1 << 37),
    /**
     * 华为的mpp数据库
     */
    gaussdb(1 << 38),

    /** Trino 数据库（Presto 分支） */
    trino(1 << 39),

    /** Ingres 数据库（未启用，掩码为 0） */
    ingres(0),
    /** IBM Cloudscape 数据库（未启用，掩码为 0） */
    cloudscape(0),
    /** Oracle TimesTen 内存数据库（未启用，掩码为 0） */
    timesten(0),
    /** IBM AS/400 数据库（未启用，掩码为 0） */
    as400(0),
    /** SAP DB 数据库（未启用，掩码为 0） */
    sapdb(0),
    /** kdb 数据库（未启用，掩码为 0） */
    kdb(0),
    /** log4jdbc 代理驱动（未启用，掩码为 0） */
    log4jdbc(0),
    /** 虚谷数据库（未启用，掩码为 0） */
    xugu(0),
    /** Firebird 数据库（未启用，掩码为 0） */
    firebirdsql(0),
    /** JSQLConnect 驱动（未启用，掩码为 0） */
    JSQLConnect(0),
    /** JTurbo 驱动（未启用，掩码为 0） */
    JTurbo(0),
    /** InterBase 数据库（未启用，掩码为 0） */
    interbase(0),
    /** PointBase 数据库（未启用，掩码为 0） */
    pointbase(0),
    /** edbc 数据库类型（未启用，掩码为 0） */
    edbc(0),
    /** Mimer SQL 数据库（未启用，掩码为 0） */
    mimer(0),

    ;

    /**
     * 将多个数据库类型的掩码合并为一个 long 值
     *
     * @param types 数据库类型
     * @return 合并后的掩码值
     */
    public static long of(DbType... types) {
        long value = 0;

        for (DbType type : types) {
            value |= type.mask;
        }

        return value;
    }

    /**
     * 根据名称解析数据库类型
     *
     * @param name 数据库类型名称
     * @return 匹配的数据库类型，无法匹配时返回 {@code null}
     */
    public static DbType of(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        if ("aliyun_ads".equalsIgnoreCase(name)) {
            return ads;
        }

        try {
            return valueOf(name);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 数据库类型掩码
     */
    public final long mask;

    /**
     * 名称的 FNV-1a 64 位哈希值
     */
    public final long hashCode64;

    private DbType(long mask) {
        this.mask = mask;
        this.hashCode64 = FnvHash.hashCode64(name());
    }

    public final boolean equals(String other) {
        return this == of(other);
    }
}
