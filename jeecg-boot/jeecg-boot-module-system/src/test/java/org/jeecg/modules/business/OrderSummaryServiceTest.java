package org.jeecg.modules.business;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.JeecgSystemApplication;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.business.order.service.IJshOrderSummaryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author yq
 * @description
 * @date 2022/8/3 17:23
 */
@Slf4j
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
@SpringBootTest(classes = JeecgSystemApplication.class)
public class OrderSummaryServiceTest {
    @Resource
    IJshOrderSummaryService jshOrderSummaryService;
    @Test
    public void statisticOrders() {
        long start = System.currentTimeMillis();
        jshOrderSummaryService.statisticDailyOrders(DateUtils.getTodayBegin(), 0);
        jshOrderSummaryService.statisticMonthlyOrders(DateUtils.getTodayMonthBegin(), 0);
        log.info("Statistic orders end, executed time: {}ms", System.currentTimeMillis() - start);
    }
}
