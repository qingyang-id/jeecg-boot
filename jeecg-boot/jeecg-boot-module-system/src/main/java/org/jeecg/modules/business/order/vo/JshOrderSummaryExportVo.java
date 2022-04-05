package org.jeecg.modules.business.order.vo;

import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: jsh_order_summary
 * @Author: jeecg-boot
 * @Date: 2022-02-04
 * @Version: V1.0
 */
@Data
public class JshOrderSummaryExportVo {
    /**
     * 时间
     */
    @Excel(name = "时间", width = 15, format = "yyyy-MM-dd")
    private Date time;

    /**
     * 客户
     */
    @Excel(name = "客户", width = 15, dictTable = "jsh_customer", dicText = "name", dicCode = "id")
    private Long customerId;

    /**
     * 总订单数
     */
    @Excel(name = "总订单数", width = 15)
    private Long totalNum;

    /**
     * 总面积
     */
    @Excel(name = "总面积(m²)", width = 15)
    private BigDecimal totalArea;

    /**
     * 订单总额
     */
    @Excel(name = "订单总额(元)", width = 15)
    private BigDecimal totalPrice;
}
