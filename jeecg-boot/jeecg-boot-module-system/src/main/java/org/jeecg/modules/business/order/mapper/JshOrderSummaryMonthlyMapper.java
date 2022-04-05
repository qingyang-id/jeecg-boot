package org.jeecg.modules.business.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.business.order.entity.JshOrderSummary;
import org.jeecg.modules.business.order.entity.JshOrderSummaryMonthly;

import java.util.List;

/**
 * @Description: jsh_order_summary_monthly
 * @Author: jeecg-boot
 * @Date:   2022-02-04
 * @Version: V1.0
 */
public interface JshOrderSummaryMonthlyMapper extends BaseMapper<JshOrderSummaryMonthly> {
    /**
     *  批量新增每日订单汇总数据
     * @param list
     * @return
     */
    public void batchInsertOrUpdate(@Param("list") List<JshOrderSummary> list);
}
