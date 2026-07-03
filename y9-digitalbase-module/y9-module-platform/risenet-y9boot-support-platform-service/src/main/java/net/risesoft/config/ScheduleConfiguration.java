package net.risesoft.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import lombok.RequiredArgsConstructor;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import net.risesoft.scheduled.ScheduledTask;
import net.risesoft.y9.configuration.app.y9platform.Y9PlatformProperties;
import net.risesoft.y9.configuration.app.y9platform.schedule.ScheduleProperties;

/**
 * 定时任务配置
 *
 * @author shidaobang
 * @date 2026/03/09
 */
@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT2H")
@RequiredArgsConstructor
public class ScheduleConfiguration implements SchedulingConfigurer {

    private final ScheduledTask scheduledTask;
    private final Y9PlatformProperties y9PlatformProperties;

    // @Bean
    // public LockProvider lockProvider(RedisConnectionFactory connectionFactory) {
    // return new RedisLockProvider(connectionFactory);
    // }

    @Bean
    public LockProvider lockProvider(@Qualifier("y9PublicDS") DataSource dataSource) {
        return new JdbcTemplateLockProvider(JdbcTemplateLockProvider.Configuration.builder()
            .withJdbcTemplate(new JdbcTemplate(dataSource))
            .usingDbTime()
            .build());
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ScheduleProperties scheduleProperties = y9PlatformProperties.getSchedule();
        taskRegistrar.addCronTask(scheduledTask::checkManagerLogReview,
            scheduleProperties.getCheckManagerLogReviewCron());
        taskRegistrar.addCronTask(scheduledTask::checkManagerPasswordModification,
            scheduleProperties.getCheckManagerPasswordModificationCron());
        taskRegistrar.addCronTask(scheduledTask::syncIdentityResource,
            scheduleProperties.getSyncIdentityResourceCron());
        taskRegistrar.addCronTask(scheduledTask::syncIdentityRole, scheduleProperties.getSyncIdentityRoleCron());
    }

}
