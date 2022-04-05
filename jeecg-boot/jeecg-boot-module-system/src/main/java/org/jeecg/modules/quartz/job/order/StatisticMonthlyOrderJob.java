package org.jeecg.modules.quartz.job.order;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.business.order.service.IJshOrderSummaryService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.annotation.Resource;

/**
 * @author yq
 * @description 订单月数据统计任务
 * @date 2022/4/5 15:50
 */
@Slf4j
public class StatisticMonthlyOrderJob implements Job {
    @Resource
    IJshOrderSummaryService jshOrderSummaryService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        long start = System.currentTimeMillis();
        log.info("Job {}, start statistic monthly orders", jobExecutionContext.getJobDetail().getKey());
        jshOrderSummaryService.statisticMonthlyOrders(DateUtils.getTodayBegin(), 0);
        log.info("Job {}, statistic monthly orders end, executed time: {}ms", jobExecutionContext.getJobDetail().getKey(), System.currentTimeMillis() - start);
    }
}
