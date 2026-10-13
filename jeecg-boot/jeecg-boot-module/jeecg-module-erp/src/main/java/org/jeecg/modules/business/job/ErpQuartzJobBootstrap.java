package org.jeecg.modules.business.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.TriggerBuilder;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 启动时把 ERP 订单统计任务注册到 Quartz（JDBC JobStore）。
 * 3.9.5 中仅写 sys_quartz_job 不会调度，需管理员调 /sys/quartzJob/resume；生产无管理员密码，
 * 故启动后对 status=0 且 Quartz 中不存在的行按 QuartzJobServiceImpl.schedulerAdd 同样方式注册。
 * status!=0（管理员手动停止）不处理；任何异常只记日志，不阻止启动。
 */
@Slf4j
@Component
public class ErpQuartzJobBootstrap implements ApplicationRunner {

    static final String JOB_CLASS = "org.jeecg.modules.business.job.StatisticOrderJob";
    private static final String SQL = "SELECT id, job_class_name, cron_expression, parameter, status FROM sys_quartz_job"
            + " WHERE job_class_name = ? AND del_flag = 0";

    private final Scheduler scheduler;
    private final JdbcTemplate jdbcTemplate;

    public ErpQuartzJobBootstrap(Scheduler scheduler, JdbcTemplate jdbcTemplate) {
        this.scheduler = scheduler;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<Map<String, Object>> rows;
        try {
            rows = jdbcTemplate.queryForList(SQL, JOB_CLASS);
        } catch (Exception e) {
            log.error("ERP quartz bootstrap: query sys_quartz_job failed", e);
            return;
        }
        for (Map<String, Object> row : rows) {
            String id = String.valueOf(row.get("id"));
            try {
                Object status = row.get("status");
                if (status == null || ((Number) status).intValue() != 0) {
                    log.info("ERP quartz bootstrap: job {} status={}, skip (stopped)", id, status);
                    continue;
                }
                if (scheduler.checkExists(JobKey.jobKey(id))) {
                    log.info("ERP quartz bootstrap: job {} already exists in scheduler, skip", id);
                    continue;
                }
                String jobClassName = String.valueOf(row.get("job_class_name")).trim();
                String cron = String.valueOf(row.get("cron_expression")).trim();
                Object parameter = row.get("parameter");
                @SuppressWarnings("unchecked")
                Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(jobClassName);
                JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(id)
                        .usingJobData("parameter", parameter == null ? null : parameter.toString()).build();
                CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(id)
                        .withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
                scheduler.scheduleJob(jobDetail, trigger);
                log.info("ERP quartz bootstrap: registered job {} ({}) cron '{}'", id, jobClassName, cron);
            } catch (Exception e) {
                log.error("ERP quartz bootstrap: register job {} failed", id, e);
            }
        }
    }
}
