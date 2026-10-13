package org.jeecg.modules.business;

import org.jeecg.modules.business.job.StatisticOrderJob;
import org.jeecg.modules.business.order.entity.JshOrderSummary;
import org.jeecg.modules.business.order.mapper.JshOrderMapper;
import org.jeecg.modules.business.order.mapper.JshOrderSummaryDailyMapper;
import org.jeecg.modules.business.order.mapper.JshOrderSummaryMonthlyMapper;
import org.jeecg.modules.business.order.service.IJshOrderSummaryService;
import org.jeecg.modules.business.order.service.impl.JshOrderSummaryServiceImpl;
import org.jeecg.modules.business.util.ErpDateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 订单统计单元测试（不依赖数据库）：mapper 用 Mockito 替身，校验统计时间窗口、递归深度与汇总行。
 * 原 3.0 版本为 @SpringBootTest 直连 dev 库的冒烟测试，库级验证由 Task 4/5 的 parity 测试负责。
 */
public class OrderSummaryServiceTest {

    private static final SimpleDateFormat FMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private JshOrderMapper jshOrderMapper;
    private JshOrderSummaryDailyMapper dailyMapper;
    private JshOrderSummaryMonthlyMapper monthlyMapper;
    private JshOrderSummaryServiceImpl service;

    @BeforeEach
    void setUp() {
        jshOrderMapper = mock(JshOrderMapper.class);
        dailyMapper = mock(JshOrderSummaryDailyMapper.class);
        monthlyMapper = mock(JshOrderSummaryMonthlyMapper.class);
        service = new JshOrderSummaryServiceImpl();
        ReflectionTestUtils.setField(service, "jshOrderMapper", jshOrderMapper);
        ReflectionTestUtils.setField(service, "jshOrderSummaryDailyMapper", dailyMapper);
        ReflectionTestUtils.setField(service, "jshOrderSummaryMonthlyMapper", monthlyMapper);
        when(jshOrderMapper.selectDailyOrderSummary(any(), any())).thenReturn(new ArrayList<>());
        when(dailyMapper.selectOrderSummary(any(), any())).thenReturn(new ArrayList<>());
    }

    private static Date d(String s) throws Exception {
        return FMT.parse(s);
    }

    private static String f(Date date) {
        return FMT.format(date);
    }

    @Test
    void erpDateUtilsKeepsOldDateUtilsSemantics() throws Exception {
        Date t = d("2024-02-15 13:14:15.678");
        assertEquals("2024-02-15 00:00:00.000", f(ErpDateUtils.getDateBegin(t)));
        assertEquals("2024-02-15 23:59:59.999", f(ErpDateUtils.getDateEnd(t)));
        assertEquals("2024-02-01 00:00:00.000", f(ErpDateUtils.getMonthBegin(t)));
        assertEquals("2024-02-29 23:59:59.999", f(ErpDateUtils.getMonthEnd(t)));
        assertEquals(f(ErpDateUtils.getDateBegin(new Date())), f(ErpDateUtils.getTodayBegin()));
        assertEquals(f(ErpDateUtils.getMonthBegin(new Date())), f(ErpDateUtils.getTodayMonthBegin()));
        assertEquals("59秒", ErpDateUtils.formatDelayTime(59L));
        assertEquals("2分钟", ErpDateUtils.formatDelayTime(150L));
        assertEquals("1小时", ErpDateUtils.formatDelayTime(3600L));
        assertEquals("2天", ErpDateUtils.formatDelayTime(172800L));
    }

    @Test
    void dailyStatisticRecomputes45DaysWithFullDayWindows() throws Exception {
        Date start = d("2024-03-01 00:00:00.000");
        service.statisticDailyOrders(start, 0);

        ArgumentCaptor<Date> begin = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Date> end = ArgumentCaptor.forClass(Date.class);
        verify(jshOrderMapper, times(45)).selectDailyOrderSummary(begin.capture(), end.capture());
        verify(dailyMapper, times(45)).batchResetByTime(any());
        assertEquals("2024-03-01 00:00:00.000", f(begin.getAllValues().get(0)));
        assertEquals("2024-03-01 23:59:59.999", f(end.getAllValues().get(0)));
        assertEquals("2024-02-29 00:00:00.000", f(begin.getAllValues().get(1)));
        assertEquals("2024-01-17 00:00:00.000", f(begin.getAllValues().get(44)));
        verify(dailyMapper, never()).batchInsertOrUpdate(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void dailyStatisticAppendsTenantTotalRow() throws Exception {
        Date start = d("2024-03-01 00:00:00.000");
        JshOrderSummary a = new JshOrderSummary(null, null, 11L, 2L, 100L, 300L, 7L, 0, null, null);
        JshOrderSummary b = new JshOrderSummary(null, null, 12L, 3L, 200L, 500L, 7L, 0, null, null);
        when(jshOrderMapper.selectDailyOrderSummary(eq(start), any())).thenReturn(new ArrayList<>(Arrays.asList(a, b)));

        service.statisticDailyOrders(start, 0);

        ArgumentCaptor<List<JshOrderSummary>> rows = ArgumentCaptor.forClass(List.class);
        verify(dailyMapper, times(1)).batchInsertOrUpdate(rows.capture());
        List<JshOrderSummary> list = rows.getValue();
        assertEquals(3, list.size());
        JshOrderSummary total = list.get(2);
        assertEquals(5L, total.getTotalNum());
        assertEquals(300L, total.getTotalArea());
        assertEquals(800L, total.getTotalPrice());
        assertEquals(7L, total.getTenantId());
        assertEquals(start, total.getTime());
    }

    @Test
    void monthlyStatisticRecomputesCurrentAndPreviousMonth() throws Exception {
        Date start = d("2024-03-01 00:00:00.000");
        service.statisticMonthlyOrders(start, 0);

        ArgumentCaptor<Date> begin = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Date> end = ArgumentCaptor.forClass(Date.class);
        verify(dailyMapper, times(2)).selectOrderSummary(begin.capture(), end.capture());
        verify(monthlyMapper, times(2)).batchResetByTime(any());
        assertEquals("2024-03-31 23:59:59.999", f(end.getAllValues().get(0)));
        assertEquals("2024-02-01 00:00:00.000", f(begin.getAllValues().get(1)));
        assertEquals("2024-02-29 23:59:59.999", f(end.getAllValues().get(1)));
    }

    @Test
    void statisticOrderJobUsesTodayAndCurrentMonth() throws Exception {
        IJshOrderSummaryService summaryService = mock(IJshOrderSummaryService.class);
        StatisticOrderJob job = new StatisticOrderJob();
        ReflectionTestUtils.setField(job, "jshOrderSummaryService", summaryService);
        JobExecutionContext ctx = mock(JobExecutionContext.class);
        JobDetail detail = mock(JobDetail.class);
        when(ctx.getJobDetail()).thenReturn(detail);
        when(detail.getKey()).thenReturn(new JobKey("statisticOrderJob"));

        job.execute(ctx);

        verify(summaryService).statisticDailyOrders(ErpDateUtils.getTodayBegin(), 0);
        verify(summaryService).statisticMonthlyOrders(ErpDateUtils.getTodayMonthBegin(), 0);
        Mockito.verifyNoMoreInteractions(summaryService);
    }
}
