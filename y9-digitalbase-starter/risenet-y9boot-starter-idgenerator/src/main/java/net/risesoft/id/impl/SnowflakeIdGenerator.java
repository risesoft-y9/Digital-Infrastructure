package net.risesoft.id.impl;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.id.Y9IdGenerator;

import cn.hutool.core.util.IdUtil;

/**
 * 在twitter的snowflake算法实现基础上根据自身需求进行了调整
 * <p>
 * snowflake的原始结构如下(每部分用-分开):<br>
 * <pre>
 * 符号位（1bit）- 时间戳相对值（41bit）- 数据中心标志（5bit）- 机器标志（5bit）- 递增序号（12bit）
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 * </pre>
 * 
 * <p>
 * 调整后的snowflake结构如下(每部分用-分开):<br>
 * <pre>
 * 符号位（1bit）- 时间戳相对值（41bit）- 机器标志（10bit）- 递增序号（12bit）
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 0000000000 - 000000000000
 * </pre>
 * <p>
 *
 * @author allen shen
 * @date 2019/10/24
 */
@Slf4j
public class SnowflakeIdGenerator implements Y9IdGenerator {
    /**
     * 起始的时间戳
     */
    private final static long START_STAMP = 1320076800000L;
    
    /**
     * 每一部分占用的位数 序列号占用的位数
     */
    private final static long DATA_CENTER_BIT = 5;
    private final static long WORKER_BIT = 5;
    private final static long MACHINE_BIT = DATA_CENTER_BIT + WORKER_BIT;
    private final static long SEQUENCE_BIT = 12;
    
    /**
     * 每一部分的最大值
     */
    private final static long MAX_DATA_CENTER_NUM = -1L ^ (-1L << DATA_CENTER_BIT);
    private final static long MAX_WORKER_NUM = -1L ^ (-1L << WORKER_BIT);
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

    /**
     * 每一部分向左的位移
     */
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long TIME_STAMP_LEFT = SEQUENCE_BIT + MACHINE_BIT;

    /** 机器标识 */
    private final long machineId;
    /** 序列号 */
    private long sequence = 0L;
    /** 上一次时间戳 */
    private long lastStamp = -1L;

    public SnowflakeIdGenerator(long machineId) {
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }
        this.machineId = machineId;
    }

    public SnowflakeIdGenerator() {
        this(getMachineId());
    }

    /**
     * 依赖的服务集群部署时，极小概率生成重复 machineId
     * @return
     */
    private static long getMachineId() {
        long dataCenterId = IdUtil.getDataCenterId(MAX_DATA_CENTER_NUM);
        long workerId = IdUtil.getWorkerId(dataCenterId, MAX_WORKER_NUM);
        return dataCenterId | workerId << WORKER_BIT;
    }

    private long getNewStamp() {
        return System.currentTimeMillis();
    }

    @Override
    public String getNextId() {
        Long id = nextId();
        return Long.toString(id);
    }

    private long getNextMill() {
        long mill = getNewStamp();
        while (mill <= lastStamp) {
            try {
                Thread.sleep(lastStamp - getNewStamp());
            } catch (InterruptedException e) {
                LOGGER.warn(e.getMessage(), e);
            }
            mill = getNewStamp();
        }
        return mill;
    }

    /**
     * 产生下一个ID
     *
     * @return
     */
    private synchronized long nextId() {
        long currStamp = getNewStamp();
        if (currStamp < lastStamp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }

        if (currStamp == lastStamp) {
            // 相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 同一毫秒的序列数已经达到最大
            if (sequence == 0L) {
                currStamp = getNextMill();
            }
        } else {
            // 不同毫秒内，序列号置为0
            sequence = 0L;
        }

        lastStamp = currStamp;
        // 时间戳 | 机器标识部分 | 序列号部分
        return (currStamp - START_STAMP) << TIME_STAMP_LEFT | machineId << MACHINE_LEFT | sequence;
    }
}