package org.jeecg.modules.business.order.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.Date;

/**
 * @Description: jsh_order_summary
 * @Author: jeecg-boot
 * @Date: 2022-02-04
 * @Version: V1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JshOrderSummaryMonthlyExportVo extends JshOrderSummaryExportVo {
    /**
     * 时间
     */
    @Excel(name = "时间", width = 15, format = "yyyy-MM")
    private Date time;
}
