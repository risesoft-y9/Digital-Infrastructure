package net.risesoft.id.impl;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.id.Y9IdGenerator;

/**
 * twitter的snowflake算法实现
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
    private final static long SEQUENCE_BIT = 12;
    /** 机器标识占用的位数 */
    private final static long MACHINE_BIT = 10;

    /**
     * 每一部分的最大值
     */
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

    /**
     * 每一部分向左的位移
     */
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATA_CENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;

    /** 机器标识 */
    private long machineId;
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
        // 数据中心部分 | 机器标识部分 | 序列号部分
        return (currStamp - START_STAMP) << DATA_CENTER_LEFT | machineId << MACHINE_LEFT | sequence;
    }
}