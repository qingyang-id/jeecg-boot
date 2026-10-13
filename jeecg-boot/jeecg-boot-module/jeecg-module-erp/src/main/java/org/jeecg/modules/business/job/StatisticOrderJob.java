package org.jeecg.modules.business.job;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.business.order.service.IJshOrderSummaryService;
import org.jeecg.modules.business.util.ErpDateUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import jakarta.annotation.Resource;

/**
 * @author yq
 * @description 订单日数据统计任务
 * @date 2022/4/5 15:50
 */
@Slf4j
public class StatisticOrderJob implements Job {
    @Resource
    IJshOrderSummaryService jshOrderSummaryService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        long start = System.currentTimeMillis();
        log.info("Job {}, start statistic orders", jobExecutionContext.getJobDetail().getKey());
        jshOrderSummaryService.statisticDailyOrders(ErpDateUtils.getTodayBegin(), 0);
        jshOrderSummaryService.statisticMonthlyOrders(ErpDateUtils.getTodayMonthBegin(), 0);
        log.info("Job {}, statistic orders end, executed time: {}ms", jobExecutionContext.getJobDetail().getKey(), System.currentTimeMillis() - start);
    }
}
