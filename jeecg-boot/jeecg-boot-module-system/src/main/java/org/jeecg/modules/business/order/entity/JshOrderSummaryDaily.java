package org.jeecg.modules.business.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;

/**
 * @Description: jsh_order_summary_daily
 * @Author: jeecg-boot
 * @Date: 2022-02-04
 * @Version: V1.0
 */
@ApiModel(value = "jsh_order_summary_daily对象", description = "jsh_order_summary_daily")
@TableName("jsh_order_summary_daily")
public class JshOrderSummaryDaily extends JshOrderSummary {
}
