package org.jeecg.modules.business.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.business.order.entity.JshOrderSummaryDaily;

import java.util.Date;

/**
 * @Description: jsh_order_summary
 * @Author: jeecg-boot
 * @Date: 2022-02-04
 * @Version: V1.0
 */
public interface IJshOrderSummaryService extends IService<JshOrderSummaryDaily> {

    /**
     * 统计每日订单数据
     */
    public void statisticDailyOrders(Date startTime, Integer deeps);

    /**
     * 统计每月订单数据
     */
    public void statisticMonthlyOrders(Date startTime, Integer deeps);
}
