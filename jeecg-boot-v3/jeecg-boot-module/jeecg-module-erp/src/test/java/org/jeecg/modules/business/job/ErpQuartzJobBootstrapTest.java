package org.jeecg.modules.business.job;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ErpQuartzJobBootstrapTest {

    private static final String JOB_ID = "1511267922611875842";
    private static final String JOB_CLASS = "org.jeecg.modules.business.job.StatisticOrderJob";

    private Scheduler scheduler;
    private JdbcTemplate jdbcTemplate;
    private ErpQuartzJobBootstrap bootstrap;

    @BeforeEach
    void setUp() {
        scheduler = mock(Scheduler.class);
        jdbcTemplate = mock(JdbcTemplate.class);
        bootstrap = new ErpQuartzJobBootstrap(scheduler, jdbcTemplate);
    }

    private void givenRows(List<Map<String, Object>> rows) {
        when(jdbcTemplate.queryForList(anyString(), eq(JOB_CLASS))).thenReturn(rows);
    }

    private static Map<String, Object> row(Object status) {
        Map<String, Object> r = new HashMap<>();
        r.put("id", JOB_ID);
        r.put("job_class_name", " " + JOB_CLASS + " ");
        r.put("cron_expression", " 0 */10 * * * ? * ");
        r.put("parameter", null);
        r.put("status", status);
        return r;
    }

    @Test
    void registersJobWhenMissingFromScheduler() throws Exception {
        givenRows(Collections.singletonList(row(0)));
        when(scheduler.checkExists(JobKey.jobKey(JOB_ID))).thenReturn(false);

        bootstrap.run(null);

        ArgumentCaptor<JobDetail> job = ArgumentCaptor.forClass(JobDetail.class);
        ArgumentCaptor<Trigger> trigger = ArgumentCaptor.forClass(Trigger.class);
        verify(scheduler, times(1)).scheduleJob(job.capture(), trigger.capture());
        assertEquals(JobKey.jobKey(JOB_ID), job.getValue().getKey());
        assertEquals(StatisticOrderJob.class, job.getValue().getJobClass());
        assertEquals(true, job.getValue().getJobDataMap().containsKey("parameter"));
        assertEquals(TriggerKey.triggerKey(JOB_ID), trigger.getValue().getKey());
        assertEquals("0 */10 * * * ? *", ((CronTrigger) trigger.getValue()).getCronExpression());
    }

    @Test
    void leavesExistingJobAlone() throws Exception {
        givenRows(Collections.singletonList(row(0)));
        when(scheduler.checkExists(JobKey.jobKey(JOB_ID))).thenReturn(true);

        bootstrap.run(null);

        verify(scheduler, never()).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }

    @Test
    void skipsJobStoppedByAdmin() throws Exception {
        givenRows(Collections.singletonList(row(-1)));

        bootstrap.run(null);

        verify(scheduler, never()).checkExists(any(JobKey.class));
        verify(scheduler, never()).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }

    @Test
    void doesNothingWhenNoRow() throws Exception {
        givenRows(Collections.emptyList());

        bootstrap.run(null);

        verify(scheduler, never()).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }

    @Test
    void schedulerFailureDoesNotPropagate() throws Exception {
        givenRows(Collections.singletonList(row(0)));
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);
        when(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class))).thenThrow(new SchedulerException("boom"));

        assertDoesNotThrow(() -> bootstrap.run(null));
    }

    @Test
    void queryFailureDoesNotPropagate() {
        when(jdbcTemplate.queryForList(anyString(), eq(JOB_CLASS))).thenThrow(new RuntimeException("db down"));

        assertDoesNotThrow(() -> bootstrap.run(null));
    }
}
