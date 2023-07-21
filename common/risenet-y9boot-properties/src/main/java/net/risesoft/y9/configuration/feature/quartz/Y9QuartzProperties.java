package net.risesoft.y9.configuration.feature.quartz;

import lombok.Getter;
import lombok.Setter;

/**
 * 定时任务属性
 *
 * @author liansen
 * @date 2022/09/28
 */
@Getter
@Setter
public class Y9QuartzProperties {

    /**
     * 调度器池大小
     */
    private int schedulerPoolSize = 5;

    /**
     * 任务池大小
     */
    private String taskPoolSize = "5-25";

    /**
     * 任务队列容量
     */
    private int taskQueueCapacity = 100;

    /**
     * minute大小
     */
    private String minuteSize = "0 0/5 * * * ?";

}
