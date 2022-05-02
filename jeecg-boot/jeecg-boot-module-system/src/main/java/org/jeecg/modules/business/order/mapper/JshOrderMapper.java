package org.jeecg.modules.business.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.business.order.entity.JshOrder;
import org.jeecg.modules.business.order.entity.JshOrderSummary;

import java.util.Date;
import java.util.List;

/**
 * @Description: jsh_order
 * @Author: jeecg-boot
 * @Date:   2022-02-04
 * @Version: V1.0
 */
public interface JshOrderMapper extends BaseMapper<JshOrder> {
    /**
     *  查询每日订单汇总数据
     * @param startTime
     * @param endTime
     * @return
     */
    List<JshOrderSummary> selectDailyOrderSummary(@Param("startTime") Date startTime, @Param("endTime") Date endTime);
}
