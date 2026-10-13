package org.jeecg.modules.business.order.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.business.order.entity.JshOrderSummary;
import org.jeecg.modules.business.order.entity.JshOrderSummaryDaily;

import java.util.Date;
import java.util.List;

/**
 * @Description: jsh_order_summary_daily
 * @Author: jeecg-boot
 * @Date:   2022-02-04
 * @Version: V1.0
 */
public interface JshOrderSummaryDailyMapper extends BaseMapper<JshOrderSummaryDaily> {
    /**
     *  查询每日订单汇总数据
     * @param startTime
     * @param endTime
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    public List<JshOrderSummary> selectOrderSummary(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     *  批量新增每日订单汇总数据
     * @param list
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    public void batchInsertOrUpdate(@Param("list") List<JshOrderSummary> list);

    /**
     *  批量初始化数据
     * @param time
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    public void batchResetByTime(Date time);
}
